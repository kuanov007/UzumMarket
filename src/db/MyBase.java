package db;

import user.Card;
import user.User;

import java.util.*;

public interface MyBase {
    List<User> users = new ArrayList<>();
    List<Card> cards = new ArrayList<>();

    static boolean alreadyRegistered(String username) {
        return users.stream().anyMatch(_user -> username.equals(_user.getUsername()));
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
}
