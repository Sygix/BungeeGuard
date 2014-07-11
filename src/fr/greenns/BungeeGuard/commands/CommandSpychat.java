package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Greenns on 07/07/14.
 */
public class CommandSpychat extends Command {

    public BungeeGuard plugin;

    public CommandSpychat(BungeeGuard plugin)
    {
        super("spychat", "bungeeguard.spychat", "sc");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(ChatColor.RED + "Vous devez etre un joueur pour executer cette command !");
            return;
        }
        else
        {
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(plugin.spy.contains(p.getUniqueId().toString()))
            {
                plugin.spy.remove(p.getUniqueId().toString());
                p.sendMessage(ChatColor.GRAY + "Vous avez désactivé "+ChatColor.RED+"SpyChat");
            }
            else
            {
                plugin.spy.add(p.getUniqueId().toString());
                p.sendMessage(ChatColor.GRAY + "Vous avez activé "+ChatColor.GREEN+"SpyChat");
            }
        }

    }
}