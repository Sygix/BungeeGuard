package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

public class ServerSilenceHandler {
    @PubSubHandler("silenceServer")
    public static void silence(Main plugin, PubSubMessageEvent e) {
        String serverName = e.getArg(0);
        boolean state = Boolean.parseBoolean(e.getArg(1));
        if (state) {
            plugin.silence(serverName);
        } else {
            plugin.unsilence(serverName);
        }
    }
}
