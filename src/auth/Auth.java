package auth;

import user.Card;
import user.User;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static db.MyBase.*;

public class Auth {
    public static Optional<User> run() {
        mainWhile:
        while (true) {
            switch (readInteger("""
                    [Main]
                    1. Register;
                    2. Sign in;
                                        
                    0. Exit;    
                    >>""")) {
                case 1 -> {
                    String userName = readLine("Creata a new username: ");
                    while (alreadyRegistered(userName)) {
                        userName = readLine("This username is already registered!, Creata a new username: ");
                    }
                    String name = readLine("Enter your name: ");
                    UUID userID = UUID.randomUUID();
                    if (readInteger("""
                            Do you want to add a card right now?
                            1. Yes, sure\t2.Maybe later;
                            >>>""") == 1) {
                        String cardNumber = readLine("Enter your card number: ");
                        if (isUsingCard(cardNumber)) {
                            System.err.println("This card already added by someone, you can't use it!");
                        } else {
                            Card card = new Card(UUID.randomUUID(), cardNumber, new Random().nextLong(500, 1000), userID);
                            addACard(card);
                        }
                    } else {
                        System.out.println("Okay, you can add your cards in any time!");
                    }
                    User user = new User(userID, name, userName);
                    users.add(user);
                    return Optional.of(user);
                }

                case 2 -> {
                    String userName = readLine("Kirishingiz uchun usernameingizni kiriting: ");
                    if (!alreadyRegistered(userName)) {
                        System.err.println("Bunaqa username bilan ro'yxatdan o'tilmagan!");
                    } else {
                        return getUserByUserName(userName);
                    }
                }
                case 0 -> {
                    break mainWhile;
                }
            }
        }
        return Optional.empty();
    }
}
