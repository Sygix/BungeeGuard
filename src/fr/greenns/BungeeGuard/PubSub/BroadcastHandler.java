package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Part of fr.greenns.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:24
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class BroadcastHandler extends PubSubBase {
    @Override
    public void handle(String channel, String msg, String[] args) {
        Set<String> servers;
        if (args[0].equalsIgnoreCase("*")) {
            servers = BungeeCord.getInstance().getServers().keySet();
        } else {
            servers = new HashSet<>(Arrays.asList(args[0].split(";")));
        }
        ServerInfo SI;
        for (String server : servers) {
            SI = BungeeCord.getInstance().getServerInfo(server);
            if (SI == null)
                return;
            for (ProxiedPlayer p : SI.getPlayers()) {
                p.sendMessage(new ComponentBuilder(" ").create());
                p.sendMessage(ComponentManager.generate(ChatColor.AQUA + "[" + ChatColor.GOLD + "***" + ChatColor.AQUA + "]" + ChatColor.GRAY + " " + ChatColor.translateAlternateColorCodes('&', args[1])));
                p.sendMessage(new ComponentBuilder(" ").create());
            }
        }
    }
}
