package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SummonHandler {
    @PubSubHandler("summon")
    public static void summon(PubSubMessageEvent e) {
        String playerName = e.getArg(0);
        String server_target = e.getArg(1);
        String sender = e.getArg(2);
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

    private static void summon(ProxiedPlayer player, ServerInfo target, String senderName) {
        if (player.getServer() != null && !player.getServer().getInfo().equals(target)) {
            player.connect(target);
            if (!senderName.isEmpty())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Envoyé sur " + Main.getServerManager().getPrettyName(target.getName()) + ChatColor.RESET + ChatColor.GOLD + " par " + senderName));
        }
    }
}
