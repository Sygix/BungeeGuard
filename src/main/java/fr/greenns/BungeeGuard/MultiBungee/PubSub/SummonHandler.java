package fr.greenns.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:46
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class SummonHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        String playerName = args[0];
        String server_target = args[1];
        String sender = args[2];
        ServerInfo target = ProxyServer.getInstance().getServerInfo(server_target);
        if (target == null) {
            return;
        }

        if (ProxyServer.getInstance().getServerInfo(playerName) != null) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(playerName).getPlayers()) {
                summon(p, target, sender);
            }
        } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player == null) {
                return;
            }
            summon(player, target, sender);
        }
    }

    private void summon(ProxiedPlayer player, ServerInfo target, String senderName) {
        if (player.getServer() != null && !player.getServer().getInfo().equals(target)) {
            player.connect(target);
            if (!senderName.isEmpty())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Summoned to " + target.getName() + " by " + senderName));
        }
    }
}
