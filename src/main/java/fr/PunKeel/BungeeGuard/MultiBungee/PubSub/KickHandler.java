package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class KickHandler {
    @PubSubHandler("kick")
    public static void kick(PubSubMessageEvent e) {
        ProxiedPlayer p;
        String user = e.getArg(0);
        if (user.length() <= 16)
            p = ProxyServer.getInstance().getPlayer(user);
        else
            p = ProxyServer.getInstance().getPlayer(UUID.fromString(user));
        if (p == null)
            return;
        p.disconnect(new TextComponent(e.getArg(1)));
    }
}
