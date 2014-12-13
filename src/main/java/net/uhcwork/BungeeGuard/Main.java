package net.uhcwork.BungeeGuard;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.io.Files;
import com.google.gson.Gson;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.uhcwork.BungeeGuard.Announces.AnnouncementManager;
import net.uhcwork.BungeeGuard.Announces.AnnouncementTask;
import net.uhcwork.BungeeGuard.BanHammer.AntiSpamListener;
import net.uhcwork.BungeeGuard.Commands.*;
import net.uhcwork.BungeeGuard.Config.MysqlConfigAdapter;
import net.uhcwork.BungeeGuard.Managers.*;
import net.uhcwork.BungeeGuard.Models.BungeeBlockedCommands;
import net.uhcwork.BungeeGuard.Models.BungeePremadeMessage;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.ReloadConfHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubListener;
import net.uhcwork.BungeeGuard.MultiBungee.RedisBungeeListener;
import net.uhcwork.BungeeGuard.Utils.MyReconnectHandler;
import net.uhcwork.BungeeGuard.Utils.ShopTask;
import net.uhcwork.BungeeGuard.Utils.SlackUtils;
import org.javalite.activejdbc.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {
    public static final String ADMIN_TAG = ChatColor.RED + "[BungeeGuard] " + ChatColor.RESET;
    @Getter
    static final SlackUtils slack = new SlackUtils();
    private static final Map<String, String> premadeMessages = new HashMap<>();
    private static final List<String> forbiddenCommands = new ArrayList<>();
    @Getter
    private static final MultiBungee MB = new MultiBungee();
    private static final Map<UUID, UUID> reply = new HashMap<>();
    private static final List<UUID> spy = new ArrayList<>();
    private static final String REDUCTION_PEINE = " &r(&eRéduction de peine&r)";
    @Getter
    private static final Random random = new Random();
    public static Main plugin;
    @Getter
    public static Gson gson = new Gson();
    @Getter
    static ServerManager serverManager;
    private final List<String> silencedServers = new ArrayList<>();
    @Getter
    private final SanctionManager sanctionManager = new SanctionManager(this);
    @Getter
    PermissionManager permissionManager = new PermissionManager(this);
    @Getter
    MysqlConfigAdapter config;
    private long startTime;
    @Getter
    private PartyManager partyManager = new PartyManager();
    @Getter
    private IgnoreManager ignoreManager = new IgnoreManager(this);
    @Getter
    private AntiSpamListener antiSpamListener = new AntiSpamListener();
    @Getter
    private AnnouncementManager announcementManager = new AnnouncementManager(this);
    private int broadcastDelay = 180;
    @Getter
    private WalletManager walletManager = new WalletManager(this);
    private String MYSQL_USER, MYSQL_HOST, MYSQL_DATABASE, MYSQL_PASS;

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

    public <T> Future<T> executePersistenceRunnable(final Callable<T> callable) {
        FutureTask<T> F = new FutureTask<>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T value = null;
                try {
                    System.out.println("[ORM] Creation de la connexion SQL pour " + Thread.currentThread().toString() + " ... :)");
                    setup();
                    value = callable.call();
                } finally {
                    System.out.println("[ORM] Fermeture pour " + Thread.currentThread().toString() + " ... :D");
                    cleanup();
                }
                return value;
            }

            private void setup() {
                if (Base.hasConnection())
                    return;
                Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://" + MYSQL_HOST + "/" + MYSQL_DATABASE, MYSQL_USER, MYSQL_PASS);
            }

            private void cleanup() {
                Base.close();
            }
        });
        getProxy().getScheduler().runAsync(this, F);
        return F;
    }


    private String getEnv(String name, String def, Properties prop) {
        String property = (prop == null) ? def : prop.getProperty(name, def);
        return MoreObjects.firstNonNull(System.getenv(name), property);
    }


    @Override
    public void onLoad() {
        plugin = this;
        startTime = System.currentTimeMillis();
        serverManager = new ServerManager(this);
        Properties prop = null;
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                prop = new Properties();
                BufferedReader reader = Files.newReader(configFile, Charsets.UTF_8);
                prop.load(reader);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MYSQL_HOST = getEnv("MYSQL_HOST", "localhost", prop);
        MYSQL_DATABASE = getEnv("MYSQL_DATABASE", "plugin", prop);
        MYSQL_USER = getEnv("MYSQL_USER", "root", prop);
        MYSQL_PASS = getEnv("MYSQL_PASS", "", prop);


        if (MYSQL_HOST.isEmpty() || MYSQL_DATABASE.isEmpty() || MYSQL_USER.isEmpty() || MYSQL_PASS.isEmpty()) {
            ProxyServer.getInstance().stop();
            throw new RuntimeException("La configuration est mauvaise, chef.");
        }

        new BungeeGuardUtils(this);
        System.out.println("Welcome to MultiBungee ~ With ORM ~ Crafted with love, and a small cache.");
        getProxy().setReconnectHandler(new MyReconnectHandler());
        config = new MysqlConfigAdapter(this);
        getProxy().setConfigurationAdapter(config);
    }

    private void fetchParties() {
        List<String> server = MB.getAllServers();
        partyManager = new PartyManager();
        for (String s : server) {
            if (!s.equals(MB.getServerId())) {
                System.out.println("RequestParties: " + s);
                MB.requestParties(s);

                System.out.println("RequestIgnores: " + s);
                MB.requestIgnores(s);
                return;
            }
        }

    }

    @Override
    public void onEnable() {
        MB.init();

        sanctionManager.loadBans();
        sanctionManager.loadMutes();
        serverManager.setupPingTask();

        BungeeGuardListener BGListener = new BungeeGuardListener(this);

        getProxy().getPluginManager().registerListener(this, BGListener);

        getProxy().getPluginManager().registerListener(this, new RedisBungeeListener(this));

        getProxy().registerChannel("UHCGames");
        getProxy().getPluginManager().registerListener(this, new PubSubListener(this));

        fetchParties();
        Set<Class<? extends Command>> commandes = new HashSet<>();
        Collections.addAll(commandes,
                CommandKick.class, CommandLobby.class, CommandReloadConf.class, CommandBStats.class,
                CommandSpychat.class, CommandSend.class, CommandBan.class, CommandUnban.class, CommandList.class,
                CommandCheck.class, CommandMute.class, CommandUnmute.class, CommandSilence.class, CommandSay.class,
                CommandMsg.class, CommandReply.class, CommandHelp.class, CommandBCast.class, CommandGtp.class,
                CommandIgnore.class, CommandBPl.class, CommandBLoad.class, CommandParty.class, CommandServer.class,
                CommandPoints.class, CommandWallet.class, CommandFind.class, CommandStaff.class, CommandSeen.class,
                CommandMaintenance.class,
                CommandUser.class, CommandGroups.class, CommandGtpHere.class, CommandRegister.class);

        for (Class<? extends Command> commande : commandes) {
            try {
                Command cmd = commande.getDeclaredConstructor(getClass()).newInstance(this);
                getProxy().getPluginManager().registerCommand(this, cmd);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        plugin.getPermissionManager().loadUsers();

        final ShopTask shopTask = new ShopTask(this);
        final ReloadConfHandler reloadConfHandler = new ReloadConfHandler();
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                reloadConfHandler.handle(plugin);
                shopTask.run();
            }
        }, 0, 10, TimeUnit.SECONDS);

        setupStopSchedule();

        getProxy().getScheduler().schedule(this, new AnnouncementTask(), 1, 1, TimeUnit.SECONDS);
    }

    private void setupStopSchedule() {
        // Auto reboot après 6 heures, dès qu'il y a peu de joueurs
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (getProxy().getOnlineCount() <= 4) {
                    getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Redémarrage du serveur dans 3 secondes ..."));
                    getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            getProxy().stop();
                        }
                    }, 3, TimeUnit.SECONDS);
                }
            }
        }, 6 * 60 * 60, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
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

    public boolean isPremadeMessage(String slug) {
        String lowerSlug = slug.toLowerCase();
        if (lowerSlug.startsWith("r:"))
            lowerSlug = lowerSlug.substring(2);
        return premadeMessages.containsKey(lowerSlug);
    }

    public String getPremadeMessage(String slug) {
        String lowerSlug = slug.toLowerCase();
        boolean reduction = lowerSlug.startsWith("r:");
        if (reduction)
            lowerSlug = lowerSlug.substring(2);

        return premadeMessages.get(lowerSlug) + (reduction ? REDUCTION_PEINE : "");
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

    public void executeRunnable(Runnable runnable) {
        getProxy().getScheduler().runAsync(this, runnable);
    }
}
