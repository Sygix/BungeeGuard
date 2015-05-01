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
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
        if (p == null)
            return;
        p.addMember(u);

        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            plugin.getPluginMessageManager().sendPartyAddMember(pp, u);
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "+ " + Main.getMB().getNameFromUuid(u) + ChatColor.RESET + " a rejoint la Party"));
        }
    }

    @PubSubHandler("partyChat")
    public static void partyChat(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
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
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.CHAT_TAG + ChatColor.RESET + playerColor + playerName + ChatColor.RESET + ": " + message));

        }
    }

    @PubSubHandler("createParty")
    public static void createParty(Main plugin, PubSubMessageEvent e) {
        // owner
        UUID u = UUID.fromString(e.getArg(0));
        plugin.getPartyManager().createParty(u);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u);
        if (p != null)
            plugin.getPluginMessageManager().sendPartyInfo(p, p.getServer());
    }

    @PubSubHandler("disbandParty")
    public static void disbandParty(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            plugin.getPluginMessageManager().sendPartyDisband(pp, p);
            pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.RED + "Party dissoute."));
        }
        plugin.getPartyManager().removeParty(p);
    }

    @PubSubHandler("inviteParty")
    public static void partyInvite(Main plugin, PubSubMessageEvent e) {
        // party.getOwner(), "" + joueur
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party party = plugin.getPartyManager().getParty(partyOwner);
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
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + Main.getMB().getNameFromUuid(partyOwner)))
                .append(ChatColor.YELLOW + " << ")
                .create());
    }

    @PubSubHandler("kickFromParty")
    public static void kickFromParty(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
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
                plugin.getPluginMessageManager().sendPartyKick(pp, u);
                pp.sendMessage(new TextComponent(PartyManager.TAG + ChatColor.RED + "- " + playerName + " a été kické de la Party"));
            }
        }
        p.removeMember(u);
    }

    @PubSubHandler("setPartyOwner")
    public static void setPartyOwner(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        String ownerName = Main.getMB().getNameFromUuid(u);
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
        plugin.getPartyManager().changePartyOwner(partyOwner, u);
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            plugin.getPluginMessageManager().sendPartyKick(pp, u);
            pp.sendMessage(new TextComponent(PartyManager.TAG + ChatColor.GREEN + ownerName + ChatColor.GRAY + " est désormais le chef de party"));
        }
    }

    @PubSubHandler("playerLeaveParty")
    public static void playerLeaveParty(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        UUID u = UUID.fromString(e.getArg(1));
        //noinspection ConstantConditions - occurs if the parsed UUID is invalid
        if (partyOwner == null || u == null)
            return;
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
        String playerName = Main.getMB().getNameFromUuid(u);
        if (p == null)
            return;
        if (p.isOwner(u)) {
            disbandParty(plugin, e);
            return;
        }
        p.removeMember(u);
        if (p.getSize() == 0)
            plugin.getPartyManager().removeParty(p);
        else {
            ProxiedPlayer pp;
            for (UUID uuid : p.getMembers()) {
                pp = ProxyServer.getInstance().getPlayer(uuid);
                if (pp == null)
                    continue;
                plugin.getPluginMessageManager().sendPartyKick(pp, u);
                pp.sendMessage(TextComponent.fromLegacyText(PartyManager.TAG + ChatColor.RED + "- " + playerName + " a quitté la Party"));
            }
        }
    }

    @PubSubHandler("@partyReply2")
    public static void partyReply(Main plugin, PubSubMessageEvent e) {
        String data = e.getArg(0);
        Type type = new TypeToken<Map<UUID, PartyManager.Party>>() {
        }.getType();
        plugin.getLogger().fine("[MB] Parties: received" + data);
        plugin.getPartyManager().setParties(Main.getGson().<Map<UUID, PartyManager.Party>>fromJson(data, type));
    }

    @PubSubHandler("@partyRequest2")
    public static void partyRequest(Main plugin, PubSubMessageEvent e) {
        String serveur = e.getArg(0);
        Main.getMB().replyParties(serveur, Main.getGson().toJson(plugin.getPartyManager().getParties()));
    }

    @PubSubHandler("summonParty")
    public static void summonParty(Main plugin, PubSubMessageEvent e) {
        UUID partyOwner = UUID.fromString(e.getArg(0));
        String serverName = e.getArg(1);
        ServerInfo SI = ProxyServer.getInstance().getServerInfo(serverName);
        PartyManager.Party p = plugin.getPartyManager().getParty(partyOwner);
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
