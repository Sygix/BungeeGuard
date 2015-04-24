package fr.PunKeel.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.PluginMessage.FriendHandler;
import fr.PunKeel.BungeeGuard.PluginMessage.FriendHandler.FriendData;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.Collection;
import java.util.UUID;

public class PluginMessageManager {
    private final Main plugin;

    public PluginMessageManager(Main plugin) {
        this.plugin = plugin;
    }

    public void sendFriendList(ProxiedPlayer p, Server server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        Collection<UUID> friendsByUuid = plugin.getFriendManager().getFriends(p.getUniqueId(), FriendManager.STATE.MUTUAL);
        out.writeUTF("Friend");
        out.writeUTF("ListFriends");
        final Collection<FriendData> friends = Collections2.transform(friendsByUuid, new Function<UUID, FriendData>() {
            @Override
            public FriendData apply(final UUID uuid) {
                return FriendHandler.toFriendData(uuid);
            }
        });

        out.writeUTF(Main.getGson().toJson(friends));
        server.sendData("UHCGames", out.toByteArray());
    }

    public void sendFriendRemove(UUID userA, UUID userB) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Friend");
        out.writeUTF("RemoveFriend");
        out.writeUTF("" + userA);
        out.writeUTF("" + userB);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(userA);
        if (p != null)
            p.getServer().sendData("UHCGames", out.toByteArray());

        p = ProxyServer.getInstance().getPlayer(userB);
        if (p != null)
            p.getServer().sendData("UHCGames", out.toByteArray());
    }

    public void sendFriendAdd(UUID userA, UUID userB) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Friend");
        out.writeUTF("AddFriend");
        out.writeUTF("" + userA);
        out.writeUTF("" + userB);
        out.writeUTF(Main.getGson().toJson(FriendHandler.toFriendData(userB)));
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(userA);
        if (p != null)
            p.getServer().sendData("UHCGames", out.toByteArray());

        p = ProxyServer.getInstance().getPlayer(userB);
        if (p != null)
            p.getServer().sendData("UHCGames", out.toByteArray());
    }

    public void sendPartyInfo(ProxiedPlayer p, Server server) {
        PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Party");
        out.writeUTF("Infos");
        out.writeBoolean(party != null);
        if (party != null) {
            out.writeUTF(Main.getMB().getNameFromUuid(party.getOwner()));
            out.writeUTF(Util.csv(Collections2.transform(party.getMembers(), new Function<UUID, String>() {
                @Override
                public String apply(UUID uuid) {
                    return Main.getMB().getNameFromUuid(uuid);
                }
            })));
        }
        server.sendData("UHCGames", out.toByteArray());
    }

    public void sendPartyAddMember(ProxiedPlayer p, UUID u) {
        PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Party");
        out.writeUTF("AddMember");
        out.writeUTF(Main.getMB().getNameFromUuid(u));
        p.getServer().sendData("UHCGames", out.toByteArray());
    }

    public void sendPartyDisband(ProxiedPlayer pp, PartyManager.Party p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Party");
        out.writeUTF("DisbandParty");
        out.writeUTF(p.getName());
        pp.getServer().sendData("UHCGames", out.toByteArray());
    }

    public void sendPartyKick(ProxiedPlayer p, UUID u) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Party");
        out.writeUTF("KickPlayer");
        out.writeUTF(Main.getMB().getNameFromUuid(u));
        p.getServer().sendData("UHCGames", out.toByteArray());
    }
}
