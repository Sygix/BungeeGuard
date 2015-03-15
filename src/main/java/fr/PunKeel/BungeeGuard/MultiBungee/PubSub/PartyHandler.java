package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import com.google.gson.reflect.TypeToken;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PartyManager;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import fr.PunKeel.BungeeGuard.Utils.MyBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class PartyHandler {
    @PubSubHandler("addPartyMember")
    public static void addPartyMember(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        if (p == null)
            return;
        p.addMember(u);

        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "+ " + Main.getMB().getNameFromUuid(u) + ChatColor.RESET + " a rejoint la Party"));
        }
    }

    @PubSubHandler("partyChat")
    public static void partyChat(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
        String message = e.getArg(2);
        if (p == null)
            return;
        String playerColor = plugin.getPermissionManager().getMainGroup(u).getColor();
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.CHAT_TAG + ChatColor.RESET + playerColor + playerName + ": " + message));

        }
    }

    @PubSubHandler("createParty")
    public static void createParty(Main plugin, PubSubMessageEvent e) {
        // partyName(), "" + joueur
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        plugin.getPartyManager().createParty(partyName, u);
    }

    @PubSubHandler("disbandParty")
    public static void disbandParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.RED + "Party dissoute."));
        }
        plugin.getPartyManager().removeParty(p);
    }

    @PubSubHandler("inviteParty")
    public static void partyInvite(Main plugin, PubSubMessageEvent e) {
        // party.getName(), "" + joueur
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party party = plugin.getPartyManager().getParty(partyName);
        if (party == null)
            return;
        party.addInvitation(u);

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u);
        if (p == null)
            return;

        TextComponent TC = new TextComponent(PartyManager.TAG + "Ceci est une invitation à rejoindre la Party de ");
        TC.setColor(ChatColor.GRAY);
        TC.addExtra(party.getDisplay());
        p.sendMessage(TC);

        p.sendMessage(new MyBuilder(PartyManager.TAG + ChatColor.YELLOW + "  >> ")
                .append(ChatColor.GREEN + "Cliquez ici pour accepter")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + partyName))
                .append(ChatColor.YELLOW + " << ")
                .create());
    }

    @PubSubHandler("kickFromParty")
    public static void kickFromParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        if (p.getSize() == 1)
            plugin.getPartyManager().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = ProxyServer.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                pp.sendMessage(new TextComponent(PartyManager.TAG + ChatColor.RED + "- " + playerName + " a été kické de la Party"));
            }
        }
        p.removeMember(u);
    }

    @PubSubHandler("setPartyOwner")
    public static void setPartyOwner(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        if (p == null)
            return;
        p.setOwner(u);
    }

    @PubSubHandler("playerLeaveParty")
    public static void playerLeaveParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
        String playerName = Main.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        p.removeMember(u);
        if (p.getSize() == 0)
            plugin.getPartyManager().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = ProxyServer.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.RED + "- " + playerName + " a quitté la Party"));
            }
        }
    }

    @PubSubHandler("@partyReply")
    public static void partyRepl(Main plugin, PubSubMessageEvent e) {
        String data = e.getArg(0);
        Type type = new TypeToken<Map<String, PartyManager.Party>>() {
        }.getType();
        System.out.println("[MB] Parties: received" + data);
        plugin.getPartyManager().setParties(Main.getGson().<Map<String, PartyManager.Party>>fromJson(data, type));
    }

    @PubSubHandler("@partyRequest")
    public static void partyRequest(Main plugin, PubSubMessageEvent e) {
        String serveur = e.getArg(0);
        Main.getMB().replyParties(serveur, Main.getGson().toJson(plugin.getPartyManager().getParties()));
    }

    @PubSubHandler("summonParty")
    public static void summonParty(Main plugin, PubSubMessageEvent e) {
        String partyName = e.getArg(0);
        String serverName = e.getArg(1);
        ServerInfo SI = ProxyServer.getInstance().getServerInfo(serverName);
        PartyManager.Party p = plugin.getPartyManager().getParty(partyName);
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

    private static void summon(ProxiedPlayer player, ServerInfo target) {
        if (player.getServer() != null
                && !player.getServer().getInfo().equals(target)
                && player.getServer().getInfo().getName().startsWith("lobby")) {
            player.connect(target);
            player.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.GOLD + "Envoi sur le serveur " + Main.getServerManager().getPrettyName(target.getName()) + ChatColor.RESET + ChatColor.GOLD + " ..."));
        }
    }
}
