package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:25
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyKickHandler extends PubSubBase {
    Main plugin;

    public PartyKickHandler(Main plugin) {
        {
            this.plugin = plugin;
        }
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        String playerName = plugin.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        if (p.getSize() == 1)
            plugin.getPM().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = ProxyServer.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                pp.sendMessage(new TextComponent(ChatColor.RED + "- " + playerName + " a été kické de la Party"));
            }
        }
        p.removeMember(u);
    }
}
