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
 * Date: 21/09/2014
 * Time: 16:37
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyDisbandHandler extends PubSubBase {
    public Main plugin;

    public PartyDisbandHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String _, String[] args) {
        String partyName = args[0];
        Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "[Party:" + p.getName() + "]" + ChatColor.RED + "Party dissoute."));
        }
        plugin.getPM().removeParty(p);
    }
}
