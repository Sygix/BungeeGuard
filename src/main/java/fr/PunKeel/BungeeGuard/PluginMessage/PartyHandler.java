package fr.PunKeel.BungeeGuard.PluginMessage;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PartyManager;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PartyHandler {
    public static void handle(Main plugin, ByteArrayDataInput in, ByteArrayDataOutput out, ProxiedPlayer p) {
        String subChannel = in.readUTF();
        switch (subChannel) {
            case "Invite":
                invite(plugin, in, p);
                break;
            case "ListMembers":
                listMembers(plugin, out, p);
                break;
            case "KickMember":
                kickMember(plugin, in, p);
                break;
            case "Leave":
                leave(plugin, p);
                break;
            case "Disband":
                disband(plugin, p);
                break;
        }
    }

    private static void invite(Main plugin, ByteArrayDataInput in, ProxiedPlayer p) {
        String user = in.readUTF();
        if (!plugin.getPartyManager().inParty(p))
            return;
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "party invite " + user);
    }

    private static void listMembers(Main plugin, ByteArrayDataOutput out, ProxiedPlayer p) {
        if (!plugin.getPartyManager().inParty(p))
            return;
        PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
        out.writeUTF("Party");
        out.writeUTF("ListMembers");
        out.writeUTF(Util.csv(Collections2.transform(party.getMembers(), new Function<UUID, String>() {
            @Override
            public String apply(UUID uuid) {
                return Main.getMB().getNameFromUuid(uuid);
            }
        })));
    }

    private static void kickMember(Main plugin, ByteArrayDataInput in, ProxiedPlayer p) {
        String toKick = in.readUTF();
        if (!plugin.getPartyManager().inParty(p))
            return;
        if (!plugin.getPartyManager().getPartyByPlayer(p).isOwner(p))
            return;
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "party kick " + toKick);
    }

    private static void disband(Main plugin, ProxiedPlayer p) {
        if (!plugin.getPartyManager().inParty(p))
            return;
        if (!plugin.getPartyManager().getPartyByPlayer(p).isOwner(p))
            return;
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "party disband");
    }

    private static void leave(Main plugin, ProxiedPlayer p) {
        if (plugin.getPartyManager().inParty(p))
            ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "party leave");
    }
}
