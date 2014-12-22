package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Permissions.Group;
import net.uhcwork.BungeeGuard.Permissions.Permissions;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

import java.util.*;

public class BroadcastHandler {
    private final static String BROADCAST_FORMAT = ChatColor.AQUA + "[" + ChatColor.GREEN + "***" + ChatColor.AQUA + "] " + ChatColor.RESET + "%s" + ChatColor.GREEN + "%s";
    private final static TextComponent EMPTY_COMPONENT = new TextComponent(" ");

    @PubSubHandler("broadcast")
    public static void broadcast(Main plugin, PubSubMessageEvent e) {
        Set<String> servers;
        if (e.getArg(0).equalsIgnoreCase("*")) {
            servers = ProxyServer.getInstance().getServers().keySet();
        } else {
            servers = new HashSet<>(Arrays.asList(e.getArg(0).split(";")));
        }
        String tag = "";
        if (!Objects.equals(e.getArg(2), "")) {
            UUID sender = UUID.fromString(e.getArg(2));
            String senderName = Main.getMB().getNameFromUuid(sender);
            Group g = plugin.getPermissionManager().getMainGroup(sender);
            if (g != null && senderName != null && !Permissions.hasPerm(sender, "bungee.can.broadcast_anonymously"))
                tag = g.getColor() + senderName + ChatColor.RESET + " : ";
        }
        ServerInfo SI;
        String msg = ChatColor.translateAlternateColorCodes('&', e.getArg(1));
        BaseComponent[] message = PrettyLinkComponent.fromLegacyText(String.format(BROADCAST_FORMAT, tag, msg));
        for (String server : servers) {
            SI = ProxyServer.getInstance().getServerInfo(server);
            if (SI == null)
                return;
            for (ProxiedPlayer p : SI.getPlayers()) {
                p.sendMessage(EMPTY_COMPONENT);
                p.sendMessage(message);
                p.sendMessage(EMPTY_COMPONENT);
            }
        }
    }
}