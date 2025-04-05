package db.file;

import user.Card;
import user.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static db.MyBase.*;

public class MyCustomNio {
    private static final String usersFile = "users.txt";
    private static final String cardsFile = "cards.txt";
    private static final String productsFile = "products.txt";

    public static void run() {
        try {
            users.clear();
            cards.clear();
            products.clear();

            users.addAll(readFromFile(usersFile));
            cards.addAll(readFromFile(cardsFile));
            products.addAll(readFromFile(productsFile));
        } catch (IOException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T extends Serializable> ArrayList<T> readFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ArrayList<T> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(fileInputStream)) {
            list = new ArrayList<>();
            while (true) {
                list.add((T) ois.readObject());
            }
        } catch (EOFException ignored) {
        }
        return list;
    }


    public static <T extends Serializable> void readFromList(List<T> list) throws IOException {
        if (list.isEmpty()) {
            return;
        }
        ObjectOutputStream ous = null;
        if (list.get(0) instanceof User) {
            ous = new ObjectOutputStream(new FileOutputStream(usersFile));
        } else if (list.get(0) instanceof Card) {
            ous = new ObjectOutputStream(new FileOutputStream(cardsFile));
        } else {
            ous = new ObjectOutputStream(new FileOutputStream(productsFile));
        }
        for (T t : list) {
            ous.writeObject(t);
        }
        ous.close();
    }
}
