package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.PubSub.ReloadConfHandler;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by mguerreiro on 07/09/2014.
 */
public class CommandReloadConf extends Command {
    /* reloadConf */


    public BungeeGuard plugin;

    public CommandReloadConf(BungeeGuard plugin) {
        super("b:rl", "bungeeguard.brl");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length != 0) {
            sender.sendMessage(ComponentManager.generate(ChatColor.GREEN + "Usage: /b:rl " + ChatColor.RED + "/!\\ Expérimental - DANGER"));
            return;
        }
        new ReloadConfHandler(plugin).handle();
        sender.sendMessage(ComponentManager.generate(ChatColor.RED + "Reload : en cours …"));
        BungeeGuardUtils.getMB().notifyStaff(ChatColor.DARK_RED + "/b:rl par " + sender.getName());
    }
}