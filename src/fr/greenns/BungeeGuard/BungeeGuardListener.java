package fr.greenns.BungeeGuard;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import fr.greenns.BungeeGuard.Ban.Ban;
import fr.greenns.BungeeGuard.Ban.BanType;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import fr.greenns.BungeeGuard.Party.*;
import fr.greenns.BungeeGuard.PubSub.*;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import fr.greenns.BungeeGuard.utils.Permissions;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BungeeGuardListener implements Listener {

    public Main plugin;

    public BungeeGuardListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        String hostString = event.getConnection().getVirtualHost().getHostString();
        if (!hostString.equalsIgnoreCase("mc.uhcgames.com") && !hostString.equalsIgnoreCase("build.uhcgames.com")) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "" + ChatColor.BOLD + "Merci de vous connecter avec " + '\n' + ChatColor.WHITE + "" + ChatColor.BOLD + "MC" + ChatColor.AQUA + "" + ChatColor.BOLD + ".uhcgames.com");
            return;
        }
        Ban BannedUser = BungeeGuardUtils.getBan(event.getConnection().getUniqueId());
        if (BannedUser != null) {
            if (BannedUser.isDefBanned()) {
                event.setCancelled(true);

                BanType BanType = (BannedUser.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.PERMANENT;
                String CancelMsg = BanType.kickFormat("", BannedUser.getReason());

                event.setCancelReason(CancelMsg);
                return;
            } else if (BannedUser.isBanned()) {
                event.setCancelled(true);

                String durationStr = BungeeGuardUtils.getDuration(BannedUser.getUntilTimestamp());
                BanType BanType = (BannedUser.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT;
                String CancelMsg = BanType.kickFormat(durationStr, BannedUser.getReason());

                event.setCancelReason(CancelMsg);
                return;
            } else {
                BannedUser.removeFromBDD("TimeEnd", "Automatique");
            }
        }

        Lobby l = plugin.lobbyUtils.bestLobbyTarget();
        if (l != null) {
            return;
        }
        event.setCancelled(true);
        event.setCancelReason(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants");
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (e.getTarget().getName().equalsIgnoreCase("hub")) {
            System.out.println("Recuperation du meilleur lobby pour " + p.getName());
            Lobby l = plugin.lobbyUtils.bestLobbyTarget();

            if (l != null) {
                e.setTarget(l.getServerInfo());
                System.out.println("Lobby selectionné: " + l.getName());

            } else {
                if (BungeeCord.getInstance().getServerInfo("limbo").getPlayers().size() < 70) {
                    e.setTarget(BungeeCord.getInstance().getServerInfo("limbo"));
                }
                e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants").create());
            }
        } else {
            final Party party = plugin.getPM().getPartyByPlayer(p);
            if (party != null && party.isOwner(p)) {
                plugin.getMB().summonParty(party.getName(), e.getTarget().getName());
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        final ProxiedPlayer p = event.getPlayer();
        if (plugin.gtp.containsKey(p.getUniqueId())) {
            BungeeCord.getInstance().getScheduler().schedule(plugin, new Runnable() {
                @Override
                public void run() {
                    p.chat("/tp " + plugin.gtp.get(p.getUniqueId()));
                    plugin.gtp.remove(p.getUniqueId());
                }
            }, 10, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();

        if (!e.getMessage().startsWith("/") && (e.getSender() instanceof ProxiedPlayer)) {

            if (e.isCommand()) {
                return;
            }
            Mute MuteUser = BungeeGuardUtils.getMute(p.getUniqueId());
            if (MuteUser != null) {
                if (MuteUser.isMute()) {
                    MuteType MuteType = (MuteUser.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
                    String muteDurationStr = BungeeGuardUtils.getDuration(MuteUser.getTime());
                    String MuteMsg = MuteType.playerFormat(muteDurationStr, MuteUser.getReason());
                    p.sendMessage(new ComponentBuilder(MuteMsg).create());
                    e.setCancelled(true);
                } else {
                    MuteUser.removeFromBDD("TimeEnd", "Automatique");
                }
            }
            if (plugin.serv.contains(p.getServer().getInfo().getName())) {
                if (p.hasPermission("bungeeguard.bypasschat")) {
                    return;
                }
                e.setCancelled(true);
                p.sendMessage(ComponentManager.generate(ChatColor.RED + "Le chat est désactivé temporairement !"));
            }
            if ((p.hasPermission("bungeeguard.staffchat")) && (e.getMessage().startsWith("!!"))) {
                e.setCancelled(true);
                plugin.getMB().staffChat(p.getServer().getInfo().getName(), p.getName(), e.getMessage().substring(2));
                e.setMessage("");

                return;
            }
            Party party = plugin.getPM().getPartyByPlayer(p);
            if (party != null && party.isPartyChat(p)) {
                plugin.getMB().partyChat(party.getName(), p.getUniqueId(), e.getMessage());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (plugin.getPM().inParty(p)) {
            plugin.getMB().playerLeaveParty(plugin.getPM().getPartyByPlayer(p), p);
        }
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e) {
        ServerPing sp = e.getResponse();
        sp.getPlayers().setOnline(BungeeGuardUtils.getMB().getPlayerCount());
        sp.setDescription(plugin.getMotd());

        List<String> lines = new ArrayList<>();

        lines.add(ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         " + ChatColor.RESET + "" + ChatColor.BOLD + "«" + ChatColor.GOLD + "" + ChatColor.BOLD + " UHC " + ChatColor.AQUA + "" + ChatColor.BOLD + "Network " + ChatColor.RESET + "" + ChatColor.BOLD + "»" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         ");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Un serveur de jeux UltraHardCore !");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "  Stress, Difficulté, Travail d'équipe");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "      Vous allez aimer UHCGames !");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.RED + "Kill The Patrick");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.YELLOW + "Ultra HungerGames");
        lines.add(ChatColor.GRAY + "➟" + ChatColor.BLUE + "Rush");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.AQUA + "Fatality");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.LIGHT_PURPLE + "Tower");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.GREEN + "FightOnFaces");
        lines.add(ChatColor.GRAY + "Et bien d'autres jeux ...");


        ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ServerPing.PlayerInfo(lines.get(i), "");
        }
        e.getResponse().getPlayers().setSample(players);
    }

    @SuppressWarnings({"deprecation"})
    @EventHandler
    public void onServerTurnOff(final ServerKickEvent event) {
        ProxiedPlayer p = event.getPlayer();
        String reason = "";
        ServerInfo kickedFrom = event.getKickedFrom();

        for (BaseComponent b : event.getKickReasonComponent()) {
            reason += b.toPlainText() + "\n";
        }
        reason = reason.trim();

        if (!(reason.contains("ban") || reason.contains("plein") ||
                reason.contains("Full") || reason.contains("fly") ||
                reason.contains("Nos services") || reason.contains("kické") ||
                reason.contains("bannis") || reason.contains("maintenance") ||
                reason.contains("kick") || reason.contains("VIP"))) {

            if (reason.contains("closed")) {
                if (kickedFrom.getName().contains("lobby")) {
                    Lobby Lobby = plugin.lobbyUtils.getLobby(kickedFrom.getName());
                    Lobby.setOffline();
                }
            }

            Lobby l = plugin.lobbyUtils.bestLobbyTarget();
            ServerInfo server = BungeeCord.getInstance().getServerInfo("limbo");
            if (l != null) {
                server = l.getServerInfo();
            }

            BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " a perdu la connection (" + event.getState().toString() + " - " + reason + ")"));
            if (server.getName().equals("limbo") && BungeeCord.getInstance().getServerInfo("limbo").getPlayers().size() > 70) {
                BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " déconnecté "));
            } else {
                BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " Redirigé vers " + server.getName().toUpperCase()));
                p.setReconnectServer(server);
                event.setCancelled(true);
                event.setCancelServer(server);
            }
        } else {
            p.disconnect(event.getKickReasonComponent());
        }
    }

    @EventHandler
    public void onTabCompleteEvent(TabCompleteEvent e) {
        String[] Message = e.getCursor().split("\\s+");
        String DebutDePseudo = Message[Message.length - 1].toLowerCase();
        for (String p : plugin.getMB().getHumanPlayersOnline()) {
            if (p.toLowerCase().startsWith(DebutDePseudo) && !e.getSuggestions().contains(p))
                e.getSuggestions().add(p);
        }
    }

    @EventHandler
    public void onPubSubMessageEvent(PubSubMessageEvent e) {
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

    @EventHandler
    public void onPermCheck(PermissionCheckEvent e) {
        e.setHasPermission(Permissions.hasPerm(e.getSender().getName(), e.getPermission()));
    }
}
