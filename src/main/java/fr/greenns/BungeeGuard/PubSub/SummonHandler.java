package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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

        if (playerName.equalsIgnoreCase("*")) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                summon(p, target, sender);
            }
        } else if (BungeeCord.getInstance().getServerInfo(playerName) != null) {
            for (ProxiedPlayer p : BungeeCord.getInstance().getServerInfo(playerName).getPlayers()) {
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
            player.sendMessage(ComponentManager.generate(ChatColor.GOLD + "Summoned to " + target.getName() + " by " + senderName));
        }
    }
}
