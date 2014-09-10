package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:23
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyInviteHandler extends PubSubBase {
    Main plugin;

    public PartyInviteHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        // party.getName(), "" + joueur
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        plugin.getPM().getParty(partyName).addInvitation(u);

        ProxiedPlayer p = BungeeCord.getInstance().getPlayer(u);
        if (p == null)
            return;

        p.sendMessage(new ComponentBuilder("Ceci est une invitation à rejoindre la Party ").color(ChatColor.GRAY)
                .append(p.getName()).bold(true)
                .append(" : ")
                .append("Accepter").underlined(true).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + p.getName()))
                .create());
    }
}
