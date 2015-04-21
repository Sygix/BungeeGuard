package fr.PunKeel.BungeeGuard.PluginMessage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Utils.UUIDUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.javalite.activejdbc.Base;

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
                plugin.getPluginMessageManager().sendFriendList(p);
                break;

        }
    }

    private static void listFriends(final Main plugin, ProxiedPlayer p) {

    }

    private static void teleportTo(ByteArrayDataInput in, ProxiedPlayer p) {
        String pseudo = in.readUTF();
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "friend teleport " + pseudo);
    }

    public static FriendData toFriendData(final UUID uuid) {
        long playTime = 0;

        try {
            playTime = Main.plugin.executePersistenceRunnable(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    Object onlineTime = Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE uuid = ? AND leaved_at IS NOT NULL", UUIDUtils.toBytes(uuid));
                    if (onlineTime == null)
                        return 0l;
                    return Long.valueOf(String.valueOf(onlineTime));
                }
            }).get(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        MultiBungee MB = Main.getMB();
        return new FriendData(uuid, MB.getNameFromUuid(uuid), MB.isPlayerOnline(uuid), MB.getLastOnline(uuid), playTime);
    }

    @lombok.AllArgsConstructor
    public static class FriendData {
        UUID uuid;
        String name;
        boolean isOnline;
        long lastOnline, playTime;
    }
}
