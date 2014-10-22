package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PartyManager;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (BungeeGuard)
 * Date: 22/10/2014
 * Time: 19:06
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyHandler {
    @PubSubHandler("addPartyMember")
    public void addPartyMember(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.addMember(u);

        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "+ " + Main.getMB().getNameFromUuid(u) + ChatColor.RESET + " a rejoint la Party"));
        }
    }

    @PubSubHandler("partyChat")
    public void partyChat(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
        String message = e.getArg(2);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "[Party:" + p.getName() + "] " + ChatColor.RESET + playerName + ": " + message));

        }
    }

    @PubSubHandler("setPartyChat")
    public void setPartyChat(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID uuid = UUID.fromString(e.getArg(1));
        boolean isPartyChat = Boolean.parseBoolean(e.getArg(2));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.setPartyChat(uuid, isPartyChat);
    }

    @PubSubHandler("createParty")
    public void createParty(Main plugin, PubSubMessageEvent e) {
        // partyName(), "" + joueur
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        plugin.getPM().createParty(partyName, u);
    }

    @PubSubHandler("disbandParty")
    public void disbandParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "[Party:" + p.getName() + "] " + ChatColor.RED + "Party dissoute."));
        }
        plugin.getPM().removeParty(p);
    }

    @PubSubHandler("inviteParty")
    public void partyInvite(Main plugin, PubSubMessageEvent e) {
        // party.getName(), "" + joueur
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party party = plugin.getPM().getParty(partyName);
        if (party == null)
            return;
        party.addInvitation(u);

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u);
        if (p == null)
            return;

        TextComponent TC = new TextComponent("Ceci est une invitation à rejoindre la Party ");
        TC.setColor(ChatColor.GRAY);
        TC.addExtra(party.getDisplay());
        p.sendMessage(TC);

        p.sendMessage(new ComponentBuilder("  >> ").color(ChatColor.YELLOW)
                .append("Cliquez ici pour accepter").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + partyName))
                .append(" << ").color(ChatColor.YELLOW)
                .create());
    }

    @PubSubHandler("kickFromParty")
    public void kickFromParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
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

    @PubSubHandler("setPartyOwner")
    public void setPartyOwner(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.setOwner(u);
    }

    @PubSubHandler("playerLeaveParty")
    public void playerLeaveParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPM().getParty(partyName);
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

    @PubSubHandler("setPartyPublique")
    public void setPartyPublique(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        boolean isPublique = Boolean.parseBoolean(e.getArg(1));
        plugin.getPM().getParty(partyName).setPublique(isPublique);
    }

    @PubSubHandler("@partyReply")
    public void partyRepl(Main plugin, PubSubMessageEvent e) {
        String data = e.getArg(0);
        Type type = new TypeToken<Map<String, PartyManager.Party>>() {
        }.getType();
        System.out.println("[MB] Parties: received" + data);
        plugin.getPM().setParties(Main.getGson().<Map<String, PartyManager.Party>>fromJson(data, type));
    }

    @PubSubHandler("@partyRequest")
    public void partyRequest(Main plugin, PubSubMessageEvent e) {
        String serveur = e.getArg(0);
        Main.getMB().replyParties(serveur, Main.getGson().toJson(plugin.getPM().getParties()));
    }

    @PubSubHandler("summonParty")
    public void summonParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        String serverName = e.getArg(1);
        ServerInfo SI = ProxyServer.getInstance().getServerInfo(serverName);
        PartyManager.Party p = plugin.getPM().getParty(partyName);
        if (SI == null)
            return;
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            if (p.isOwner(pp))
                continue;
            summon(pp, SI);
        }
    }

    private void summon(ProxiedPlayer player, ServerInfo target) {
        if (player.getServer() != null
                && !player.getServer().getInfo().equals(target)
                && player.getServer().getInfo().getName().startsWith("lobby")) {
            player.connect(target);
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Envoi sur le serveur " + Main.getPrettyServerName(target.getName()) + ChatColor.RESET + ChatColor.GOLD + " ..."));
        }
    }
}
