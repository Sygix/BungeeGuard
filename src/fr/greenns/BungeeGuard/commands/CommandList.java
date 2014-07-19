package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Greenns on 15/07/14.
 */
public class CommandList extends Command {

    public BungeeGuard plugin;

    public CommandList(BungeeGuard plugin)
    {
        super("list", "", "who", "ls", "playerlist", "online", "plist");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage(ChatColor.AQUA + "Il y a actuellement "+ChatColor.RED+BungeeCord.getInstance().getPlayers().size()+ChatColor.AQUA+" joueurs en ligne sur le serveur !");
        }
    }
}
