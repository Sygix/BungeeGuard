package net.uhcwork.BungeeGuard.Party.PubSub;

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
public class PartyChatSetHandler extends PubSubBase {
    Main plugin;

    public PartyChatSetHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        UUID uuid = UUID.fromString(args[1]);
        boolean isPartyChat = Boolean.parseBoolean(args[2]);
        Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.setPartyChat(uuid, isPartyChat);
    }
}
