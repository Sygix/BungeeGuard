package net.uhcwork.BungeeGuard.Utils;

import net.uhcwork.BungeeGuard.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SlackUtils {
    static final String HOOK_URL = "https://hooks.slack.com/services/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";


    public void staffChat(String username, String message) {
        sendMessage(username, "#staffchat", message);
    }

    public void sendMessage(String username, String channel, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("channel", channel);
        data.put("username", username);
        data.put("text", message);
        data.put("icon_emoji", ":microscope:");
        doPost(data);
    }

    private void doPost(final Map<String, String> data) {
        Callable<?> myCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                OutputStreamWriter writer = null;
                BufferedReader reader = null;
                String result = "";
                try {
                    String req = "payload=" + URLEncoder.encode(Main.getGson().toJson(data), "UTF-8");
                    URL url = new URL(HOOK_URL);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(req);
                    writer.flush();

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String ligne;
                    while ((ligne = reader.readLine()) != null) {
                        result += ligne;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert writer != null;
                        writer.close();
                    } catch (Exception ignored) {
                    }
                    try {
                        assert reader != null;
                        reader.close();
                    } catch (Exception ignored) {
                    }
                }
                return result;

            }
        };
        Main.plugin.executePersistenceRunnable(myCallable);
    }
}
