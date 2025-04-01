import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String sentence = "Beruniy bekati     ";
        int index = 0;
        while (true) {
            TimeUnit.MILLISECONDS.sleep(300);
            System.out.print("\r|" + sentence.substring(index) + sentence.substring(0, index) + "|");
            index++;
            if (index == sentence.length()) {
                index = 0;
            }
        }
    }
}