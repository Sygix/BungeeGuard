package fr.greenns.BungeeGuard;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import fr.greenns.BungeeGuard.SQL.MySQL;
import fr.greenns.BungeeGuard.commands.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.security.auth.login.Configuration;

public class BungeeGuard extends Plugin {

	public MySQL sql;
	public BungeeGuardUtils utils;
	public HashMap<String,Long> mute;
	public HashMap<String,ProxiedPlayer> reply;
	public ArrayList<String> spy;
	public ArrayList<String> serv = new ArrayList<String>();
	public BungeeGuardListener BGListener;
    public String motd;
	
    @Override
    public void onEnable() {
    	
    	sql = new MySQL(getLogger(), "", "localhost", "3306", "plugin", "root", "b1t3du68250_mysql");
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
			sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Ban` (`id` int(11) NOT NULL AUTO_INCREMENT,`nameBanned` varchar(255) NOT NULL,`nameAdmin` varchar(255) NOT NULL,`ip` varchar(255) NOT NULL,`ban` int(11) NOT NULL,`unban` int(11) NOT NULL,`reason` text NOT NULL,`unbanReason` text,`unbanName` varchar(255) NOT NULL,`status` int(11) NOT NULL,PRIMARY KEY (`id`))");
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
		mute = new HashMap<String,Long>();
		reply = new HashMap<String,ProxiedPlayer>();
        spy = new ArrayList<String>();
		utils = new BungeeGuardUtils(this);
		sql.close();

		ProxyServer.getInstance().getPluginManager().registerListener(this, BGListener);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandKick(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandLobby(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSpychat(this));
        //ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandBan(this));
        //ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandUnban(this));
        //ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandCheck(this));
        //ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMute(this));
        //ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandUnmute(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSilence(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSay(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMsg(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandReply(this));



    }
    
    

	@Override
	public void onDisable()
	{
		sql.close();
	}
}
