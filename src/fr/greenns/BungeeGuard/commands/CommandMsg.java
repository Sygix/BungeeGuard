package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandMsg extends Command {

	public BungeeGuard plugin;

	public CommandMsg(BungeeGuard plugin)
	{
		super("msg", "bungeeguard.msg");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.msg"))	
			{
				return;
			}
		}

		if(plugin.mute.containsKey(plugin.mute.containsKey(sender.getName())))
		{
			sender.sendMessage("§cVous êtes muté temporairement !");
			return;
		}

		if(args.length == 0)
		{
			sender.sendMessage("§cLa bonne commande est :");
			sender.sendMessage("§c/msg NomDeMonAmi Hey sa te dit de jouer avec moi ?");
			return;
		}

		if(args.length >= 1 )
		{
			if (!args[0].equalsIgnoreCase(sender.getName()))
			{
				for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers())
				{
					if (args[0].equalsIgnoreCase(p.getName())) 
					{
						String text1 = "";
						for (int i = 1; i < args.length; i++)
							text1 = text1 + args[i] + " ";

						String text = text1;
						p.sendMessage("§8[§a" + sender.getName() + " §7➠  §ame§8] §7" + text);
						sender.sendMessage("§8[§ame §7➠  §a" + p.getName() + "§8] §7" + text);
						plugin.reply.put(sender.getName(), p);

						return;
					}
				}
				sender.sendMessage("§cLe joueur que vous chercher a contacter n'est pas en ligne !");
			}
			else
			{
				sender.sendMessage("§cVous ne pouvez pas envoyer un message à vous-même !");
			}
		}
	}
}
