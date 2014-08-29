package fr.greenns.BungeeGuard.PubSub;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Part of fr.greenns.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:24
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class BroadcastHandler implements PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        String[] servers = args[0].split(";");
        ServerInfo SI;
        for (String server : servers) {
            SI = BungeeCord.getInstance().getServerInfo(server);
            if (SI == null)
                return;
            for (ProxiedPlayer p : SI.getPlayers()) {
                p.sendMessage(new ComponentBuilder(" ").create());
                p.sendMessage(new ComponentBuilder(ChatColor.AQUA + "[" + ChatColor.GOLD + "***" + ChatColor.AQUA + "]" + ChatColor.GRAY + " " + message).create());
                p.sendMessage(new ComponentBuilder(" ").create());
            }
        }
    }
}
