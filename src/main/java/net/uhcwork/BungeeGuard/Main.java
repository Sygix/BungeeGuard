package net.uhcwork.BungeeGuard;

import com.google.gson.Gson;
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
import net.uhcwork.BungeeGuard.Ignore.IgnoreManager;
import net.uhcwork.BungeeGuard.Kick.CommandKick;
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
import net.uhcwork.BungeeGuard.Party.PartyManager;
import net.uhcwork.BungeeGuard.commands.*;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    public static Main plugin;
    private static Connection db_co;
    private static Map<String, String> premadeMessages = new HashMap<>();
    private static List<String> forbiddenCommands = new ArrayList<>();
    public Gson gson = new Gson();
    private String motd;
    private long startTime;
    private Map<UUID, UUID> reply = new HashMap<>();
    private List<UUID> spy = new ArrayList<>();
    private List<String> silencedServers = new ArrayList<>();
    private HashMap<UUID, String> gtp = new HashMap<>();
    private MultiBungee MB = new MultiBungee();
    private PartyManager PM = new PartyManager();
    private BanManager BM = new BanManager(this);
    private MuteManager MM = new MuteManager();
    private LobbyManager LM = new LobbyManager(this);
    private IgnoreManager IM = new IgnoreManager(this);
    private AntiSpamListener AS = new AntiSpamListener();
    private AnnouncementManager AM = new AnnouncementManager(this);
    private int broadcastDelay = 180;

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

    public void setPremadeMessages(List<BungeePremadeMessage> all) {
        premadeMessages.clear();
        for (BungeePremadeMessage message : all) {
            premadeMessages.put(message.getSlug(), message.getText());
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

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public MultiBungee getMB() {
        return MB;
    }

    @Override
    public void onLoad() {
        plugin = this;
        startTime = System.currentTimeMillis();
        new BungeeGuardUtils(this);
        System.out.println("Welcome to MultiBungee ~ With ORM");
        getDb();
        ProxyServer.getInstance().setConfigurationAdapter(new MysqlConfigAdapter(this));
    }

    private void fetchParties() {
        MultiBungee MB = BungeeGuardUtils.getMB();
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
                CommandIgnore.class, CommandBUp.class, CommandBLoad.class, CommandParty.class, CommandServer.class);

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
        ProxyServer.getInstance().getScheduler().cancel(this);
    }

    public PartyManager getPM() {
        return PM;
    }

    public BanManager getBM() {
        return BM;
    }

    public MuteManager getMM() {
        return MM;
    }

    public IgnoreManager getIM() {
        return IM;
    }

    public LobbyManager getLM() {
        return LM;
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
        return premadeMessages.containsKey(slug);
    }

    public String getPremadeMessage(String slug) {
        return premadeMessages.get(slug);
    }

    public AntiSpamListener getAS() {
        return AS;
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

    public AnnouncementManager getAM() {
        return AM;
    }
}
