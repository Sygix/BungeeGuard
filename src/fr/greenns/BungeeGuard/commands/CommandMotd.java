package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Greenns on 11/07/14.
 */
public class CommandMotd extends Command {

    public BungeeGuard plugin;

    public CommandMotd(BungeeGuard plugin)
    {
        super("motd", "bungeeguard.motd");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            plugin.utils.refreshMotd();
            sender.sendMessage("§aMotd updated !");
            return;
        }
    }
}