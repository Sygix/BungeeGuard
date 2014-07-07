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
public class CommandLobby extends Command {

    public BungeeGuard plugin;

    public CommandLobby(BungeeGuard plugin)
    {
        super("lobby", "", "leave", "hub", "quit", "spawn");
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
            if(p.getServer().getInfo().getName().equalsIgnoreCase("lobby"))
            {
                p.sendMessage(ChatColor.RED + "Vous etes déjà connecté a ce serveur !");
            }
            else
            {
                p.connect(BungeeCord.getInstance().getServerInfo("lobby"));
                p.sendMessage(ChatColor.GREEN + "Connexion vers le lobby . . .");
            }
        }

    }
}