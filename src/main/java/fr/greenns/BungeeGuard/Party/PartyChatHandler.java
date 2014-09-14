package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.PubSubBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:50
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyChatHandler extends PubSubBase {
    public Main plugin;

    public PartyChatHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String _, String[] args) {
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        String playerName = plugin.getMB().getNameFromUuid(u);
        String message = args[2];
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(new TextComponent(ChatColor.GRAY + "[Party:" + p.getName() + "]" + ChatColor.RESET + playerName + ": " + message));

        }
    }
}
