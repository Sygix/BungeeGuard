package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.BungeeGuard;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:36
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class UpdateMOTDHandler implements PubSubBase {


    private BungeeGuard plugin;

    public UpdateMOTDHandler(BungeeGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        plugin.utils.refreshMotd();
    }
}
