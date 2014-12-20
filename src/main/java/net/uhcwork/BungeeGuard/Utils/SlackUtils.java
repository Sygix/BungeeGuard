package net.uhcwork.BungeeGuard.Utils;

import com.google.common.util.concurrent.RateLimiter;
import net.uhcwork.BungeeGuard.Main;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SlackUtils {
    static final String HOOK_URL = "https://hooks.slack.com/services/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";
    final RateLimiter rateLimiter = RateLimiter.create(1.5);

    public void staffChat(String username, String message) {
        sendMessage(username, "#staffchat", message);
    }

    public void sendMessage(String username, String channel, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("channel", channel);
        data.put("username", username);
        data.put("text", message);
        data.put("parse", "full");

        if (username.equals("BungeeCord")) {
            data.put("icon_emoji", ":rotating_light:");
        } else {
            data.put("icon_url", "https://cravatar.eu/helmhead/" + username + "/100.png");
        }
        doPost(data);
    }

    private void doPost(final Map<String, String> data) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rateLimiter.acquire(1);
                try {
                    String req = "payload=" + URLEncoder.encode(Main.getGson().toJson(data), "UTF-8");
                    URL url = new URL(HOOK_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Content-Length", "" + Integer.toString(req.getBytes().length));
                    connection.setUseCaches(false);

                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(req);
                    wr.flush();
                    wr.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        System.out.println("[Slack] Non 200 return code: " + responseCode);
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Main.plugin.executeRunnable(runnable);
    }
}
