package fr.PunKeel.BungeeGuard.PluginMessage;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.FriendManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Utils.UUIDUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.javalite.activejdbc.Base;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FriendHandler {

    public static void handle(Main plugin, ByteArrayDataInput in, ByteArrayDataOutput out, ProxiedPlayer p) {
        String subChannel = in.readUTF();

        switch (subChannel) {
            case "TeleportTo":
                teleportTo(in, p);
                break;
            case "ListFriends":
                listFriends(plugin, out, p);
                break;

        }
    }

    private static void listFriends(final Main plugin, final ByteArrayDataOutput out, ProxiedPlayer p) {
        Collection<UUID> friendsByUuid = plugin.getFriendManager().getFriends(p.getUniqueId(), FriendManager.STATE.MUTUAL);
        out.writeUTF("Friend");
        out.writeUTF("ListFriends");
        final MultiBungee MB = Main.getMB();
        final Collection<FriendData> friends = Collections2.transform(friendsByUuid, new Function<UUID, FriendData>() {
            @Override
            public FriendData apply(final UUID uuid) {
                long playTime = 0;

                try {
                    playTime = plugin.executePersistenceRunnable(new Callable<Long>() {
                        @Override
                        public Long call() throws Exception {
                            return Long.valueOf(String.valueOf(Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE uuid = ? AND leaved_at IS NOT NULL", UUIDUtils.toBytes(uuid))));
                        }
                    }).get(20, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
                return new FriendData(uuid, MB.getNameFromUuid(uuid), MB.isPlayerOnline(uuid), MB.getLastOnline(uuid), playTime);
            }

        });

        out.writeUTF(Main.getGson().toJson(friends));
    }

    private static void teleportTo(ByteArrayDataInput in, ProxiedPlayer p) {
        String pseudo = in.readUTF();
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "friend teleport " + pseudo);
    }

    @lombok.AllArgsConstructor
    static class FriendData {
        UUID uuid;
        String name;
        boolean isOnline;
        long lastOnline, playTime;
    }
}
