package net.uhcwork.BungeeGuard.Permissions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (bungeeguard)
 * Date: 20/10/2014
 * Time: 17:53
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandGroups extends Command {

    public Main plugin;
    MultiBungee MB;
    PermissionManager PM;

    public CommandGroups(Main plugin) {
        super("groups", "bungeeguard.admin.permission");
        this.plugin = plugin;
        this.MB = Main.getMB();
        this.PM = plugin.getPermissionManager();
    }


    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /groups");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Liste des groupes");
        for (Group g : PM.getGroups().values()) {
            sender.sendMessage("- " + g.getColor() + g.getName() + ChatColor.RESET + "(" + g.getId() + ")");
        }

    }
}
