package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:16
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class StaffNotificationHandler implements PubSubBase {


    private BungeeGuard plugin;

    public StaffNotificationHandler(BungeeGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            if (p.hasPermission("bungeeguard.notify")) {
                p.sendMessage(ComponentManager.generate(message));
            }
        }
    }
}
