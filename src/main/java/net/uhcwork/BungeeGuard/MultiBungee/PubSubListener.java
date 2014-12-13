package net.uhcwork.BungeeGuard.MultiBungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.ServerManager;
import net.uhcwork.BungeeGuard.Models.BungeeCheat;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class PubSubListener implements Listener {
    private final Main plugin;
    private final MultiBungee MB;
    private final ServerManager SM;

    public PubSubListener(Main plugin) {
        this.plugin = plugin;
        this.MB = Main.getMB();
        SM = Main.getServerManager();
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent e) {
        if (!(e.getSender() instanceof Server))
            return;
        if (!e.getTag().equals("UHCGames"))
            return;
        Server sender = (Server) e.getSender();

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String subchannel = in.readUTF();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        if (subchannel.equals("ConnectOther")) {
            String playerName = in.readUTF();
            String serverName = in.readUTF();
            MB.summon(playerName, serverName, "");
        }

        if (subchannel.equals("PlayerCount")) {
            String target = in.readUTF();
            out.writeUTF("PlayerCount");
            if (target.equals("ALL")) {
                out.writeUTF("ALL");
                out.writeInt(MB.getPlayerCount());
            } else {
                if (ProxyServer.getInstance().getServerInfo(target) == null)
                    // Server does not exist.
                    return;
                out.writeUTF(target);
                out.writeInt(MB.getPlayersOnServer(target).size());
            }
        }


        if (subchannel.equals("PingServers")) {
            sendServersPing(sender);
        }

        if (subchannel.equals("PlayerList")) {
            String target = in.readUTF();
            out.writeUTF("PlayerList");
            if (target.equals("ALL")) {
                out.writeUTF("ALL");
                out.writeUTF(Util.csv(MB.getPlayersOnline()));
            } else {
                if (ProxyServer.getInstance().getServerInfo(target) == null)
                    // Server does not exist.
                    return;
                out.writeUTF(target);
                out.writeUTF(Util.csv(MB.getPlayersOnServer(target)));
            }
        }

        if (subchannel.equals("UUIDOther")) {
            String playerName = in.readUTF();
            out.writeUTF("UUIDOther");
            out.writeUTF(playerName);
            out.writeUTF("" + MB.getUuidFromName(playerName));
        }

        if (subchannel.equals("Message")) {
            String playerName = in.readUTF();
            UUID u = MB.getUuidFromName(playerName);
            MB.sendPlayerMessage(u, in.readUTF());
        }

        if (subchannel.equals("KickPlayer")) {
            String playerName = in.readUTF();
            String reason = in.readUTF();
            MB.kickPlayer(playerName, reason);
        }

        if (subchannel.equals("addCoins")) {
            UUID uuid = UUID.fromString(in.readUTF());
            double amount = in.readDouble();
            plugin.getWalletManager().addToBalance(uuid, amount);
        }
        if (subchannel.equals("cheat")) {
            String playerName = in.readUTF();
            String cheatName = in.readUTF();
            double score = in.readDouble();
            if (cheatName.length() > 3) {
                return;
            }
            BungeeCheat BC = new BungeeCheat();
            BC.setPlayerName(playerName);
            BC.setServerName(sender.getInfo().getName());
            BC.setCheatType(cheatName);
            BC.setCheatScore(score);
            plugin.executePersistenceRunnable(new SaveRunner(BC));
        }

        byte[] data = out.toByteArray();
        if (data.length != 0)
            sender.sendData("UHCGames", data);

    }

    private void sendServersPing(final Server sender) {
        Callback<ServerPing> pingBack;
        Set<String> servers = new CopyOnWriteArraySet<>(ProxyServer.getInstance().getServers().keySet());
        for (final String serverName : servers) {
            pingBack = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    byte[] data;
                    ByteArrayDataOutput out;
                    out = ByteStreams.newDataOutput();
                    out.writeUTF("PingServers");
                    out.writeUTF(serverName);
                    out.writeUTF(SM.getPrettyName(serverName));
                    out.writeUTF(SM.getShortName(serverName));
                    if (throwable != null || serverPing == null) {
                        out.writeInt(-1);
                    } else {
                        out.writeInt(serverPing.getPlayers().getOnline());
                        out.writeInt(serverPing.getPlayers().getMax());
                        out.writeUTF(serverPing.getDescription());
                    }
                    data = out.toByteArray();
                    if (data.length != 0)
                        sender.sendData("UHCGames", data);
                }
            };
            Main.getServerManager().ping(serverName, pingBack);
        }
    }
}
