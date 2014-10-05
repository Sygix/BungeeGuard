package net.uhcwork.BungeeGuard.Party.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.PubSubBase;
import net.uhcwork.BungeeGuard.Party.Party;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
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
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        p.removeMember(u);
        if (p.getSize() == 0)
            plugin.getPM().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = ProxyServer.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "- " + playerName + " a quitté la Party"));
            }
        }
    }
}