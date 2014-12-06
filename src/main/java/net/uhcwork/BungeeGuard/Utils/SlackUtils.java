package net.uhcwork.BungeeGuard.Utils;

import net.uhcwork.BungeeGuard.Main;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OutputStreamWriter writer = null;
                try {
                    String req = "payload=" + URLEncoder.encode(Main.getGson().toJson(data), "UTF-8");
                    URL url = new URL(HOOK_URL);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(req);
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert writer != null;
                        writer.close();
                    } catch (Exception ignored) {
                    }
                }

            }
        };
        Main.plugin.executeRunnable(runnable);
    }
}
