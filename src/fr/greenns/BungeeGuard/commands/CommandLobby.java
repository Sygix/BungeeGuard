package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Greenns on 07/07/14.
 */
public class CommandLobby extends Command {

    public BungeeGuard plugin;

    public CommandLobby(BungeeGuard plugin) {
        super("lobby", "", "leave", "hub", "quit", "spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette command !").color(ChatColor.RED).create());
        } else {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (p.getServer().getInfo().getName().equalsIgnoreCase("lobby")) {
                p.sendMessage(new ComponentBuilder("Vous etes déjà connecté a ce serveur !").color(ChatColor.RED).create());
            } else {
                p.connect(BungeeCord.getInstance().getServerInfo("hub"));
                p.sendMessage(new ComponentBuilder("Connexion vers le lobby . . .").color(ChatColor.GREEN).create());
            }
        }

    }
}