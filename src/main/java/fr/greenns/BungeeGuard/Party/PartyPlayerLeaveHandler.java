package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:24
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyPlayerLeaveHandler extends PubSubBase {
    Main plugin;

    public PartyPlayerLeaveHandler(Main plugin) {
        {
            this.plugin = plugin;
        }
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        System.out.println("Leave:" + message);
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        String playerName = plugin.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        p.removeMember(u);
        if (p.getSize() == 0)
            plugin.getPM().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = BungeeCord.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                pp.sendMessage(ChatColor.RED + "- " + playerName + " a quitté la Party");
            }
        }
    }
}