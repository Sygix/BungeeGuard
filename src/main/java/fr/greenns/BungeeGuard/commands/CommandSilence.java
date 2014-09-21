package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
            boolean silenced = !plugin.isSilenced(servName);
            plugin.getMB().silenceServer(servName, silenced);
            for (ProxiedPlayer playerdwa : ProxyServer.getInstance().getPlayers()) {
                if (playerdwa.getServer().getInfo().getName().equalsIgnoreCase(servName) &&
                        playerdwa.hasPermission("bungeeguard.notify")) {
                    playerdwa.sendMessage(new ComponentBuilder(BungeeGuardUtils.getStaffBroadcastTag() + "Le chat du serveur ").color(ChatColor.GRAY)
                            .append(servName).color(ChatColor.AQUA).append(" a été ").color(ChatColor.GRAY)
                            .append(silenced ? "désactivé " : "activé").color(silenced ? ChatColor.RED : ChatColor.GREEN).append("!").color(ChatColor.GRAY).create());
                }
            }

        } else {
            BungeeGuardUtils.msgPluginCommand(sender);
        }
    }

}
