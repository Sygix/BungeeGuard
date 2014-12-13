package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.uhcwork.BungeeGuard.Main;

import java.util.Collections;
import java.util.Map;

/**
 * Part of net.uhcwork.BungeeGuard.commands (bungeeguard)
 * Date: 14/09/2014
 * Time: 18:57
 * May be open-source & be sold (by mguerreiro, of course !)
 */

/**
 * Command to list and switch a player between available servers.
 */
public class CommandServer extends Command implements TabExecutor {

    public CommandServer(Main plugin) {
        super("server", "bungeecord.command.server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        if (args.length == 0) {
            player.sendMessage(new TextComponent(ChatColor.GOLD + "Vous êtes actuellement sur " + player.getServer().getInfo().getName()));
        } else {
            ServerInfo server = servers.get(args[0]);
            if (server == null) {
                player.sendMessage(new TextComponent(ProxyServer.getInstance().getTranslation("no_server")));
            } else if (!server.canAccess(player)) {
                player.sendMessage(new TextComponent(ProxyServer.getInstance().getTranslation("no_server_permission")));
            } else {
                player.connect(server);
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, String[] args) {
        return (args.length != 0) ? Collections.<String>emptyList() : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getServers().values(), new Predicate<ServerInfo>() {
            @Override
            public boolean apply(ServerInfo input) {
                return input.canAccess(sender);
            }
        }), new Function<ServerInfo, String>() {
            @Override
            public String apply(ServerInfo input) {
                return input.getName();
            }
        });
    }
}