package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:46
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class StaffChatHandler {

    @PubSubHandler("staffChat")
    public void staffChat(PubSubMessageEvent e) {
        String serverName = e.getArg(0);
        String senderName = e.getArg(1);
        String message = ChatColor.translateAlternateColorCodes('&', e.getArg(2));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("bungee.staffchat")) {
                player.sendMessage(PrettyLinkComponent.fromLegacyText(ChatColor.RED + "[" + Main.getPrettyServerName(serverName) + ChatColor.RESET + ChatColor.RED + "] " + senderName + ": " + message));
            }
        }
    }

    @PubSubHandler("notifyStaff")
    public void notifyStaff(Main plugin, PubSubMessageEvent e) {
        for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            if (p.hasPermission("bungee.notify")) {
                p.sendMessage(TextComponent.fromLegacyText(e.getMessage()));
            }
        }
    }
}
