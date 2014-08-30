package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Greenns on 15/07/14.
 */
public class CommandList extends Command {

    public BungeeGuard plugin;

    public CommandList(BungeeGuard plugin) {
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
