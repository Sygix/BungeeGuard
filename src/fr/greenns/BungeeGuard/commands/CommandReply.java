package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;

import java.util.UUID;

public class CommandReply extends Command {

	public BungeeGuard plugin;

	public CommandReply(BungeeGuard plugin) {
		super("r", "bungeeguard.reply");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;

			if (!p.hasPermission("bungeeguard.reply")) {
				return;
			}
		} else {
			sender.sendMessage(new ComponentBuilder("Vous devez être un joueur pour exécuter cette commande !").color(ChatColor.RED).create());
			return;
		}

		ProxiedPlayer p = (ProxiedPlayer) sender;

		Mute MuteUser = BungeeGuardUtils.getMute(p.getUniqueId());
		if (MuteUser != null) {
			if(MuteUser.isMute()) {
				MuteType MuteType = (MuteUser.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
				String MuteMsg = MuteType.playerFormat("", MuteUser.getReason());
				p.sendMessage(new ComponentBuilder(MuteMsg).create());		
			}
			else {
				MuteUser.removeFromBDD("TimeEnd", "Automatique");
			}
			return;
		}

		if (args.length == 0) {
			p.sendMessage(new ComponentBuilder("La bonne commande est :").color(ChatColor.RED).create());
			p.sendMessage(new ComponentBuilder("/r je te répond après").color(ChatColor.RED).create());
			return;
		}

		if (args.length >= 1) {
			if (plugin.reply.get(p.getName()) == null) {
				p.sendMessage(new ComponentBuilder("Vous n'avez personne à qui répondre !").color(ChatColor.RED).create());
				return;
			}

			for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
				if (plugin.reply.get(p.getName()) == pl) {
					ProxiedPlayer pe = plugin.reply.get(p.getName());
					String text1 = "";
					for (int i = 0; i < args.length; i++)
						text1 = text1 + args[i] + " ";

					String text = text1;
					pe.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append(p.getName()).color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());
					p.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(pe.getName()).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());

					plugin.reply.put(p.getName(), pe);
					plugin.reply.put(pe.getName(), p);
					for (UUID uuid : plugin.spy) {
						try {
							ProxiedPlayer admin = BungeeCord.getInstance().getPlayer(uuid);
							admin.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("SPY").color(ChatColor.RED).append("] ").color(ChatColor.GRAY).append(p.getName()).append(": /r ").append(pe.getName() + " " + text).create());
						} catch (Exception e) {

						}
					}

					return;
				}
			}
			p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
		}

	}
}
