package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:46
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class StaffChatHandler extends PubSubBase {

    @Override
    public void handle(String channel, String _, String[] args) {

        String serverName = args[0];
        String senderName = args[1];
        String message = ChatColor.translateAlternateColorCodes('&', args[2]);
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("bungeeguard.staffchat")) {
                player.sendMessage(PrettyLinkComponent.fromLegacyText(ChatColor.RED + "[" + Main.getPrettyServerName(serverName) + ChatColor.RESET + ChatColor.RED + "] " + senderName + ": " + message));
            }
        }
    }
}
