package net.uhcwork.BungeeGuard.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 15/07/14
 * Time: 18:46
 * May be open-source & be sold (by Greenns, of course !)
 */
public class CommandList extends Command {

    public Main plugin;

    public CommandList(Main plugin) {
        super("list", "", "who", "ls", "playerlist", "online", "plist");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Il y a actuellement ").color(ChatColor.AQUA).append("" + BungeeGuardUtils.getMB().getPlayerCount()).color(ChatColor.RED).append(" joueurs en ligne sur le serveur !").color(ChatColor.AQUA).create());
        }
    }
}
