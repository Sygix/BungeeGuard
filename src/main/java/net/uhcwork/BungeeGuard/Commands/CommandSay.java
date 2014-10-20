package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;

public class CommandSay extends Command {

    public Main plugin;

    public CommandSay(Main plugin) {
        super("say", "bungeeguard.say");
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
            Main.getMB().broadcastServers("*", msg);
            Main.getMB().notifyStaff("[" + Main.getMB().getServerId() + "] " + sender.getName() + ": /say " + msg);
        }
    }
}