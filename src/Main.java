import auth.Auth;
import ui.UserUI;
import user.User;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        while (true) {
            Optional<User> optionalUser = Auth.run();
            if (optionalUser.isEmpty()) {
                System.out.println("Dasturdan foydalanganingiz uchun rahmat!");
                break;
            }
            try {
                UserUI.getCurrentUser(optionalUser.get());
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }
}