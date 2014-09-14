package fr.greenns.BungeeGuard.PubSub;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
        String message = args[2];

        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            if (player.hasPermission("bungeeguard.staffchat")) {
                player.sendMessage(new TextComponent(ChatColor.RED + "[" + serverName + "] " + senderName + ": " + message));
            }
        }
    }
}