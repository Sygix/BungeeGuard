package fr.greenns.BungeeGuard.MultiBungee;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.*;
import fr.greenns.BungeeGuard.Party.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Part of fr.greenns.BungeeGuard.MultiBungee (bungeeguard)
 * Date: 14/09/2014
 * Time: 21:09
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PubSubListener implements Listener {
    Main plugin;

    public PubSubListener(Main plugin) {
        this.plugin = plugin;
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
}
