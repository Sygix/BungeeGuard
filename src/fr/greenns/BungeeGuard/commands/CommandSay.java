package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandSay extends Command {

    public Main plugin;

    public CommandSay(Main plugin) {
        super("say", "bungeeguard.say");
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
            for (String m : args) msg += m + " ";
            BungeeGuardUtils.getMB().broadcastServers("*", msg);
            BungeeGuardUtils.getMB().notifyStaff("[" + BungeeGuardUtils.getMB().getServerId() + "] " + sender.getName() + ": /say " + msg);
        }
    }
}