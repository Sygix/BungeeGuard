package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.ServerManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;

public class CommandList extends Command {

	final MultiBungee MB;
	final ServerManager SM;
	final String[] LS_EASTEREGG = {"total 52K",
			"drwxr-xr-x  6 minecraft minecraft 4.0K Dec 24 13:58 .",
			"drwxr-xr-x  4 root      root      4.0K Dec 24 12:43 ..",
			"-rw-------  1 minecraft minecraft 7.8K Dec 25 14:22 .bash_history",
			"-rwxr-xr-x  1 minecraft minecraft  713 Dec 24 12:43 .bashrc",
			"drwx------  3 minecraft minecraft 4.0K Dec 24 13:48 .config",
			"-rw-------  1 minecraft minecraft   19 Dec 25 02:09 .nano_history",
			"-rwxr-xr-x  1 minecraft minecraft  131 Dec 24 12:43 .profile",
			"drwxr-xr-x  2 minecraft minecraft 4.0K Dec 24 12:43 .ssh"};
	private final Main plugin;

	public CommandList(Main plugin) {
		super("list", "", "who", "ls", "playerlist", "online", "plist");
		this.plugin = plugin;
		MB = Main.getMB();
		SM = Main.getServerManager();
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		int count;
		if (args.length == 0 || !sender.hasPermission("bungee.list.server")) {
			count = Main.getMB().getPlayerCount();
			sender.sendMessage(new ComponentBuilder("Il y a actuellement ").color(ChatColor.AQUA)
					.append("" + count).color(ChatColor.RED)
					.append(" joueur" + s(count) + " en ligne sur le serveur !").color(ChatColor.AQUA)
					.create());
		} else if (args.length == 1) {
			String server = args[0];
			if (server.equalsIgnoreCase("-lah")) {
				for (String msg : LS_EASTEREGG) {
					sender.sendMessage(TextComponent.fromLegacyText(msg));
				}
				return;
			}
			Collection<String> serverNames = SM.matchServer(server);
			if (serverNames.isEmpty()) {
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serveur inconnu"));
				return;
			}
			for (String serverName : serverNames) {
				count = MB.getPlayersOnServer(serverName).size();
				sender.sendMessage(new ComponentBuilder("Il y a actuellement ").color(ChatColor.AQUA)
						.append("" + count).color(ChatColor.RED)
						.append(" joueur" + s(count) + " en ligne sur le serveur ").color(ChatColor.AQUA)
						.append(serverName).bold(true)
						.create());
			}
		}
	}

	private String s(int count) {
		return count > 1 ? "s" : "";
	}
}
