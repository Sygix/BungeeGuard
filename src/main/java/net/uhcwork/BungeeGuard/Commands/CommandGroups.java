package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PermissionManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Permissions.Group;

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
        super("groups", "bungee.groups");
        this.plugin = plugin;
        this.MB = Main.getMB();
        this.PM = plugin.getPermissionManager();
    }


    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /groups"));
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Liste des groupes"));
        for (Group g : PM.getGroups().values()) {
            sender.sendMessage(TextComponent.fromLegacyText("- " + g.getColor() + g.getName() + ChatColor.RESET + " (" + g.getId() + ")"));
        }

    }
}
