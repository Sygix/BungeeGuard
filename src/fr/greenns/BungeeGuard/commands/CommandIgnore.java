package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Greenns on 07/07/14.
 */
public class CommandIgnore extends Command {

    public BungeeGuard plugin;

    public CommandIgnore(BungeeGuard plugin) {
        super("ignore", "bungeeguard.ignore");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette commande !").color(ChatColor.RED).create());
            return;
        } else {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (args.length == 1) {
                ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
                if (player == null) {
                    p.sendMessage(ComponentManager.generate(ChatColor.RED + "Ce joueur n'est pas en ligne."));
                } else {
                    if (player.hasPermission("bungeeguard.ignore.ignore")) {
                        p.sendMessage(ComponentManager.generate(ChatColor.RED + "Vous n'avez pas la permission d'ignorer ce joueur."));
                    } else {
                        if (plugin.ignore.containsKey(p.getUniqueId())) {
                            if (plugin.ignore.get(p.getUniqueId()).contains(player.getUniqueId())) {
                                plugin.ignore.get(p.getUniqueId()).remove(player.getUniqueId());
                                p.sendMessage(ComponentManager.generate(ChatColor.GRAY + "Vous n'ignorez plus " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
                            } else {
                                plugin.ignore.get(p.getUniqueId()).add(player.getUniqueId());
                                p.sendMessage(ComponentManager.generate(ChatColor.GRAY + "Vous ignorez maintenant " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
                            }
                        } else {
                            List<UUID> list = new ArrayList<>();
                            list.add(player.getUniqueId());
                            plugin.ignore.put(p.getUniqueId(), list);
                            p.sendMessage(ComponentManager.generate(ChatColor.GRAY + "Vous ignorez maintenant " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
                        }
                    }
                }
            } else {
                p.sendMessage(ComponentManager.generate(ChatColor.RED + "Utilisation: /ignore <joueur>"));
            }
        }

    }
}