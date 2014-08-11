package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandKick extends Command {

	public BungeeGuard plugin;

	public CommandKick(BungeeGuard plugin) {
		super("kick", "bungeeguard.kick");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String nick;

		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;

			if (!p.hasPermission("bungeeguard.kick")) {
				return;
			}
			nick = p.getDisplayName();
		} else {
			nick = "*Console*";
		}

		if (args.length == 0) {
			plugin.utils.msgPluginCommand(sender);
			return;
		}

		if (BungeeCord.getInstance().getPlayer(args[0]) != null) {
			ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
			String msg = "";
			String kickmsg = "";
			String powodd = "Aucune raison définie ...";

			if (args.length > 1) {
				powodd = "";
				for (int a = 1; a < args.length; a++)
					powodd += " " + args[a];
			}

			kickmsg = "§cVous avez été kické pour :  \n" + powodd;
			msg = nick + " a kické " + target.getDisplayName() + " pour : "
					+ powodd;

			System.out.println("§c" + msg);

			for (ProxiedPlayer playerdwa : target.getServer().getInfo()
					.getPlayers()) {
				if (playerdwa.hasPermission("bungeeguard.notify")) {
					playerdwa.sendMessage(plugin.utils.staffBroadcast
							+ ChatColor.RED + msg);
				}
			}
			sender.sendMessage("§a" + target.getName()
					+ " §2a été kické !");
			target.disconnect(kickmsg);
		} else if (BungeeCord.getInstance().getPlayer(args[0]) == null) {
			sender.sendMessage("§c§o" + args[0]
					+ "§r§c n'est pas connécté !");
		}
	}

}
