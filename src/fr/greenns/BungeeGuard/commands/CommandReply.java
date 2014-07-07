package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

import java.util.UUID;

public class CommandReply extends Command {

	public BungeeGuard plugin;

	public CommandReply(BungeeGuard plugin)
	{
		super("r", "bungeeguard.reply");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.reply"))	
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
			sender.sendMessage("§c/r je te répond apres");
			return;
		}

		if(args.length >= 1 )
		{
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers())
			{
				if (plugin.reply.get(sender.getName()) == p)
				{
					ProxiedPlayer pe = plugin.reply.get(sender.getName());
					String text1 = "";
					for (int i = 0; i < args.length; i++)
						text1 = text1 + args[i] + " ";

					String text = text1;
					pe.sendMessage("§8[§a" + sender.getName() + " §7➠  §ame§8] §7" + text);
					sender.sendMessage("§8[§ame §7➠  §a" + pe + "§8] §7" + text);
					plugin.reply.put(sender.getName(), pe);
                    for(String sp : plugin.spy)
                    {
                        ProxiedPlayer admin = BungeeCord.getInstance().getPlayer(UUID.fromString(sp));
                        admin.sendMessage(ChatColor.GRAY + sender.getName() + ": /r " + pe.getName() + " " + text);
                    }

					return;
				}
			}
			sender.sendMessage("§cLe joueur que vous chercher a contacter n'est pas en ligne !");
		}

	}
}
