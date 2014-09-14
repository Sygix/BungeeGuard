package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandSilence extends Command {

    public Main plugin;

    public CommandSilence(Main plugin) {
        super("silence", "bungeeguard.silence");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Cette commande fonctionne uniquement en mode joueur !").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length == 0) {
            String servName = p.getServer().getInfo().getName();
            if (!plugin.silencedServers.contains(servName)) {
                plugin.getMB().silenceServer(servName, true);
                for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers()) {
                    if (playerdwa.getServer().getInfo().getName().equalsIgnoreCase(servName) &&
                            playerdwa.hasPermission("bungeeguard.notify")) {
                        playerdwa.sendMessage(new ComponentBuilder(plugin.utils.staffBroadcast + "Le chat du serveur ").color(ChatColor.GRAY)
                                .append(servName).color(ChatColor.AQUA).append(" a été ").color(ChatColor.GRAY)
                                .append("désactivé ").color(ChatColor.RED).append("!").color(ChatColor.GRAY).create());
                    }
                }
            } else {
                plugin.getMB().silenceServer(servName, false);
                for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers()) {
                    if (playerdwa.getServer().getInfo().getName().equalsIgnoreCase(servName)
                            && playerdwa.hasPermission("bungeeguard.notify")) {
                        playerdwa.sendMessage(new ComponentBuilder(plugin.utils.staffBroadcast + "Le chat du serveur ").color(ChatColor.GRAY)
                                .append(servName).color(ChatColor.AQUA).append(" a été ").color(ChatColor.GRAY)
                                .append("réactivé ").color(ChatColor.GREEN).append("!").color(ChatColor.GRAY).create());
                    }
                }
            }
        } else {
            plugin.utils.msgPluginCommand(sender);
        }
    }

}
