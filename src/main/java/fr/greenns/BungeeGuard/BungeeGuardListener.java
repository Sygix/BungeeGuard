package fr.greenns.BungeeGuard;

import fr.greenns.BungeeGuard.Ban.BanType;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Models.BungeeBan;
import fr.greenns.BungeeGuard.Models.BungeeMute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import fr.greenns.BungeeGuard.Party.Party;
import fr.greenns.BungeeGuard.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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
        if (!Permissions.hasPerm(event.getConnection().getName(), "bungee.canBypassHost") &&
                !event.getConnection().getListener().getForcedHosts().containsKey(hostString)) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "" + ChatColor.BOLD + "Merci de vous connecter avec " + '\n' + ChatColor.WHITE + "" + ChatColor.BOLD + "MC" + ChatColor.AQUA + "" + ChatColor.BOLD + ".uhcgames.com");
            return;
        }
        BungeeBan ban = BungeeGuardUtils.getBan(event.getConnection().getUniqueId());
        if (ban != null) {
            if (ban.isDefBanned()) {
                event.setCancelled(true);

                BanType banType = (ban.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.PERMANENT;
                String kickMessage = banType.kickFormat("", ban.getReason());

                event.setCancelReason(kickMessage);
                return;
            }
            if (ban.isBanned()) {
                event.setCancelled(true);

                String durationStr = BungeeGuardUtils.getDuration(ban.getUntilTimestamp());
                BanType banType = (ban.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT;
                String kickMessage = banType.kickFormat(durationStr, ban.getReason());

                event.setCancelReason(kickMessage);
                return;
            }
            plugin.getBM().unban(ban, "TimeEnd", "Automatique", true);
            plugin.getMB().unban(event.getConnection().getUniqueId());
        }
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (e.getTarget().getName().equalsIgnoreCase("hub")) {
            System.out.println("Recuperation du meilleur lobby pour " + p.getName());
            Lobby l = plugin.getLM().getBestLobbyFor(p);

            if (l != null) {
                e.setTarget(l.getServerInfo());
                System.out.println("Lobby selectionné: " + l.getName());

            } else {
                if (ProxyServer.getInstance().getServerInfo("limbo").getPlayers().size() < 70) {
                    e.setTarget(ProxyServer.getInstance().getServerInfo("limbo"));
                }
                e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants").create());
            }
        } else if (!e.getTarget().getName().startsWith("lobby")) {
            final Party party = plugin.getPM().getPartyByPlayer(p);
            if (party != null && party.isOwner(p)) {
                plugin.getMB().summonParty(party.getName(), e.getTarget().getName());
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        final ProxiedPlayer p = event.getPlayer();
        if (plugin.getGTP().containsKey(p.getUniqueId())) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                @Override
                public void run() {
                    p.chat("/tp " + plugin.getGTP().get(p.getUniqueId()));
                    plugin.getGTP().remove(p.getUniqueId());
                }
            }, 10, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();

        if (!p.hasPermission("bungee.admin")) {
            plugin.getAS().onChat(e);

            if (Permissions.miniglob(plugin.getForbiddenCommands(), e.getMessage())) {
                e.setMessage("");
                e.setCancelled(true);
                return;
            }
        }
        if (e.isCancelled())
            return;
        if (!e.getMessage().startsWith("/") && (e.getSender() instanceof ProxiedPlayer)) {

            if (e.isCommand()) {
                return;
            }
            BungeeMute mute = BungeeGuardUtils.getMute(p.getUniqueId());
            if (mute != null) {
                if (mute.isMute()) {
                    MuteType MuteType = (mute.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
                    String muteDurationStr = BungeeGuardUtils.getDuration(mute.getUntilTimestamp());
                    String MuteMsg = MuteType.playerFormat(muteDurationStr, mute.getReason());
                    p.sendMessage(TextComponent.fromLegacyText(MuteMsg));
                    e.setCancelled(true);
                } else {
                    plugin.getMM().unmute(mute, "TimeEnd", "Automatique", true);
                    plugin.getMB().unmutePlayer(p.getUniqueId());
                }
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
            if (plugin.isSilenced(p.getServer().getInfo().getName())) {
                if (!p.hasPermission("bungeeguard.bypasschat")) {
                    e.setCancelled(true);
                    p.sendMessage(new TextComponent(ChatColor.RED + "Le chat est désactivé temporairement !"));
                    return;
                }
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

    @EventHandler
    public void onServerTurnOff(final ServerKickEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String reason = "";
        ServerInfo kickedFrom = e.getKickedFrom();

        for (BaseComponent b : e.getKickReasonComponent()) {
            reason += b.toPlainText() + "\n";
        }
        reason = reason.trim();

        if (!(reason.contains("ban") || reason.contains("Full") || reason.contains("fly") ||
                reason.contains("Nos services") || reason.contains("kické") ||
                reason.contains("bannis") || reason.contains("maintenance") ||
                reason.contains("kick") || reason.contains("VIP"))) {

            if (reason.contains("closed")) {
                if (kickedFrom.getName().startsWith("lobby")) {
                    Lobby Lobby = plugin.getLM().getLobby(kickedFrom.getName());
                    Lobby.setOffline();
                }
            }

            Lobby l = plugin.getLM().getBestLobbyFor(p);
            ServerInfo server = ProxyServer.getInstance().getServerInfo("limbo");
            if (l != null) {
                server = l.getServerInfo();
            }

            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " a perdu la connection (" + e.getState().toString() + " - " + reason + ")"));
            if (server.getName().equals("limbo") && ProxyServer.getInstance().getServerInfo("limbo").getPlayers().size() > 70) {
                ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " déconnecté "));
            } else {
                ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + p.getName() + " Redirigé vers " + server.getName().toUpperCase()));
                p.setReconnectServer(server);
                e.setCancelled(true);
                e.setCancelServer(server);
                return;
            }
        } else {
            p.disconnect(e.getKickReasonComponent());
        }

        if (plugin.getPM().inParty(p)) {
            plugin.getMB().playerLeaveParty(plugin.getPM().getPartyByPlayer(p), p);
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
    public void onPermCheck(PermissionCheckEvent e) {
        e.setHasPermission(Permissions.hasPerm(e.getSender().getName(), e.getPermission()));
    }
}
