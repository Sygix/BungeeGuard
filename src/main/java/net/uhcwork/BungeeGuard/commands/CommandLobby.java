package net.uhcwork.BungeeGuard.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/07/14
 * Time: 18:45
 * May be open-source & be sold (by Greenns, of course !)
 */
public class CommandLobby extends Command {

    public Main plugin;

    public CommandLobby(Main plugin) {
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
                p.connect(ProxyServer.getInstance().getServerInfo("hub"));
                p.sendMessage(new ComponentBuilder("Connexion vers le lobby . . .").color(ChatColor.GREEN).create());
            }
        }

    }
}