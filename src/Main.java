import auth.Auth;
import db.file.MyCustomNio;
import handler.TelegramLogHandler;
import ui.UserUI;
import user.User;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import static db.MyBase.logger;

public class Main {
    public static FileHandler signInSignUpHolder;
    public static FileHandler canceledProductHolder;
    public static FileHandler completedProductHolder;
    public static TelegramLogHandler errorsHandler;

    static {
        try {
            signInSignUpHolder = new FileHandler("auth-activity.log", true);
            signInSignUpHolder.setFormatter(new SimpleFormatter());
            signInSignUpHolder.setFilter(record -> record.getLevel() == Level.INFO);

            canceledProductHolder = new FileHandler("canceled-orders.log", true);
            canceledProductHolder.setFormatter(new SimpleFormatter());
            canceledProductHolder.setFilter(record -> record.getLevel() == Level.WARNING);

            completedProductHolder = new FileHandler("completed-orders.log", true);
            completedProductHolder.setFormatter(new SimpleFormatter());
            completedProductHolder.setFilter(record -> record.getLevel() == Level.SEVERE);

            errorsHandler = new TelegramLogHandler("<YOUR_BOT_TOKEN>", "CHAT_ID");
            errorsHandler.setFormatter(new SimpleFormatter());
            errorsHandler.setFilter(record -> record.getLevel() == Level.OFF);

            logger.addHandler(errorsHandler);
            logger.addHandler(signInSignUpHolder);

            logger.addHandler(canceledProductHolder);
            logger.addHandler(completedProductHolder);
            logger.setUseParentHandlers(false);

        } catch (IOException ignored) {
        }
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