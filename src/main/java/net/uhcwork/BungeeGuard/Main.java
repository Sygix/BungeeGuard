package net.uhcwork.BungeeGuard;

import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
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
    private MultiBungee MB;
    private PartyManager PM;
    private BanManager BM;
    private MuteManager MM;
    private LobbyManager LM;
    private IgnoreManager IM;
    private AntiSpamListener AS;

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

    public static void setPremadeMessages(List<BungeePremadeMessage> all) {
        premadeMessages.clear();
        for (BungeePremadeMessage message : all) {
            premadeMessages.put(message.getSlug(), message.getText());
        }
    }

    public List<String> getForbiddenCommands() {
        return forbiddenCommands;
    }

    public static void setForbiddenCommands(List<BungeeBlockedCommands> all) {
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

        MB = new MultiBungee();

        MB.registerPubSubChannels("ban", "unban");
        MB.registerPubSubChannels("kick", "silenceServer");
        MB.registerPubSubChannels("mute", "unmute");
        MB.registerPubSubChannels("staffChat", "notifyStaff");
        MB.registerPubSubChannels("message", "privateMessage", "ignore", "broadcast");
        MB.registerPubSubChannels("reloadConf", "summon");
        MB.registerPubSubChannels("setPartyPublique", "inviteParty", "addPartyMember", "playerLeaveParty", "setPartyChat",
                "setPartyOwner", "kickFromParty", "summonParty", "partyChat", "createParty", "disbandParty");
        MB.registerPubSubChannels("@" + MB.getServerId() + "/partyRequest", "@" + MB.getServerId() + "/partyReply");


        BM = new BanManager(this);
        BM.loadBans();

        MM = new MuteManager();
        MM.loadMutes();

        LM = new LobbyManager(this);
        LM.setupPingTask();

        IM = new IgnoreManager(this);

        AS = new AntiSpamListener();

        BungeeGuardListener BGListener = new BungeeGuardListener(this);

        ProxyServer.getInstance().getPluginManager().registerListener(this, BGListener);

        ProxyServer.getInstance().getPluginManager().registerListener(this, new RedisBungeeListener());

        getProxy().registerChannel("UHCGames");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PubSubListener(this));

        fetchParties();
        Set<Class<? extends Command>> commandes = new HashSet<>();
        commandes.add(CommandKick.class);
        commandes.add(CommandLobby.class);
        commandes.add(CommandReloadConf.class);
        commandes.add(CommandSpychat.class);
        commandes.add(CommandSend.class);
        commandes.add(CommandBan.class);
        commandes.add(CommandUnban.class);
        commandes.add(CommandList.class);
        commandes.add(CommandCheck.class);
        commandes.add(CommandMute.class);
        commandes.add(CommandUnmute.class);
        commandes.add(CommandSilence.class);
        commandes.add(CommandSay.class);
        commandes.add(CommandMsg.class);
        commandes.add(CommandReply.class);
        commandes.add(CommandHelp.class);
        commandes.add(CommandBCast.class);
        commandes.add(CommandGtp.class);
        commandes.add(CommandIgnore.class);
        commandes.add(CommandBUp.class);
        commandes.add(CommandBLoad.class);
        commandes.add(CommandParty.class);
        commandes.add(CommandServer.class);

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
        return System.currentTimeMillis() - startTime;
    }
}
