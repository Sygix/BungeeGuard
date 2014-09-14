package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:48
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class PrivateMessageHandler extends PubSubBase {

    private Main plugin;

    public PrivateMessageHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String sender = args[0];
        UUID receiver = UUID.fromString(args[1]);
        String contenu = args[2];

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(receiver);

        if (p != null) {
            p.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append(sender).color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + contenu).create());
        }

        plugin.reply.put(receiver, sender);

        ProxiedPlayer admin;
        for (UUID uuid : plugin.spy) {
            try {
                admin = ProxyServer.getInstance().getPlayer(uuid);
                admin.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("SPY").color(ChatColor.RED).append("] ").color(ChatColor.GRAY).append(sender).append(": /msg ").append(BungeeGuardUtils.getMB().getNameFromUuid(receiver) + " " + contenu).create());
            } catch (Exception ignored) {
            }
        }
    }
}
