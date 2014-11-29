package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 00:13
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class KickHandler {
    @PubSubHandler("kick")
    public static void kick(PubSubMessageEvent e) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getArg(0));
        if (p == null)
            return;
        p.disconnect(new TextComponent(e.getArg(1)));
    }
}
