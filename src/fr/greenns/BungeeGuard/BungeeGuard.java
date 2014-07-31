package fr.greenns.BungeeGuard;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Lobbies.LobbyUtils;
import fr.greenns.BungeeGuard.SQL.MySQL;
import fr.greenns.BungeeGuard.commands.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.scheduler.BungeeScheduler;

import javax.security.auth.login.Configuration;

public class BungeeGuard extends Plugin {

    public MySQL sql;
    public Configuration config;
    public BungeeGuardUtils utils;
    public LobbyUtils lobbyUtils;
    public Lobby lobby;
    public HashMap<String,Long> mute;
    public HashMap<String,ProxiedPlayer> reply;
    public ArrayList<String> spy;
    public ArrayList<String> serv;
    public ArrayList<Lobby> lobbyList;
    public HashMap<String, Boolean> serversUp = new HashMap<String, Boolean>();
    public BungeeGuardListener BGListener;
    public String motd;
    public Long time;

    @Override
    public void onEnable() {

        sql = new MySQL(getLogger(), "", "vm-db-01.uhcwork.net", "3306", "plugin", "minecraft", "tn8E6VhU9P3m");
        //sql = new MySQL(getLogger(), "", "localhost", "3306", "testSQL", "testSQL", "X5SvEef9uDAHzV9P");
        sql.open();

        if (this.sql.checkConnection())
        {
            System.out.println("BungeeGuard - Connexion BDD reussite !");
        }
        else
        {
            System.out.println("BungeeGuard - Connexion BDD §cIMPOSSIBLE  §r!!!!!");
        }
        if(sql.checkTable("BungeeGuard_Ban"))
        {
            System.out.println("BungeeGuard - Table BungeeGuard_Ban trouvée !");
        }
        else
        {
            System.out.println("BungeeGuard - Table BungeeGuard_Ban inéxistante, creation en cours ...");

            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Ban` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `nameBanned` varchar(255) NOT NULL,\n" +
                    "  `nameAdmin` varchar(255) NOT NULL,\n" +
                    "  `uuidBanned` varchar(255) NOT NULL,\n" +
                    "  `uuidAdmin` varchar(255) NOT NULL,\n" +
                    "  `ip` varchar(255) NOT NULL,\n" +
                    "  `ban` int(11) NOT NULL,\n" +
                    "  `unban` int(11) NOT NULL,\n" +
                    "  `reason` text NOT NULL,\n" +
                    "  `unbanReason` text,\n" +
                    "  `unbanName` varchar(255) NOT NULL,\n" +
                    "  `status` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

            System.out.println("BungeeGuard - Table BungeeGuard_Ban crée !");
        }
        if(sql.checkTable("BungeeGuard_Motd"))
        {
            System.out.println("BungeeGuard - Table BungeeGuard_Motd trouvée !");
        }
        else
        {
            System.out.println("BungeeGuard - Table BungeeGuard_Motd inéxistante, creation en cours ...");
            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Motd` (" +
                    "  `id` int(11) NOT NULL," +
                    "  `motd` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`))");
            System.out.println("BungeeGuard - Table BungeeGuard_Motd crée !");
        }



        BGListener = new BungeeGuardListener(this);
        lobby = new Lobby(this);
        lobbyUtils = new LobbyUtils(this);
        mute = new HashMap<String,Long>();
        reply = new HashMap<String,ProxiedPlayer>();
        spy = new ArrayList<String>();
        lobbyList = new ArrayList<Lobby>();
        serv = new ArrayList<String>();
        utils = new BungeeGuardUtils(this);
        sql.close();
 
        ProxyServer.getInstance().getPluginManager().registerListener(this, BGListener);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandKick(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandLobby(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSpychat(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandBan(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandUnban(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandList(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandCheck(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMute(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandUnmute(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSilence(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMotd(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSay(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMsg(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandReply(this));


        utils.refreshMotd();

        /*time = 1405443600000L - System.currentTimeMillis();

        utils.refreshMotd();


        BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if(time == 0)
                {
                    BungeeCord.getInstance().getScheduler().shutdown();
                }
                time = time - 1000;
                utils.refreshMotd();
            }
        }, 1, 1, TimeUnit.SECONDS);*/

        for (final ServerInfo serverInfo : getProxy().getServers().values())
        {
        	if(serverInfo.getName().contains("lobby"))
        	{
        		new Lobby(serverInfo.getName(), serverInfo.getPlayers().size(), this);
                declarePing(serverInfo);
        	}
        }
        
        for (final Lobby l : lobbyList){
    	BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run()
                {
            		int newSlot = l.getServerInfo().getPlayers().size();
            		l.setSlot(newSlot);
                }
            }, 5, 5, TimeUnit.SECONDS);
    	}
    }


    public void declarePing(final ServerInfo serverInfo1)
    {
    	BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run()
            {
                serverInfo1.ping(new Callback<ServerPing>()
                		{
                    @Override
                    public void done(ServerPing result, Throwable error) {
                        serversUp.put(serverInfo1.getName(), error == null);
                    }
                });
            }

        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable()
    {
        sql.close();
    }
}
