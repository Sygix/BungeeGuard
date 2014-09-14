package fr.greenns.BungeeGuard.commands;

import com.google.common.collect.ImmutableSet;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mguerreiro on 07/09/2014.
 */

public class CommandSend extends Command implements TabExecutor {

    private final Main plugin;

    public CommandSend(Main plugin) {
        super("send", "bungeecord.command.send");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments, usage: /send <player|all ou *|current ou @> <target>");
            return;
        }
        ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1]);
        if (target == null) {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("no_server"));
            return;
        }

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")) {
            summon("all", target.getName(), sender.getName());
        } else if (args[0].equalsIgnoreCase("current") || args[0].equalsIgnoreCase("@")) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(ChatColor.RED + "Only in game players can use this command");
                return;
            }
            summon(((ProxiedPlayer) sender).getServer().getInfo().getName(), target.getName(), sender.getName());
        } else {
            UUID u = BungeeGuardUtils.getMB().getUuidFromName(args[0]);
            if (u == null || !BungeeGuardUtils.getMB().isPlayerOnline(u)) {
                sender.sendMessage(ChatColor.RED + "That player is not online");
                return;
            }
            summon(args[0], target.getName(), sender.getName());
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully summoned player(s)");
    }

    private void summon(String player, String target, String sender) {
        BungeeGuardUtils.getMB().summon(player, target, sender);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getName().toLowerCase().startsWith(search)) {
                    matches.add(player.getName());
                }
            }
            if ("all".startsWith(search)) {
                matches.add("all");
            }
            if ("current".startsWith(search)) {
                matches.add("current");
            }
        } else {
            String search = args[1].toLowerCase();
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                if (server.toLowerCase().startsWith(search)) {
                    matches.add(server);
                }
            }
        }
        return matches;
    }
}