package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandSay extends Command {

    public BungeeGuard plugin;

    public CommandSay(BungeeGuard plugin) {
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
        }
    }
}