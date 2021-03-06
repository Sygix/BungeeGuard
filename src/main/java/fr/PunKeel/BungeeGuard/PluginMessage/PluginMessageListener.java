package fr.PunKeel.BungeeGuard.PluginMessage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.ServerManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginMessageListener implements Listener {
    private final Main plugin;
    private final MultiBungee MB;
    private final ServerManager SM;

    public PluginMessageListener(Main plugin) {
        this.plugin = plugin;
        this.MB = Main.getMB();
        SM = Main.getServerManager();
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent e) {
        if (!(e.getSender() instanceof Server && e.getReceiver() instanceof ProxiedPlayer))
            return;
        if (!e.getTag().equals("UHCGames"))
            return;
        Server sender = (Server) e.getSender();
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

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

        if (subchannel.equals("Party")) {
            PartyHandler.handle(plugin, in, out, p);
        }
        if (subchannel.equals("Friend")) {
            FriendHandler.handle(plugin, in, out, p);
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

        if (subchannel.equals("ignore")) {
            boolean new_state = in.readBoolean();
            MB.ignorePlayer(p.getUniqueId(), new_state ? '+' : '-', null);
        }

        if (subchannel.equalsIgnoreCase("Message")) {
            String playerName = in.readUTF();
            UUID u = MB.getUuidFromName(playerName);
            MB.sendPlayerMessage(u, in.readUTF());
        }

        if (subchannel.equalsIgnoreCase("KickPlayer")) {
            String playerName = in.readUTF();
            String reason = in.readUTF();
            if (playerName.length() > 16)
                MB.kickPlayer(UUID.fromString(playerName), reason);
            else
                MB.kickPlayer(playerName, reason);
        }

        if (subchannel.equals("addCoins")) {
            UUID uuid = UUID.fromString(in.readUTF());
            double amount = in.readDouble();
            plugin.getWalletManager().addToBalance(uuid, amount);
        }

        if (subchannel.equalsIgnoreCase("cheat")) {
            String playerName = in.readUTF();
            String cheatName = in.readUTF();
            double score = in.readDouble();
            if (cheatName.length() > 3) {
                return;
            }
            plugin.getCheatManager().addCheat(sender.getInfo().getName(), playerName, cheatName, score);
        }

        if (subchannel.equalsIgnoreCase("GetLobbies")) {
            out.writeUTF("GetLobbies");
            out.writeUTF(Main.getGson().toJson(Main.getServerManager().getLobbiesInfo()));
        }

        if (subchannel.equalsIgnoreCase("BanCheat")) {
            String playerName = in.readUTF().replaceAll("\\s+", "");
            if (plugin.getSanctionManager().findBan(MB.getUuidFromName(playerName)) != null)
                return;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "ban " + playerName + " 1mo cheat");
        }

        byte[] data = out.toByteArray();
        if (data.length != 0)
            sender.sendData("UHCGames", data);

    }

    private void sendServersPing(final Server sender) {
        Callback<ServerPing> pingBack;
        Set<String> servers = new CopyOnWriteArraySet<>(ProxyServer.getInstance().getServers().keySet());
        for (final String serverName : servers) {
            final String prettyName = SM.getPrettyName(serverName);
            final String shortName = SM.getShortName(serverName);
            pingBack = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    byte[] data;
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("PingServers");
                    out.writeUTF(serverName);
                    out.writeUTF(prettyName);
                    out.writeUTF(shortName);
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
