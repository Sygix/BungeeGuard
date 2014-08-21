package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;

public class CommandGtp extends Command {

	public BungeeGuard plugin;

	public CommandGtp(BungeeGuard plugin) {
		super("gtp", "bungeeguard.gtp");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			return;
		}
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		if (args.length != 1) {
			plugin.utils.msgPluginCommand(sender);
			return;
		}
		
		ProxiedPlayer teleportToPlayer = BungeeCord.getInstance().getPlayer(args[0]); 
		if(teleportToPlayer == null) {
			sender.sendMessage(ComponentManager.generate(ChatColor.RED + "Erreur: Ce joueur n'est pas en ligne"));
		} else {
			sender.sendMessage(ComponentManager.generate(ChatColor.GREEN + "Téléportation vers " + ChatColor.BLUE + teleportToPlayer.getName() + ChatColor.GREEN + " dans le monde " + ChatColor.GOLD + teleportToPlayer.getServer().getInfo().getName() + ChatColor.GREEN + "..."));
			plugin.gtp.put(p.getUniqueId(), teleportToPlayer.getUniqueId());
			p.connect(teleportToPlayer.getServer().getInfo());
		}
	}
}