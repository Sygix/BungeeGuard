package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:13
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class KickHandler implements PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
        if (p == null)
            return;
        p.disconnect(ComponentManager.generate(args[1]));
    }
}
