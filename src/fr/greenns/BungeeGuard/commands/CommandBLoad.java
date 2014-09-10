package fr.greenns.BungeeGuard.commands;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:45
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class CommandBLoad extends Command {
    public Main plugin;

    public CommandBLoad(Main plugin) {
        super("b:load", "bungeeguard.bload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MultiBungee MB = BungeeGuardUtils.getMB();
        List<String> servers = MB.getAllServers();
        for (String server : servers) {
            sender.sendMessage(ComponentManager.generate(server + ": " + MB.getPlayersOnServer(server).size() + " joueur(s)"));
        }
    }
}