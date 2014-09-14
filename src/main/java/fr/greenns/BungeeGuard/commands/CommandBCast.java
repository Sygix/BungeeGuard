package fr.greenns.BungeeGuard.commands;

import com.google.common.base.Joiner;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandBCast extends Command {

    public Main plugin;

    public CommandBCast(Main plugin) {
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

            msg = BungeeGuardUtils.translateCodes(msg);
            List<String> serversList = new ArrayList<>();

            for (Lobby server : plugin.lobbys) {
                if (server == null)
                    continue;
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
            BungeeGuardUtils.getMB().notifyStaff("[" + BungeeGuardUtils.getMB().getServerId() + "] " + sender.getName() + ": /bcast " + Joiner.on(",").join(serversList) + " " + msg);
        }
    }
}