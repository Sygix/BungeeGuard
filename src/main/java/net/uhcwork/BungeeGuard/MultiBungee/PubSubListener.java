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
import net.uhcwork.BungeeGuard.Models.BungeeCheat;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee (bungeeguard)
 * Date: 14/09/2014
 * Time: 21:09
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PubSubListener implements Listener {
    private final Main plugin;
    private final MultiBungee MB;

    public PubSubListener(Main plugin) {
        this.plugin = plugin;
        this.MB = Main.getMB();
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
            int amount = in.readInt();
            plugin.getWalletManager().addToBalance(uuid, amount);
        }
        if (subchannel.equals("cheat")) {
            String playerName = in.readUTF();
            String cheatName = in.readUTF();
            double score = in.readDouble();
            if (!(cheatName.equals("ForceField") || cheatName.equals("moving.survivalfly"))) {
                return;
            }
            BungeeCheat BC = new BungeeCheat();
            BC.setPlayerName(playerName);
            BC.setServerName(sender.getInfo().getName());
            BC.setPlayerUUID(MB.getUuidFromName(playerName));
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
        for (final String serverName : ProxyServer.getInstance().getServers().keySet()) {
            pingBack = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    byte[] data;
                    ByteArrayDataOutput out;
                    out = ByteStreams.newDataOutput();
                    out.writeUTF("PingServers");
                    out.writeUTF(serverName);
                    out.writeUTF(Main.getPrettyServerName(serverName));
                    out.writeUTF(Main.getShortServerName(serverName));
                    if (throwable != null) {
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
            plugin.getServerManager().ping(serverName, pingBack);
        }
    }
}
