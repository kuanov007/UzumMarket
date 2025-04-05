package ui;

import db.file.MyCustomNio;
import product.OrderProduct;
import product.OrderStatus;
import product.Product;
import user.Card;
import user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static db.MyBase.*;

public class UserUI {
    private static User currentUser;

    public static void getCurrentUser(User user) {
        currentUser = user;
        try {
            run();
        } catch (IOException ignored) {
        }
    }

    public static void run() throws IOException {
        Optional<Card> card = getCardByUserId(currentUser.getId());
        mainWhile:
        while (true) {
            switch (readInteger("""
                    [Name : %s | Balance: %s$]
                    1. Products;
                    2. My orders(Monitoring!);
                    3. History;
                    4. Fill balance;
                    5. Add a card;
                                        
                    0. log out;
                    >>>""".formatted(currentUser.getName(), card.isPresent() ? card.get().getBalance() :
                    "card not found!"))) {
                case 1 -> {
                    if (card.isEmpty()) {
                        System.err.println("""
                                You can't enter products catalog.
                                Cause: You haven't a card!
                                """);
                        return;
                    }

                    buyProducts(card.get());
                }
                case 2 -> monitoring();
                case 3 -> {
                    List<OrderProduct> myCompletedOrders = getAllMyHistory();
                    if (myCompletedOrders.isEmpty()) {
                        System.out.println("You haven't get any product yet!");
                        continue;
                    }
                    System.out.println("Your completed orders: ");
                    for (OrderProduct myCompletedOrder : myCompletedOrders) {
                        System.out.println(myCompletedOrder);
                    }
                    System.out.println();
                }
                case 4 -> {
                    Optional<Card> cardByUserId = getCardByUserId(currentUser.getId());
                    if (cardByUserId.isEmpty()) {
                        System.err.println("You don't have card, please add your card!");
                        continue;
                    }
                    long amount = readInteger("Enter a amount(0-200000) : ");
                    if (amount < 0 || amount > 200_000) {
                        System.err.println("Incorrect input, operation failed!");
                        continue;
                    }
                    Card myCard = cardByUserId.get();
                    myCard.setBalance(myCard.getBalance() + amount);
                    MyCustomNio.readFromList(cards);
                    System.out.println("Balance filled successfully!");
                }
                case 5 -> {
                    if (cards.stream().noneMatch(_card -> _card.getUserId().equals(currentUser.getId()))) {
                        while (true) {
                            String cardNumber = readLine("Enter a card number: ");
                            if (cards.stream().anyMatch(_card -> _card.getCardNumber().equals(cardNumber))) {
                                System.err.println("This card is using by someone!!!");
                                continue;
                            }
                            Card newCard = new Card(UUID.randomUUID(), cardNumber, new Random().nextLong(5000, 10000), currentUser.getId());
                            addACard(newCard);
                            card = Optional.of(newCard);
                            MyCustomNio.readFromList(cards);
                            logger.info(currentUser.getName() + " o'ziga karta qo'shdi");
                            break;
                        }
                        System.out.println("Card added successfully!");
                    } else {
                        System.err.println("You have a card and you can't add anymore!");
                    }
                }
                case 0 -> {
                    break mainWhile;
                }
            }
        }

    }

    private static List<OrderProduct> getAllMyHistory() {
        List<OrderProduct> orders = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getStatus() == OrderStatus.OLIBKETILDI &&
                orderProduct.getUserId().equals(currentUser.getId())) {
                orders.add(orderProduct);
            }
        }
        return orders;
    }

    private static void monitoring() {
        while (true) {
            Stream<OrderProduct> orderProductStream = orderProducts.stream().filter(
                    orderProduct -> (orderProduct.getUserId().equals(currentUser.getId())) &&
                                    !(orderProduct.getStatus().equals(OrderStatus.BUYURTMA_BEKOR_QILINDI) ||
                                      orderProduct.getStatus().equals(OrderStatus.OLIBKETILDI))
            );
            List<OrderProduct> myOrderedProducts = orderProductStream.toList();
            if (myOrderedProducts.isEmpty()) {
                System.out.println("You haven't ordered any thing!");
                return;
            }
            System.out.println("In there, you can see your order's status until you get it!");
            for (int i = 0; i < myOrderedProducts.size(); i++) {
                System.out.println((i + 1) + " -> " + myOrderedProducts.get(i));
            }
            System.out.println("0 -> back");
            int chosenOption = readInteger("Enter a index that if you want to cancel your order: ") - 1;
            if (chosenOption < 0 || chosenOption >= myOrderedProducts.size()) {
                System.err.println("When you get a back, 'OLIBKETISHGA_TAYYOR' statuses will change to 'OLIBKETILDI'!");
                secondSleep(1);
                for (OrderProduct myOrderedProduct : myOrderedProducts) {
                    checkProductIsGotByUser(myOrderedProduct);
                }
                break;
            } else {
                OrderProduct tempOrderProductToCancel = myOrderedProducts.get(chosenOption);
                System.out.printf("""
                        Do you really want to cancel this order?
                        %s
                        1. Yes\t2. No;
                        """, tempOrderProductToCancel);
                if (readInteger(">>>") == 1) {
                    myOrderedProducts.get(chosenOption).setStatus(OrderStatus.BUYURTMA_BEKOR_QILINDI);
                    logger.warning(currentUser + " - " + myOrderedProducts.get(chosenOption) + " orderini bekor qildi!");
                    System.out.println("Order has cancelled!");
                    System.out.println("The order fee will be refunded after 3 minutes!");
                    Thread refunderThread = new Thread(() -> {
                        secondSleep(20);
                        for (Card card : cards) {
                            if (card.getUserId().equals(currentUser.getId())) {
                                card.setBalance(card.getBalance() + myOrderedProducts.get(chosenOption).getPrice());
                                try {
                                    MyCustomNio.readFromList(cards);
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    });
                    refunderThread.setDaemon(true);
                    refunderThread.start();
                } else {
                    System.out.println("Order has not cancelled!");
                }
            }
        }
    }

    private static void buyProducts(Card card) {
        System.out.println("All products");
        for (int i = 0; i < products.size(); i++) {
            System.out.println((i + 1) + " -> Name: " + products.get(i).getName() +
                               " Price: " + products.get(i).getPrice() + "$");
        }
        int chosenIndex = readInteger(">>>") - 1;
        if (chosenIndex < 0 || chosenIndex >= products.size()) {
            System.err.println("Invalid command!");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        Product chosenProduct = products.get(chosenIndex);
        int amount = readInteger("Enter amount of the element: ");
        if (amount * chosenProduct.getPrice() >= card.getBalance()) {
            System.err.println("Not enough funds, operation failed!");
            return;
        }
        OrderProduct orderProduct = new OrderProduct(
                UUID.randomUUID(),
                currentUser.getId(),
                card.getId(),
                chosenProduct.getId(),
                amount,
                OrderStatus.BUYURTMA_BERILDI,
                LocalDateTime.now(),
                null
        );
        orderProducts.add(orderProduct);
        System.out.println("To'lov qilinishi kutilyapdi!");
        card.setBalance(card.getBalance() - orderProduct.getPrice());
        secondSleep(2);
        System.out.println("Buyurtma berildi!");
        Thread statusChangerThread = getThread(orderProduct);
        statusChangerThread.start();
    }

    private static Thread getThread(OrderProduct orderProduct) {
        Thread statusChangerThread = new Thread(() -> {
            secondSleep(3);
            if (itsCancelled(orderProduct)) {
                return;
            }
            orderProduct.setStatus(OrderStatus.YIGILYAPDI);
            secondSleep(5);
            if (itsCancelled(orderProduct)) {
                return;
            }
            orderProduct.setStatus(OrderStatus.KURYERDA);
            secondSleep(20);
            if (itsCancelled(orderProduct)) {
                return;
            }
            orderProduct.setStatus(OrderStatus.OLIBKETISHGA_TAYYOR);
        });
        statusChangerThread.setDaemon(true);
        return statusChangerThread;
    }

    private static void checkProductIsGotByUser(OrderProduct orderProduct) {
        if (orderProduct.getStatus().equals(OrderStatus.OLIBKETISHGA_TAYYOR)) {
            orderProduct.setStatus(OrderStatus.OLIBKETILDI);
            logger.severe(orderProduct + " - orderi sotildi!");
        }
    }


}
