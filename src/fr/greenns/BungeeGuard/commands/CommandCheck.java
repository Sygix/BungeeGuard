package fr.greenns.BungeeGuard.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandCheck  extends Command {

	public BungeeGuard plugin;

	public CommandCheck(BungeeGuard plugin)
	{
		super("check", "bungeeguard.check");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.check"))	
			{
				return;
			}
		}

		if(args.length == 0)
		{
			plugin.utils.msgPluginCommand(sender);
			return;
		}

		if(args.length >= 1 )
		{
			ResultSet res;
			ResultSet activeBans;
			String message;

			plugin.sql.open();

			if(plugin.sql.getConnection() == null)
			{
				sender.sendMessage(ChatColor.RED+"[MYSQL] Connection error ...");
				return;
			}

			try {

				String safenick = args[0].toLowerCase().replaceAll("'", "\"");

				res = plugin.sql.query("SELECT COUNT(id) as count FROM `BungeeGuard_Ban` WHERE `nameBanned` = '"+safenick+"'");

				if (res.next())
				{
					message = "Le joueur "+safenick+" a "+ res.getInt("count") +" ban";
					activeBans = plugin.sql.query("SELECT COUNT(id) as count FROM `BungeeGuard_Ban` WHERE `nameBanned` = '"+safenick+"' AND `status` = 1");
					if ( activeBans.next())
					{
						message += " dont " + activeBans.getInt("count") +" ban encore actif !";
					}
					sender.sendMessage("§c"+message );
				}

			}
			catch (final SQLException ex)
			{
				System.out.println("SQL problem (exception) while checking player bans : " + ex );
			}
			finally
			{
				try {
					if (!plugin.sql.getConnection().isClosed())
					{
						plugin.sql.close();
					}
				} catch (SQLException ex)
				{
					System.out.println(ex);
				}
			}
		}


	}

}
