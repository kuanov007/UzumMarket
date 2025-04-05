package ui;

import product.OrderProduct;
import product.OrderStatus;
import product.Product;
import user.Card;
import user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static db.MyBase.*;

public class UserUI {
    private static User currentUser;

    public static void getCurrentUser(User user)  {
        currentUser = user;
        run();
    }

    public static void run()  {
        Optional<Card> card = getCardByUserId(currentUser.getId());
        mainWhile:
        while (true) {
            switch (readInteger("""
                    [Name : %s | Balance: %s$]
                    1. Products;
                    2. My orders(Monitoring!);
                    3. History;
                    4. Fill balance;
                                        
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

                }
                case 4 -> {

                }
                case 0 -> {
                    break mainWhile;
                }
            }
        }

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
                    System.out.println("Order has cancelled!");
                    System.out.println("The order fee will be refunded after 3 minutes!");
                    Thread refunderThread = new Thread(() -> {
                        secondSleep(20);
                        for (Card card : cards) {
                            if (card.getUserId().equals(currentUser.getId())) {
                                card.setBalance(card.getBalance() + myOrderedProducts.get(chosenOption).getPrice());
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
            secondSleep(15);
            if (itsCancelled(orderProduct)) {
                return;
            }
            orderProduct.setStatus(OrderStatus.KURYERDA);
            secondSleep(120);
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
        }
    }


}
