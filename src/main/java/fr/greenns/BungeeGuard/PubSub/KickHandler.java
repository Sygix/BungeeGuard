package fr.greenns.BungeeGuard.PubSub;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:13
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class KickHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
        if (p == null)
            return;
        p.disconnect(new TextComponent(args[1]));
    }
}
