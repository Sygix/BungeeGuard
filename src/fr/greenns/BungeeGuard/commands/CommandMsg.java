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

public class CommandMsg extends Command {

	public BungeeGuard plugin;

	public CommandMsg(BungeeGuard plugin) {
		super("msg", "bungeeguard.msg", "m", "w", "tell", "whisper");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;

			if (!p.hasPermission("bungeeguard.msg")) {
				return;
			}
		} else {
			sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette command !").color(ChatColor.RED).create());
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
			p.sendMessage(new ComponentBuilder("Exemple :").color(ChatColor.RED).create());
			p.sendMessage(new ComponentBuilder("/msg NomDeMonAmi Hey sa te dit de jouer avec moi ?").color(ChatColor.RED).create());
			return;
		}

		if (args.length >= 1) {
			if (!args[0].equalsIgnoreCase(p.getName())) {
				for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
					if (args[0].equalsIgnoreCase(pl.getName())) {
						String text1 = "";
						for (int i = 1; i < args.length; i++)
							text1 = text1 + args[i] + " ";

						String text = text1;
						pl.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append(p.getName()).color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());
						p.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(pl.getName()).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());

						
						plugin.reply.put(p.getName(), pl);
						plugin.reply.put(pl.getName(), p);
						for (UUID uuid : plugin.spy) {
							try {
								ProxiedPlayer admin = BungeeCord.getInstance().getPlayer(uuid);
								admin.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("SPY").color(ChatColor.RED).append("]").color(ChatColor.GRAY).append(p.getName()).append(": /msg ").append(pl.getName() + " " + text).create());
							} catch (Exception e) {

							}
						}

						return;
					}
				}
				p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
			} else {
				p.sendMessage(new ComponentBuilder("Vous ne pouvez pas envoyer un message à vous-même !").color(ChatColor.RED).create());
			}
		}
	}
}
