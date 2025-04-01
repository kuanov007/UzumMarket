package auth;

import user.User;

import java.util.Optional;
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
                        System.err.println("This username is already registered!");
                        userName = readLine("Creata a new username: ");
                    }
                    String name = readLine("Enter your name: ");
                    if (readInteger("""
                            Do you want to add a card right now?
                            1. Yes, sure\t2.Maybe later;
                            >>>""") == 1) {
                        //todo("Add card metodini yozishing kerak")
                    } else {
                        System.out.println("Okay, you can add your cards in any time!");
                    }
                    User user = new User(UUID.randomUUID(), name, userName);
                    return Optional.of(user);
                }

                case 2 -> {

                }

                case 0 -> {
                    break mainWhile;
                }
            }
        }
        return Optional.empty();
    }
}
