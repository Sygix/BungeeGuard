package net.uhcwork.BungeeGuard.Party;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.PubSubBase;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
 * Date: 13/09/2014
 * Time: 16:49
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyAddMemberHandler extends PubSubBase {
    private final Main plugin;

    public PartyAddMemberHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.addMember(u);

        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "+ " + plugin.getMB().getNameFromUuid(u) + ChatColor.RESET + " a rejoint la Party"));
        }
    }
}
