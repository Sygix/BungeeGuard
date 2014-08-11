package fr.greenns.BungeeGuard.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandUnban extends Command {

	public BungeeGuard plugin;

	public CommandUnban(BungeeGuard plugin) {
		super("unban", "bungeeguard.unban");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		String unbanReason = "";
		String unbanName = "";

		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;

			if (!p.hasPermission("bungeeguard.unban")) {
				return;
			}
			unbanName = sender.getName().toString();
		} else {
			unbanName = "*Console*";
		}

		if (args.length == 0) {
			plugin.utils.msgPluginCommand(sender);
			return;
		}
		if (args.length >= 1) {

			if (args.length >= 1) {
				for (int a = 1; a < args.length; a++)
					unbanReason += " " + args[a];
			}

			plugin.sql.open();

			if (plugin.sql.getConnection() == null) {
				sender.sendMessage(ChatColor.RED
						+ "[MYSQL] Connection error ...");
				return;
			}
			String safenick = args[0].toLowerCase().replaceAll("'", "\"");

			try {
				ResultSet res = plugin.sql
						.query("SELECT id FROM `BungeeGuard_Ban` WHERE `nameBanned` = '"
								+ safenick + "' and `status` = 1");
				// res.last();

				boolean activeBan = false;

				// moved out of while block, why reset it each time, it's
				// already prepaired

				PreparedStatement pstmt = plugin.sql
						.prepare("UPDATE BungeeGuard_Ban SET status = 2, unbanreason=?, unbanname=? WHERE id=?");

				while (res.next()) {
					int id = res.getInt("id");
					pstmt.setString(1, unbanReason);
					pstmt.setString(2, unbanName);
					pstmt.setInt(3, id);

					pstmt.executeUpdate();

					activeBan = true;
				}
				pstmt.close();
				pstmt = null; // throw it away

				if (activeBan == true) {
					String msg = "";

					msg = "§a" + safenick + "§c a été débanni par §7"
							+ unbanName;

					System.out.println(msg);

					for (ProxiedPlayer playerdwa : BungeeCord.getInstance()
							.getPlayers()) {
						if (playerdwa.hasPermission("bungeeguard.notify")) {
							playerdwa.sendMessage(plugin.utils.staffBroadcast
									+ msg);
						}
					}
				} else {
					sender.sendMessage("§c" + safenick
							+ " joueur n'est pas banni ...");
				}

			} catch (final SQLException ex) {
				System.out.println("SQL problem (exception) : " + ex);
			} finally {
				try {
					if (!plugin.sql.getConnection().isClosed()) {
						plugin.sql.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex);
				}
			}
		}
	}

}
