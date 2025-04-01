package ui;

import user.Card;
import user.User;

import java.util.Optional;

import static db.MyBase.getCardByUserId;
import static db.MyBase.readInteger;

public class UserUI {
    private static User currentUser;

    public static void getCurrentUser(User user) {
        currentUser = user;
        run();
    }

    public static void run() {
        Optional<Card> card = getCardByUserId(currentUser.getId());
        mainWhile:
        while (true) {
            switch (readInteger("""
                    [Name : %s | Balance: %s$]
                    1. Products;
                    2. My orders;
                    3. History;
                    4. Fill balance;
                                        
                    0. log out;
                    >>>""".formatted(currentUser.getName(), card.isPresent() ? card.get().getBalance() :
                    "card not found!"))) {
                case 1 -> {

                }
                case 2 -> {

                }
                case 0 -> {
                    break mainWhile;
                }
            }
        }

    }
}
