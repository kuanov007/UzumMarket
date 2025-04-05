package handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TelegramLogHandler extends Handler {
    private final String botToken;
    private final String chatId;

    public TelegramLogHandler(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;

        String message = getFormatter().format(record);

        try {
            sendToTelegram(message);
        } catch (IOException e) {
            System.err.println("Check your: chat_id or token!");
        }
    }

    private void sendToTelegram(String message) throws IOException {
        String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body = "chat_id=" + chatId + "&text=" + encode(message);
        try (OutputStream os = conn.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
            writer.write(body);
            writer.flush();
        }
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Telegram returned error code: " + responseCode);
        }
    }

    private String encode(String text) {
        return text.replace("&", "%26").replace("\n", "%0A").replace(" ", "%20");
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
