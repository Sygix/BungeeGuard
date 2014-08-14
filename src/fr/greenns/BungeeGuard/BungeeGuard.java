package fr.greenns.BungeeGuard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Lobbies.LobbyUtils;
import fr.greenns.BungeeGuard.SQL.MySQL;
import fr.greenns.BungeeGuard.commands.CommandBan;
import fr.greenns.BungeeGuard.commands.CommandCheck;
import fr.greenns.BungeeGuard.commands.CommandKick;
import fr.greenns.BungeeGuard.commands.CommandList;
import fr.greenns.BungeeGuard.commands.CommandLobby;
import fr.greenns.BungeeGuard.commands.CommandMotd;
import fr.greenns.BungeeGuard.commands.CommandMsg;
import fr.greenns.BungeeGuard.commands.CommandMute;
import fr.greenns.BungeeGuard.commands.CommandReply;
import fr.greenns.BungeeGuard.commands.CommandSay;
import fr.greenns.BungeeGuard.commands.CommandSilence;
import fr.greenns.BungeeGuard.commands.CommandSpychat;
import fr.greenns.BungeeGuard.commands.CommandUnban;
import fr.greenns.BungeeGuard.commands.CommandUnmute;
import fr.greenns.BungeeGuard.utils.AuthPlayer;
import fr.greenns.BungeeGuard.utils.Ban;
import fr.greenns.BungeeGuard.utils.Mute;

public class BungeeGuard extends Plugin {

	public MySQL sql;
	public Configuration config;
	public BungeeGuardUtils utils;
	public LobbyUtils lobbyUtils;
	public Lobby lobby;
	public HashMap<String,ProxiedPlayer> reply;
	public ArrayList<UUID> spy;
	public ArrayList<String> serv;
	public ArrayList<Lobby> lobbyList;
	public HashMap<String, Boolean> serversUp = new HashMap<String, Boolean>();
	public BungeeGuardListener BGListener;
	public String motd;
	public Long time;
	public static BungeeGuard plugin;
	
	public static List<AuthPlayer> authplayers = new ArrayList<AuthPlayer>();
	public static List<Ban> bans = new ArrayList<Ban>();
	public static List<Mute> mutes = new ArrayList<Mute>();

	@Override
	public void onEnable() {
		plugin = this;
		
		sql = new MySQL(getLogger(), "", "vm-db-01.uhcwork.net", "3306", "plugin", "minecraft", "tn8E6VhU9P3m");
		//sql = new MySQL(getLogger(), "", "localhost", "3306", "testSQL", "testSQL", "X5SvEef9uDAHzV9P");
		sql.open();

		if (this.sql.checkConnection())
		{
			System.out.println("BungeeGuard - Connexion BDD réussite !");
		}
		else
		{
			System.out.println("BungeeGuard - Connexion BDD §cIMPOSSIBLE  §r!!!!!");
		}
		if(sql.checkTable("BungeeGuard_Ban"))
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Ban trouvée !");
			
			System.out.println("BungeeGuard - Chargement des bannis ...");
			try
			{
				if (BungeeGuard.plugin.sql.getConnection().isClosed())
				{
					BungeeGuard.plugin.sql.open();
				}

				if(BungeeGuard.plugin.sql.getConnection() == null)
				{
					System.out.println("[MYSQL] Connection error ...");
				}
				ResultSet res = sql.query("SELECT * FROM BungeeGuard_Ban WHERE status = 1");
				
				while(res.next()) {
					new Ban(UUID.fromString(res.getString("uuidBanned")), res.getString("nameBanned"), res.getLong("unban"), res.getString("reason"), res.getString("nameAdmin"), res.getString("uuidAdmin"));
				}
			}
			catch (final SQLException ex)
			{
				System.out.println("SQL problem (exception) when gettings banned players from BDD : " + ex );
			}
		}
		else
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Ban inÃ©xistante, creation en cours ...");

			sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Ban` (" +
					"  `id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `nameBanned` varchar(255) NOT NULL," +
					"  `nameAdmin` varchar(255) NOT NULL," +
					"  `uuidBanned` varchar(255) NOT NULL," +
					"  `uuidAdmin` varchar(255) NOT NULL," +
					"  `ban` bigint(20) NOT NULL," +
					"  `unban` bigint(20) NOT NULL," +
					"  `reason` text NOT NULL," +
					"  `unbanReason` text," +
					"  `unbanName` varchar(255) NOT NULL," +
					"  `status` int(11) NOT NULL," +
					"  PRIMARY KEY (`id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

			System.out.println("BungeeGuard - Table BungeeGuard_Ban créé !");
		}
		if(sql.checkTable("BungeeGuard_Mute"))
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Mute trouvée !");
			
			System.out.println("BungeeGuard - Chargement des mutes ...");
			try
			{
				if (BungeeGuard.plugin.sql.getConnection().isClosed())
				{
					BungeeGuard.plugin.sql.open();
				}

				if(BungeeGuard.plugin.sql.getConnection() == null)
				{
					System.out.println("[MYSQL] Connection error ...");
				}
				ResultSet res = sql.query("SELECT * FROM BungeeGuard_Mute WHERE status = 1");
				
				while(res.next()) {
					new Mute(UUID.fromString(res.getString("uuidMute")), res.getString("nameMute"), res.getLong("unmute"), res.getString("reason"), res.getString("nameAdmin"), res.getString("uuidAdmin"));
				}
			}
			catch (final SQLException ex)
			{
				System.out.println("SQL problem (exception) when getting mute players from BDD : " + ex );
			}
		}
		else
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Mute inéxistante, création en cours ...");

			sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Mute` (" +
					"  `id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `nameMute` varchar(255) NOT NULL," +
					"  `nameAdmin` varchar(255) NOT NULL," +
					"  `uuidMute` varchar(255) NOT NULL," +
					"  `uuidAdmin` varchar(255) NOT NULL," +
					"  `mute` bigint(20) NOT NULL," +
					"  `unmute` bigint(20) NOT NULL," +
					"  `reason` text NOT NULL," +
					"  `unmuteReason` text," +
					"  `unmuteName` varchar(255) NOT NULL," +
					"  `status` int(11) NOT NULL," +
					"  PRIMARY KEY (`id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

			System.out.println("BungeeGuard - Table BungeeGuard_Mute créé !");
		}
		if(sql.checkTable("BungeeGuard_AdminAuth"))
		{
			System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth trouvée !");
			
			System.out.println("BungeeGuard - Chargement des adminAuth ...");
			try
			{
				if (BungeeGuard.plugin.sql.getConnection().isClosed())
				{
					BungeeGuard.plugin.sql.open();
				}

				if(BungeeGuard.plugin.sql.getConnection() == null)
				{
					System.out.println("[MYSQL] Connection error ...");
				}
				ResultSet res = sql.query("SELECT * FROM BungeeGuard_AdminAuth");
				
				while(res.next()) {
					new AuthPlayer(UUID.fromString(res.getString("UUID")), res.getString("AuthKey"));
				}
			}
			catch (final SQLException ex)
			{
				System.out.println("SQL problem (exception) when getting adminAuth players from BDD : " + ex );
			}
		}
		else
		{
			System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth inéxistante, création en cours ...");

			sql.createTable("CREATE TABLE `BungeeGuard_AdminAuth` (" +
			  "`UUID` varchar(255) NOT NULL," +
			  "`Pseudo` varchar(45) NOT NULL," +
			  "`AuthKey` varchar(255) NOT NULL," +
			  "PRIMARY KEY (`UUID`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=latin1;");

			System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth créé !");
		}
		if(sql.checkTable("BungeeGuard_Motd"))
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Motd trouvÃ©e !");
		}
		else
		{
			System.out.println("BungeeGuard - Table BungeeGuard_Motd inÃ©xistante, creation en cours ...");
			sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Motd` (" +
					"  `id` int(11) NOT NULL," +
					"  `motd` varchar(255) NOT NULL," +
					"  PRIMARY KEY (`id`))");
			System.out.println("BungeeGuard - Table BungeeGuard_Motd créé !");
		}



		BGListener = new BungeeGuardListener(this);
		lobby = new Lobby(this);
		lobbyUtils = new LobbyUtils(this);
		reply = new HashMap<String,ProxiedPlayer>();
		spy = new ArrayList<UUID>();
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

		ArrayList<String> si = new ArrayList<>();

		for (final ServerInfo serverInfo : BungeeCord.getInstance().getServers().values())
		{
			if(serverInfo.getName().contains("lobby"))
			{
				si.add(serverInfo.getName());
			}
		}
		Collections.sort(si);

		for(String serv : si)
		{
			ServerInfo serverInfo = BungeeCord.getInstance().getServerInfo(serv);
			new Lobby(serv, 0, this);
			declarePing(serverInfo);
		}

		for (final Lobby l : lobbyList){
			BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
				
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
					public void done(ServerPing result, Throwable error)
					{
						serversUp.put(serverInfo1.getName(), error == null);
					}
				});
			}

		}, 5, 5, TimeUnit.SECONDS);
	}
	
	// TODO
	public void updateLobbysStatus() {
		for (final ServerInfo serverInfo : BungeeCord.getInstance().getServers().values())
		{
			if(serverInfo.getName().contains("lobby"))
			{
				// Vérification du status du serveur
				// Nombre de joueurs
				// On-Off
			}
		}
	}

	@Override
	public void onDisable()
	{
		sql.close();
		BungeeCord.getInstance().getScheduler().cancel(this);
	}
}
