package net.uhcwork.BungeeGuard;

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
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Handshake;
import net.uhcwork.BungeeGuard.Managers.PartyManager;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import net.uhcwork.BungeeGuard.Models.BungeeLitycs;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.Permissions.Permissions;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BungeeGuardListener implements Listener {
    private static final ServerPing.PlayerInfo[] playersPing;
    private static final Map<UUID, BungeeLitycs> bungeelitycs = new ConcurrentHashMap<>();

    static {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         " + ChatColor.RESET + "" + ChatColor.BOLD + "«" + ChatColor.GOLD + "" + ChatColor.BOLD + " UHC " + ChatColor.AQUA + "" + ChatColor.BOLD + "Network " + ChatColor.RESET + "" + ChatColor.BOLD + "»" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         ");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Un serveur de jeux UltraHardCore !");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "  Stress, Difficulté, Travail d'équipe");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "      Vous allez aimer UHCGames !");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.RED + "Kill The Patrick");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.YELLOW + "Ultra HungerGames");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.BLUE + "Rush");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.AQUA + "Fatality");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.LIGHT_PURPLE + "Tower");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.GREEN + "FightOnFaces");
        lines.add(ChatColor.GRAY + "Et bien d'autres jeux ...");


        ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ServerPing.PlayerInfo(lines.get(i), "");
        }
        playersPing = players;
    }

    private final Main plugin;
    private final BaseComponent[] header = new ComponentBuilder("MC.UHCGames.COM")
            .color(ChatColor.GOLD)
            .bold(true).create();
    private final BaseComponent[] footer = new ComponentBuilder("Store")
            .color(ChatColor.RED)
            .bold(true)
            .append(".UHCGames.com")
            .bold(true)
            .color(ChatColor.AQUA).create();
    private String fullNotVIP = "" + ChatColor.YELLOW + ChatColor.BOLD + "Le serveur est plein" +
            ChatColor.GOLD + ChatColor.BOLD + "\nVous pourrez le rejoindre en devenant VIP !" +
            ChatColor.RED + ChatColor.BOLD + "\nAchetez-le sur " +
            ChatColor.WHITE + ChatColor.BOLD + "https://store.uhcgames.com/";
    private Method handshakeMethod = null;

    public BungeeGuardListener(Main plugin) {
        this.plugin = plugin;
        try {
            Class<?> initialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
            handshakeMethod = initialHandler.getDeclaredMethod("getHandshake");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        event.registerIntent(plugin);
        System.out.println(event.getConnection().getUniqueId());
        if (Main.getMB().getPlayerCount() > plugin.getConfig().getMaxPlayers()) {
            if (!Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.join_full")) {
                event.setCancelled(true);
                event.setCancelReason(fullNotVIP);
                event.completeIntent(plugin);
                return;
            }
        }
        String hostString = event.getConnection().getVirtualHost().getHostString().toLowerCase();
        if (!Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.canBypassHost") &&
                !plugin.getConfig().getForcedHosts().containsKey(hostString)) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "" + ChatColor.BOLD + "Merci de vous connecter avec " + '\n' + ChatColor.WHITE + "" + ChatColor.BOLD + "MC" + ChatColor.AQUA + "" + ChatColor.BOLD + ".uhcgames.com");
        } else {
            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getWalletManager().getAccount(event.getConnection().getUniqueId());
                }
            }, 10, TimeUnit.MILLISECONDS);

            BungeeBan ban = plugin.getSanctionManager().findBan(event.getConnection().getUniqueId());
            if (ban != null) {
                if (ban.isBanned()) {
                    event.setCancelled(true);
                    event.setCancelReason(ban.getBanMessage());
                    event.completeIntent(plugin);
                    return;
                }
                plugin.getSanctionManager().unban(ban, "TimeEnd", "Automatique", true);
                Main.getMB().unban(event.getConnection().getUniqueId());
            }
        }
        event.completeIntent(plugin);
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();

        p.setTabHeader(header, footer);

        if (e.getTarget().getName().equalsIgnoreCase("hub")) {
            System.out.println("Recuperation du meilleur lobby pour " + p.getName());
            String l = plugin.getServerManager().getBestLobbyFor(p);
            if (l != null) {
                e.setTarget(plugin.getProxy().getServerInfo(l));
                System.out.println("Lobby selectionné: " + l);
            } else {
                if (ProxyServer.getInstance().getServerInfo("limbo").getPlayers().size() < 70) {
                    e.setTarget(ProxyServer.getInstance().getServerInfo("limbo"));
                }
                e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants").create());
            }
        } else if (!e.getTarget().getName().startsWith("lobby")) {
            final PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && party.isOwner(p)) {
                Main.getMB().summonParty(party.getName(), e.getTarget().getName());
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String message = e.getMessage();
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.startsWith("connected with") && lowerMessage.endsWith("minechat")) {
            e.setCancelled(true);
            return;
        }
        if (!p.hasPermission("bungee.can.repeat_message")) {
            plugin.getAntiSpamListener().onChat(e);
        }
        if (!p.hasPermission("bungee.admin")) {
            if (Permissions.miniglob(plugin.getForbiddenCommands(), lowerMessage)) {
                e.setMessage("");
                e.setCancelled(true);
                return;
            }
        }
        if (e.isCancelled())
            return;
        if (!lowerMessage.startsWith("/") && (e.getSender() instanceof ProxiedPlayer)) {
            if (e.isCommand()) {
                return;
            }
            BungeeMute mute = plugin.getSanctionManager().findMute(p.getUniqueId());
            if (mute != null) {
                if (mute.isMute()) {
                    p.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
                    e.setCancelled(true);
                    return;
                } else {
                    plugin.getSanctionManager().unmute(mute, "TimeEnd", "Automatique", true);
                    Main.getMB().unmutePlayer(p.getUniqueId());
                }
            }
            if ((p.hasPermission("bungee.staffchat")) && (e.getMessage().startsWith("!!"))) {
                e.setCancelled(true);
                Main.getMB().staffChat(p.getServer().getInfo().getName(), p.getName(), e.getMessage().substring(2));
                e.setMessage("");

                return;
            }
            PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && party.isPartyChat(p)) {
                Main.getMB().partyChat(party.getName(), p.getUniqueId(), e.getMessage());
                e.setCancelled(true);
            }
            if (plugin.isSilenced(p.getServer().getInfo().getName())) {
                if (!p.hasPermission("bungee.bypasschat")) {
                    e.setCancelled(true);
                    p.sendMessage(new TextComponent(ChatColor.RED + "Le chat est désactivé temporairement !"));
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (plugin.getPartyManager().inParty(p)) {
            Main.getMB().playerLeaveParty(plugin.getPartyManager().getPartyByPlayer(p), p);
        }
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            final BungeeLitycs old_bl = bungeelitycs.get(p.getUniqueId());
            old_bl.leave(p);
            plugin.executePersistenceRunnable(new SaveRunner(old_bl));
            bungeelitycs.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e) {
        e.registerIntent(plugin);
        ServerPing sp = e.getResponse();
        sp.getPlayers().setMax(plugin.getConfig().getMaxPlayers());
        sp.getPlayers().setOnline(Main.getMB().getPlayerCount());
        sp.setDescription(plugin.getConfig().getMotd());
        e.getResponse().getPlayers().setSample(playersPing);
        e.completeIntent(plugin);
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
                plugin.getServerManager().setOffline(kickedFrom.getName());
            }

            String l = plugin.getServerManager().getBestLobbyFor(p);
            ServerInfo server = plugin.getProxy().getServerInfo(l);


            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "[BungeeGuard] " + p.getName() + " a perdu la connection (" + e.getState().toString() + " - " + reason + ")"));
            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "[BungeeGuard] " + p.getName() + " Redirigé vers " + Main.getPrettyServerName(server.getName())));
            p.setReconnectServer(server);
            e.setCancelled(true);
            e.setCancelServer(server);
            return;
        } else {
            p.disconnect(e.getKickReasonComponent());
        }
        if (e.isCancelled())
            return;
        if (plugin.getPartyManager().inParty(p)) {
            Main.getMB().playerLeaveParty(plugin.getPartyManager().getPartyByPlayer(p), p);
        }
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            final BungeeLitycs old_bl = bungeelitycs.get(p.getUniqueId());
            old_bl.leave(p);
            plugin.executePersistenceRunnable(new SaveRunner(old_bl));
            bungeelitycs.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onTabCompleteEvent(TabCompleteEvent e) {
        String[] args = e.getCursor().split(" ");

        final String checked = (args.length > 0 ? args[args.length - 1] : e.getCursor()).toLowerCase();
        for (String playerName : Main.getMB().getHumanPlayersOnline()) {
            if (playerName.toLowerCase().startsWith(checked)) {
                e.getSuggestions().add(playerName);
            }
        }
    }

    @EventHandler
    public void onPermCheck(PermissionCheckEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            e.setHasPermission(Permissions.hasPerm(p.getUniqueId(), e.getPermission()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (e.isCancelled())
            return;

        try {
            Handshake h = (Handshake) handshakeMethod.invoke(p.getPendingConnection());
            Map<String, Object> data = new HashMap<>();
            data.put("server_id", e.getTarget().getName());
            data.put("groupes", plugin.getPermissionManager().getGroupes(p.getUniqueId()));
            h.setHost(Main.getGson().toJson(data));
            System.out.println(Main.getGson().toJson(data));
        } catch (IllegalAccessException | InvocationTargetException e1) {
            System.out.println("Erreur passage hostname: " + e1.getMessage());
        }
        final BungeeLitycs old_bl;
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            old_bl = bungeelitycs.get(p.getUniqueId());
            bungeelitycs.remove(p.getUniqueId());
        } else
            old_bl = null;
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                if (old_bl != null) {
                    old_bl.leave(p);
                    old_bl.saveIt();
                }
                BungeeLitycs bl = new BungeeLitycs();
                bl.join(p, e.getTarget());
                bl.saveIt();
                bungeelitycs.put(p.getUniqueId(), bl);
            }
        });
    }
}
