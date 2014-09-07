package fr.greenns.BungeeGuard.commands;

/**
 * Created by mguerreiro on 07/09/2014.
 */

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class CommandBLoad extends Command {
    public BungeeGuard plugin;

    public CommandBLoad(BungeeGuard plugin) {
        super("b:load", "bungeeguard.bload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MultiBungee MB = BungeeGuardUtils.getMB();
        Map<String, Collection<UUID>> x = MB.getServerToPlayers().asMap();
        for (String server : x.keySet()) {
            sender.sendMessage(ComponentManager.generate(server + ": " + x.get(server).size() + " joueur(s)"));
        }
    }
}