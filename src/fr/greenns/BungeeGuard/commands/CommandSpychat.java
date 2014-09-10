package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/07/14
 * Time: 18:46
 * May be open-source & be sold (by Greenns, of course !)
 */
public class CommandSpychat extends Command {

    public Main plugin;

    public CommandSpychat(Main plugin) {
        super("spychat", "bungeeguard.spychat", "sc");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette command !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (plugin.spy.contains(p.getUniqueId())) {
            plugin.spy.remove(p.getUniqueId());
            p.sendMessage(new ComponentBuilder("Vous avez désactivé ").color(ChatColor.GRAY).append("SpyChat").color(ChatColor.RED).create());
        } else {
            plugin.spy.add(p.getUniqueId());
            p.sendMessage(new ComponentBuilder("Vous avez activé ").color(ChatColor.GRAY).append("SpyChat").color(ChatColor.GREEN).create());
        }
    }
}