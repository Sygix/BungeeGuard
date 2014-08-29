package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.PubSub.PubSubBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * Part of fr.greenns.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:48
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class PrivateMessageHandler implements PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        // envoyer le mp au receveur
        // ajouter au reply
        //pl.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append(p.getName()).color(ChatColor.GREEN).append(" ? ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());
        /*
        for (UUID uuid : plugin.spy) {
                                    try {
                                        ProxiedPlayer admin = BungeeCord.getInstance().getPlayer(uuid);
                                        admin.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("SPY").color(ChatColor.RED).append("] ").color(ChatColor.GRAY).append(p.getName()).append(": /msg ").append(pl.getName() + " " + text).create());
                                    } catch (Exception ignored) {
                                    }
                                }
         */
        // contacter le staff en spychat
    }
}
