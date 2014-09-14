package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.ReloadConfHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 22:02
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandReloadConf extends Command {
    /* reloadConf */


    public Main plugin;

    public CommandReloadConf(Main plugin) {
        super("b:rl", "bungeeguard.brl");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length != 0) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /b:rl "));
            return;
        }
        new ReloadConfHandler(plugin).handle();
        sender.sendMessage(new TextComponent(ChatColor.RED + "Reload : en cours …"));
        BungeeGuardUtils.getMB().notifyStaff(ChatColor.DARK_RED + "/b:rl par " + sender.getName());
    }
}