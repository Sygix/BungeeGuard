package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;

public class CommandHelp extends Command {

	public BungeeGuard plugin;

	public CommandHelp(BungeeGuard plugin) {
		super("help", "bungeeguard.help");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
			
			sender.sendMessage(ComponentManager.generate(ChatColor.GOLD + "--------------[ " + ChatColor.BOLD + ChatColor.GREEN + "✔ COMMANDES ✔ " + ChatColor.GOLD + "]--------------"));
			//new ComponentBuilder("---------------[ ").color(ChatColor.GOLD).append("✔ COMMANDES ✔ ").color(ChatColor.GREEN).color(ChatColor.BOLD).append("]-----------------").color(ChatColor.GOLD).create());
			sender.sendMessage(new ComponentBuilder("                        Joueurs").color(ChatColor.AQUA).color(ChatColor.BOLD).create());
			sender.sendMessage(new ComponentBuilder("/uhcoins").color(ChatColor.GREEN).append(" Accède à ton compte UHCoins").color(ChatColor.GRAY).create());
			sender.sendMessage(new ComponentBuilder("/party").color(ChatColor.GREEN).append(" Crée des party avec tes amis pour pouvoir jouer avec eux").color(ChatColor.GRAY).create());
			sender.sendMessage(new ComponentBuilder("/vstart").color(ChatColor.GREEN).append(" Vote pour le démarrage d'une partie UltraHungerGames").color(ChatColor.GRAY).create());
			sender.sendMessage(new ComponentBuilder("                        VIP").color(ChatColor.AQUA).color(ChatColor.BOLD).create());
			sender.sendMessage(new ComponentBuilder("/fly").color(ChatColor.GREEN).append(" Active le fly").color(ChatColor.GRAY).create());
			sender.sendMessage(new ComponentBuilder("/resetstats").color(ChatColor.GREEN).append(" Remet à zero tes statistiques pour repartir sur de bonnes bases").color(ChatColor.GRAY).create());
			sender.sendMessage(new ComponentBuilder("                        YouTuber").color(ChatColor.AQUA).color(ChatColor.BOLD).create());
			sender.sendMessage(new ComponentBuilder("/fstart").color(ChatColor.GREEN).append(" Force le démarrage d'une partie").color(ChatColor.GRAY).create());
			sender.sendMessage(ComponentManager.generate(ChatColor.GOLD + "---------------------------------------------------"));
	}
}
