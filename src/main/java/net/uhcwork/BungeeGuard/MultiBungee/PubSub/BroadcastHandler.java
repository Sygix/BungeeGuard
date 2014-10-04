package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.utils.PrettyLinkComponent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Part of net.uhcwork.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:24
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class BroadcastHandler extends PubSubBase {
    @Override
    public void handle(String channel, String _, String[] args) {
        Set<String> servers;
        if (args[0].equalsIgnoreCase("*")) {
            servers = ProxyServer.getInstance().getServers().keySet();
        } else {
            servers = new HashSet<>(Arrays.asList(args[0].split(";")));
        }
        ServerInfo SI;
        String msg = ChatColor.translateAlternateColorCodes('&', args[1]);
        for (String server : servers) {
            SI = ProxyServer.getInstance().getServerInfo(server);
            if (SI == null)
                return;
            for (ProxiedPlayer p : SI.getPlayers()) {
                p.sendMessage(new TextComponent(" "));
                p.sendMessage(PrettyLinkComponent.fromLegacyText(ChatColor.AQUA + "[" + ChatColor.GREEN + "***" + ChatColor.AQUA + "]" + ChatColor.GRAY + " " + msg));
                p.sendMessage(new TextComponent(" "));
            }
        }
    }
}