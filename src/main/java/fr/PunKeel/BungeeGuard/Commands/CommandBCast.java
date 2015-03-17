package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import fr.PunKeel.BungeeGuard.BungeeGuardUtils;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandBCast extends Command {

    private final Main plugin;

    public CommandBCast(Main plugin) {
        super("bcast", "bungee.bcast", "broadcast", "bc");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            BungeeGuardUtils.msgPluginCommand(sender);
            return;
        }
        UUID uuid = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : null;

        BungeeMute mute = plugin.getSanctionManager().findMute(uuid);
        if (mute != null) {
            sender.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
            return;
        }

        if (args.length > 0) {
            String msg = "";
            for (String m : args)
                msg += m + " ";
            List<String> serversList = new ArrayList<>();

            for (ServerInfo server : Main.getServerManager().getOnlineLobbies()) {
                serversList.add(server.getName());
            }
            if (sender instanceof ProxiedPlayer) {
                String currentServer = ((ProxiedPlayer) sender).getServer().getInfo().getName();
                if (!serversList.contains(currentServer))
                    serversList.add(currentServer);
            }

            Main.getMB().broadcastServers(serversList, msg, uuid);
            Main.getMB().notifyStaff("[" + Main.getMB().getServerId() + "] " + sender.getName() + ": /bcast " + Joiner.on(",").join(serversList) + " " + msg);
        }
    }
}