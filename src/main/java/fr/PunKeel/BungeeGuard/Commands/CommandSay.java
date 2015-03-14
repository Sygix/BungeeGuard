package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.BungeeGuardUtils;
import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandSay extends Command {

    public CommandSay(Main plugin) {
        super("say", "bungee.say");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            BungeeGuardUtils.msgPluginCommand(sender);
            return;
        }

        if (args.length > 0) {
            UUID uuid = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : null;
            String msg = "";
            for (String m : args)
                msg += m + " ";
            Main.getMB().broadcastServers("*", msg, uuid);
            Main.getMB().notifyStaff("[" + Main.getMB().getServerId() + "] " + sender.getName() + ": /say " + msg);
        }
    }
}