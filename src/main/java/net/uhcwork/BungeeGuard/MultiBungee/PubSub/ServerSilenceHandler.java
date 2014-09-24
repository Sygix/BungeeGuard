package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (bungeeguard)
 * Date: 14/09/2014
 * Time: 14:26
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ServerSilenceHandler extends PubSubBase {
    Main plugin;

    public ServerSilenceHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String serverName = args[0];
        boolean state = Boolean.parseBoolean(args[1]);
        if (state) {
            plugin.silence(serverName);
        } else {
            plugin.unsilence(serverName);
        }
    }
}
