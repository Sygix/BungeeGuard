package fr.greenns.BungeeGuard.commands;

import java.util.ArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandSilence extends Command {

	public BungeeGuard plugin;

	public CommandSilence(BungeeGuard plugin)
	{
		super("silence", "bungeeguard.silence");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.silence"))
			{
				return;
			}
		}
		else
		{
			sender.sendMessage("§cCette command fonctionne uniquement en mode joueur !");
			return;
		}


		ProxiedPlayer p = (ProxiedPlayer)sender;
		
		if(args.length == 0)
		{
			if(!plugin.serv.contains(p.getServer().getInfo().getName()))
			{
				plugin.serv.add(p.getServer().getInfo().getName());
				String servName = p.getServer().getInfo().getName();
				sender.sendMessage("§7 le chat du serveur a été désactivé temporairement !");
				for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers())
				{
					if(playerdwa.hasPermission("bungeeguard.notify"))
					{
						playerdwa.sendMessage("§c le chat du serveur §e"+servName+"§c a été §cdésactivé §7!");
					}
				}
			}
			else
			{
				plugin.serv.remove(p.getServer().getInfo().getName());
				String servName = p.getServer().getInfo().getName();
				sender.sendMessage("§7 le chat du serveur a été réactivé pour tous !");
				
				for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers())
				{
					if(playerdwa.hasPermission("bungeeguard.notify"))
					{
						playerdwa.sendMessage(plugin.utils.staffBroadcast + "§c le chat du serveur §e"+servName+"§c a été §aréactiver §7!");
					}
				}
			}
		}
		else
		{
			plugin.utils.msgPluginCommand(sender);
		}
	}

}
