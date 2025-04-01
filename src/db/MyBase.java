package db;

import user.Card;
import user.User;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public interface MyBase {
    List<User> users = new ArrayList<>();
    List<Card> cards = new ArrayList<>();

    static boolean alreadyRegistered(String username) {
        return users.stream().anyMatch(_user -> username.equals(_user.getUsername()));
    }

    static boolean isUsingCard(String cardNumber) {
        return cards.stream().anyMatch(_card -> cardNumber.equals(_card.getCardNumber()));
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
