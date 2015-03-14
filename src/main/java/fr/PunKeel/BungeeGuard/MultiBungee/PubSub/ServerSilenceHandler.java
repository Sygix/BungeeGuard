package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;

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
