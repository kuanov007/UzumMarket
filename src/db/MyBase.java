package db;

import product.OrderProduct;
import product.OrderStatus;
import product.Product;
import user.Card;
import user.User;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface MyBase {
    List<User> users = new ArrayList<>();
    List<Card> cards = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<OrderProduct> orderProducts = new ArrayList<>();

    static boolean alreadyRegistered(String username) {
        if (users.isEmpty()) return false;
        return users.stream().anyMatch(_user -> username.equals(_user.getUsername()));
    }

    static Optional<Card> getCardByUserId(UUID userId) {
        for (Card card : cards) {
            if (card.getUserId().equals(userId)) {
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    static boolean isUsingCard(String cardNumber) {
        return cards.stream().anyMatch(_card -> cardNumber.equals(_card.getCardNumber()));
    }

    static void addACard(Card card) {
        cards.add(card);
    }

    static Optional<User> getUserByUserName(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    static String readLine(String message) {
        System.out.print(message);
        String string = new Scanner(System.in).nextLine();
        if (string.isBlank()) {
            System.err.println("You have fill the blank!");
            return readLine(message);
        }
        return string;
    }

    static int readInteger(String message) {
        int integer;
        while (true) {
            System.out.print(message);
            try {
                integer = new Scanner(System.in).nextInt();
                break;
            } catch (InputMismatchException ignored) {
                System.err.println("You can enter only numbers!");
            }
        }
        return integer;
    }

    static void secondSleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static long productPriceById(UUID id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product.getPrice();
            }
        }
        return -1;
    }

    static String productNameById(UUID id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product.getName();
            }
        }
        return null;
    }

    static boolean itsCancelled(OrderProduct orderProduct) {
        return orderProduct.getStatus().equals(OrderStatus.BUYURTMA_BEKOR_QILINDI);
    }
}
