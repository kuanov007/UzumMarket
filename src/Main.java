import auth.Auth;
import db.file.MyCustomNio;
import ui.UserUI;
import user.User;

import java.io.IOException;
import java.util.Optional;

public class Main {

    static {
        MyCustomNio.run();
    }

    public static void main(String[] args) throws IOException {
        Optional<User> optionalUser = Auth.run();
        if (optionalUser.isEmpty()) {
            System.out.println("Dasturdan foydalanganingiz uchun rahmat!");
            return;
        }
        UserUI.getCurrentUser(optionalUser.get());
        main(null);
    }
}