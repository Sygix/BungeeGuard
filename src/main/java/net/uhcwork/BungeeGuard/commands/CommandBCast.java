package net.uhcwork.BungeeGuard.commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Lobbies.Lobby;
import net.uhcwork.BungeeGuard.Main;

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
            BungeeGuardUtils.msgPluginCommand(sender);
            return;
        }

        if (args.length > 0) {
            String msg = "";
            for (String m : args)
                msg += m + " ";

            msg = BungeeGuardUtils.translateCodes(msg);
            List<String> serversList = new ArrayList<>();

            for (Lobby server : plugin.getLM().getLobbies()) {
                if (server == null || !server.isOnline())
                    continue;
                serversList.add(server.getName());
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