package net.uhcwork.BungeeGuard;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.uhcwork.BungeeGuard.Announces.AnnouncementManager;
import net.uhcwork.BungeeGuard.Announces.AnnouncementTask;
import net.uhcwork.BungeeGuard.AntiSpam.AntiSpamListener;
import net.uhcwork.BungeeGuard.Ban.BanManager;
import net.uhcwork.BungeeGuard.Ban.CommandBan;
import net.uhcwork.BungeeGuard.Ban.CommandUnban;
import net.uhcwork.BungeeGuard.Config.MysqlConfigAdapter;
import net.uhcwork.BungeeGuard.Ignore.CommandIgnore;
import net.uhcwork.BungeeGuard.Ignore.IgnoreManager;
import net.uhcwork.BungeeGuard.Kick.CommandKick;
import net.uhcwork.BungeeGuard.Lobbies.CommandLobby;
import net.uhcwork.BungeeGuard.Lobbies.LobbyManager;
import net.uhcwork.BungeeGuard.Models.BungeeBlockedCommands;
import net.uhcwork.BungeeGuard.Models.BungeePremadeMessage;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.ReloadConfHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubListener;
import net.uhcwork.BungeeGuard.MultiBungee.RedisBungeeListener;
import net.uhcwork.BungeeGuard.Mute.CommandMute;
import net.uhcwork.BungeeGuard.Mute.CommandUnmute;
import net.uhcwork.BungeeGuard.Mute.MuteManager;
import net.uhcwork.BungeeGuard.Party.CommandParty;
import net.uhcwork.BungeeGuard.Party.PartyManager;
import net.uhcwork.BungeeGuard.Wallet.CommandPoints;
import net.uhcwork.BungeeGuard.Wallet.CommandWallet;
import net.uhcwork.BungeeGuard.Wallet.WalletManager;
import net.uhcwork.BungeeGuard.commands.*;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    public static Main plugin;
    @Getter
    public static Gson gson = new Gson();
    private static Connection db_co;
    private static Map<String, String> premadeMessages = new HashMap<>();
    private static List<String> forbiddenCommands = new ArrayList<>();
    @Getter
    @Setter
    private static String motd;
    @Getter
    private static MultiBungee MB = new MultiBungee();
    //private static HttpApi httpApi = new HttpApi();
    private static Map<UUID, UUID> reply = new HashMap<>();
    private static List<UUID> spy = new ArrayList<>();
    private static Map<String, String> prettyServerNames = new HashMap<>();
    private long startTime;
    private List<String> silencedServers = new ArrayList<>();
    private HashMap<UUID, String> gtp = new HashMap<>();
    @Getter
    private PartyManager PM = new PartyManager();
    @Getter
    private BanManager BM = new BanManager(this);
    @Getter
    private MuteManager MM = new MuteManager();
    @Getter
    private LobbyManager LM = new LobbyManager(this);
    @Getter
    private IgnoreManager IM = new IgnoreManager(this);
    @Getter
    private AntiSpamListener AS = new AntiSpamListener();
    @Getter
    private AnnouncementManager AM = new AnnouncementManager(this);
    private int broadcastDelay = 180;
    @Getter
    private WalletManager WM = new WalletManager(this);

    public static void getDb() {
        if (Base.hasConnection()) {
            return;
        }
        System.out.println("[ORM] Creation de la connexion SQL pour " + Thread.currentThread().toString() + " ... :(");
        System.out.println("[ORM] " + BungeeGuardUtils.getCallingMethodInfo());
        if (db_co == null) {
            DB db = new DB("default");
            db.open("com.mysql.jdbc.Driver", "jdbc:mysql://vm-db-01.uhcwork.net/plugin", "bungeecord", "ozXsw4FUKoR8jh");
            db_co = db.connection();
        } else {
            // Petit hack qui permet d'utiliser le même SQL dans tous les threads :]
            Base.attach(db_co);
        }
    }

    public static String getPrettyServerName(String name) {
        return prettyServerNames.containsKey(name) ? prettyServerNames.get(name) : name;
    }

    public void setPremadeMessages(List<BungeePremadeMessage> all) {
        premadeMessages.clear();
        for (BungeePremadeMessage message : all) {
            premadeMessages.put(message.getSlug().toLowerCase(), message.getText());
        }
    }

    public List<String> getForbiddenCommands() {
        return forbiddenCommands;
    }

    public void setForbiddenCommands(List<BungeeBlockedCommands> all) {
        forbiddenCommands.clear();
        for (BungeeBlockedCommands cmd : all) {
            forbiddenCommands.add(cmd.getCommand());
        }
    }

    @Override
    public void onLoad() {
        plugin = this;
        startTime = System.currentTimeMillis();
        //httpApi.onLoad(this);
        new BungeeGuardUtils(this);
        System.out.println("Welcome to MultiBungee ~ With ORM");
        getDb();
        ProxyServer.getInstance().setConfigurationAdapter(new MysqlConfigAdapter(this));
    }

    private void fetchParties() {
        List<String> server = MB.getAllServers();
        PM = new PartyManager();
        for (String s : server) {
            if (!s.equals(MB.getServerId())) {
                System.out.println("RequestParties: " + s);
                MB.requestParties(s);
                return;
            }
        }

    }

    @Override
    public void onEnable() {
        //httpApi.onEnable();
        MB.init();
        MB.registerPubSubChannels("ban", "unban");
        MB.registerPubSubChannels("kick", "silenceServer");
        MB.registerPubSubChannels("mute", "unmute");
        MB.registerPubSubChannels("staffChat", "notifyStaff");
        MB.registerPubSubChannels("message", "privateMessage", "ignore", "broadcast");
        MB.registerPubSubChannels("reloadConf", "summon");
        MB.registerPubSubChannels("setPartyPublique", "inviteParty", "addPartyMember", "playerLeaveParty", "setPartyChat",
                "setPartyOwner", "kickFromParty", "summonParty", "partyChat", "createParty", "disbandParty");
        MB.registerPubSubChannels("@" + MB.getServerId() + "/partyRequest", "@" + MB.getServerId() + "/partyReply");

        BM.loadBans();
        MM.loadMutes();
        LM.setupPingTask();

        BungeeGuardListener BGListener = new BungeeGuardListener(this);

        ProxyServer.getInstance().getPluginManager().registerListener(this, BGListener);

        ProxyServer.getInstance().getPluginManager().registerListener(this, new RedisBungeeListener());

        getProxy().registerChannel("UHCGames");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PubSubListener(this));

        fetchParties();
        Set<Class<? extends Command>> commandes = new HashSet<>();
        Collections.addAll(commandes, CommandKick.class, CommandLobby.class, CommandReloadConf.class,
                CommandSpychat.class, CommandSend.class, CommandBan.class, CommandUnban.class, CommandList.class,
                CommandCheck.class, CommandMute.class, CommandUnmute.class, CommandSilence.class, CommandSay.class,
                CommandMsg.class, CommandReply.class, CommandHelp.class, CommandBCast.class, CommandGtp.class,
                CommandIgnore.class, CommandBUp.class, CommandBLoad.class, CommandParty.class, CommandServer.class,
                CommandPoints.class, CommandWallet.class, CommandFind.class, CommandStaff.class);

        for (Class<? extends Command> commande : commandes) {
            try {
                Command cmd = commande.getDeclaredConstructor(getClass()).newInstance(this);
                ProxyServer.getInstance().getPluginManager().registerCommand(this, cmd);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                new ReloadConfHandler(plugin).handle();
            }
        }, 0, 3 * 60, TimeUnit.SECONDS);

        getProxy().getScheduler().schedule(this, new AnnouncementTask(), 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        //httpApi.onDisable();
        ProxyServer.getInstance().getScheduler().cancel(this);
    }

    public void addGtp(UUID uuid, String playerName) {
        gtp.put(uuid, playerName);

    }

    public boolean isSilenced(String servName) {
        return silencedServers.contains(servName);
    }

    public boolean isSpying(UUID uniqueId) {
        return spy.contains(uniqueId);
    }

    public void toggleSpy(UUID uniqueId) {
        if (spy.contains(uniqueId))
            spy.remove(uniqueId);
        else
            spy.add(uniqueId);
    }

    public boolean isReply(UUID sender, UUID receiverUUID) {
        return reply.containsKey(sender) && reply.get(sender).equals(receiverUUID);
    }

    public void setReply(UUID receiver, UUID sender) {
        reply.put(receiver, sender);
    }

    public List<UUID> getSpies() {
        return spy;
    }

    public void silence(String serverName) {
        silencedServers.add(serverName);
    }

    public void unsilence(String serverName) {
        silencedServers.remove(serverName);
    }

    public UUID getReply(UUID uniqueId) {
        return reply.containsKey(uniqueId) ? reply.get(uniqueId) : null;
    }

    public HashMap<UUID, String> getGTP() {
        return gtp;
    }

    public boolean isPremadeMessage(String slug) {
        return premadeMessages.containsKey(slug.toLowerCase());
    }

    public String getPremadeMessage(String slug) {
        return premadeMessages.get(slug.toLowerCase());
    }

    public long getUptime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public int getBroadcastDelay() {
        return broadcastDelay;
    }

    public void setBroadcastDelay(int broadcastDelay) {
        this.broadcastDelay = broadcastDelay;
    }

    public void resetPrettyServerNames() {
        prettyServerNames.clear();
    }

    public void addPrettyServerName(String name, String prettyName) {
        prettyServerNames.put(name, prettyName);
    }
}
