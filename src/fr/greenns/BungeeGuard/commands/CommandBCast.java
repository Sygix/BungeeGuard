package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandBCast extends Command {

    public BungeeGuard plugin;

    public CommandBCast(BungeeGuard plugin) {
        super("bcast", "bungeeguard.bcast");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            plugin.utils.msgPluginCommand(sender);
            return;
        }

        if (args.length > 0) {
            String msg = "";
            for (String m : args)
                msg += m + " ";

            msg = plugin.utils.translateCodes(msg);
            List<String> serversList = new ArrayList<>();

            for (Lobby server : BungeeGuard.lobbys) {
                ServerInfo u = server.getServerInfo();
                if (u != null)
                    serversList.add(u.getName());
            }
            if (sender instanceof ProxiedPlayer) {
                String currentServer = ((ProxiedPlayer) sender).getServer().getInfo().getName();
                if (!serversList.contains(currentServer))
                    serversList.add(currentServer);
            }

            BungeeGuardUtils.getMB().broadcastServers(serversList, msg);
        }
    }
}