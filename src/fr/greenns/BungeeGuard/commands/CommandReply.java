package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.Mute;
import fr.greenns.BungeeGuard.utils.MuteType;

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
			sender.sendMessage(ChatColor.RED
					+ "Vous devez etre un joueur pour executer cette command !");
			return;
		}

		ProxiedPlayer p = (ProxiedPlayer) sender;

		Mute MuteUser = BungeeGuardUtils.getMute(p.getUniqueId());
		if (MuteUser != null) {
			if(MuteUser.isMute()) {
				MuteType MuteType = (MuteUser.getReason() != null) ? fr.greenns.BungeeGuard.utils.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.utils.MuteType.NON_PERMANENT;
				String MuteMsg = MuteType.playerFormat("", MuteUser.getReason());
				p.sendMessage(new ComponentBuilder(MuteMsg).create());		
			}
			else {
				MuteUser.removeFromBDD("TimeEnd", "Automatique");
			}
			return;
		}

		if (args.length == 0) {
			p.sendMessage("§cLa bonne commande est :");
			p.sendMessage("§c/r je te répond apres");
			return;
		}

		if (args.length >= 1) {
			if (plugin.reply.get(p.getName()) == null) {
				p.sendMessage(ChatColor.RED
						+ "Vous n'avez personne à qui répondre !");
				return;
			}

			for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
				if (plugin.reply.get(p.getName()) == pl) {
					ProxiedPlayer pe = plugin.reply.get(p.getName());
					String text1 = "";
					for (int i = 0; i < args.length; i++)
						text1 = text1 + args[i] + " ";

					String text = text1;
					pe.sendMessage("§8[§a" + p.getName()
							+ " §7➠  §aMoi§8] §f" + text);
					p.sendMessage("§8[§aMoi §7➠  §a" + pe + "§8] §f"
							+ text);
					plugin.reply.put(p.getName(), pe);
					plugin.reply.put(pe.getName(), p);
					for (UUID uuid : plugin.spy) {
						try {
							ProxiedPlayer admin = BungeeCord.getInstance()
									.getPlayer(uuid);
							admin.sendMessage("§7[§cSPY§7] "
									+ ChatColor.GRAY + p.getName() + ": /r "
									+ pe.getName() + " " + text);
						} catch (Exception e) {

						}
					}

					return;
				}
			}
			p.sendMessage("§cLe joueur que vous chercher a contacter n'est pas en ligne !");
		}

	}
}
