package fr.greenns.BungeeGuard.MultiBungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.*;
import fr.greenns.BungeeGuard.Party.*;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.MultiBungee (bungeeguard)
 * Date: 14/09/2014
 * Time: 21:09
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PubSubListener implements Listener {
    Main plugin;
    MultiBungee MB;

    public PubSubListener(Main plugin) {
        this.plugin = plugin;
        this.MB = plugin.getMB();
    }

    @EventHandler
    public void onPubSubMessageEvent(com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent e) {
        String message = e.getMessage();
        String channel = e.getChannel();
        String[] args = message.split(MultiBungee.REGEX_SEPARATOR);
        PubSubBase handler = new PubSubBase() {
        };
        if (channel.startsWith("@" + plugin.getMB().getServerId() + "/")) {
            // Si channel ressemble à @serveur/commande, on retire le préfixe :]
            channel = channel.replace("@" + plugin.getMB().getServerId() + "/", "@");
        }

        switch (channel) {
            case "redisbungee-data":
                return;
            case "notifyStaff":
                handler = new StaffNotificationHandler(plugin);
                break;
            case "kick":
                handler = new KickHandler();
                break;
            case "mute":
                handler = new MuteHandler();
                break;
            case "message":
                handler = new MessageHandler();
                break;
            case "ban":
                handler = new BanHandler();
                break;
            case "broadcast":
                handler = new BroadcastHandler();
                break;
            case "privateMessage":
                handler = new PrivateMessageHandler(plugin);
                break;
            case "unmute":
                handler = new UnmuteHandler();
                break;
            case "unban":
                handler = new UnBanHandler();
                break;
            case "staffChat":
                handler = new StaffChatHandler();
                break;
            case "reloadConf":
                handler = new ReloadConfHandler(plugin);
                break;
            case "summon":
                handler = new SummonHandler();
                break;
            case "ignore":
                handler = new IgnoreHandler(plugin);
                break;
            case "silenceServer":
                handler = new ServerSilenceHandler(plugin);
                break;
            case "inviteParty":
                handler = new PartyInviteHandler(plugin);
                break;
            case "setPartyPublique":
                handler = new PartyPubliqueHandler(plugin);
                break;
            case "playerLeaveParty":
                handler = new PartyPlayerLeaveHandler(plugin);
                break;
            case "setPartyChat":
                handler = new PartyChatSetHandler(plugin);
                break;
            case "addPartyMember":
                handler = new PartyAddMemberHandler(plugin);
                break;
            case "setPartyOwner":
                handler = new PartyOwnerSetHandler(plugin);
                break;
            case "kickFromParty":
                handler = new PartyKickHandler(plugin);
                break;
            case "partyChat":
                handler = new PartyChatHandler(plugin);
                break;
            case "summonParty":
                handler = new PartySummonHandler(plugin);
                break;
            case "createParty":
                handler = new PartyCreateHandler(plugin);
                break;
            case "disbandParty":
                handler = new PartyDisbandHandler(plugin);
                break;

            case "@partyRequest":
                handler = new PartyRequestHandler(plugin);
                break;
            case "@partyReply":
                handler = new PartyReplyHandler(plugin);
                break;


        }
        if (handler.ignoreSelfMessage() && args.length != 0 && args[0].equals(plugin.getMB().getServerId()))
            return;
        handler.handle(channel, message, args);
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
            plugin.getMB().summon(playerName, serverName, "");
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

        byte[] data = out.toByteArray();
        if (data.length != 0)
            sender.sendData("UHCGames", data);

    }
}
