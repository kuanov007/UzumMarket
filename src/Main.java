import auth.Auth;
import ui.UserUI;
import user.User;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Optional<User> optionalUser = Auth.run();
        if (optionalUser.isEmpty()) {
            System.out.println("Dasturdan foydalanganingiz uchun rahmat!");
            return;
        }
        UserUI.getCurrentUser(optionalUser.get());
        main(null);
    }
}