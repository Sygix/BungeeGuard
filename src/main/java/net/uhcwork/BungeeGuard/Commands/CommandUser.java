package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PermissionManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Permissions.Group;
import net.uhcwork.BungeeGuard.Permissions.User;
import net.uhcwork.BungeeGuard.Permissions.UserModel;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import net.uhcwork.BungeeGuard.Utils.DateUtil;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (bungeeguard)
 * Date: 20/10/2014
 * Time: 16:47
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandUser extends Command {

    private final Main plugin;
    private final MultiBungee MB;
    private final PermissionManager PM;

    public CommandUser(Main plugin) {
        super("user", "bungee.permission");
        this.plugin = plugin;
        this.MB = Main.getMB();
        this.PM = plugin.getPermissionManager();
    }


    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length == 0) {
            usage(sender);
            return;
        }
        final String playerName = args[0];
        final UUID uuid = MB.getUuidFromName(playerName);
        if (uuid == null) {
            sender.sendMessage(TextComponent.fromLegacyText("Joueur inconnu."));
            return;
        }
        final User u = PM.getUser(uuid);
        sender.sendMessage(TextComponent.fromLegacyText("Joueur " + ChatColor.GREEN + playerName));
        if (args.length == 1) {
            if (u == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Groupe : ... Aucun :("));
                return;
            }
            plugin.executePersistenceRunnable(new VoidRunner() {
                @Override
                protected void run() {
                    for (UserModel g : u.getGroupes()) {
                        Timestamp until = g.getUntil();
                        Group groupe = PM.getGroup(g.getGroup());
                        if (until != null && until.getTime() > 0) {
                            sender.sendMessage(TextComponent.fromLegacyText("- " + groupe.getColor() + groupe.getName() + ChatColor.RESET + " pour encore " + ChatColor.GOLD +
                                    DateUtil.formatDateDiff(until.getTime())));
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText("- " + groupe.getColor() + groupe.getName() + ChatColor.RESET + " à vie."));
                        }
                    }
                }
            });
            return;
        }

        if (args.length == 3 || args.length == 4) {
            String action = args[1];
            final String group_id = args[2];
            final Group groupe = PM.getGroup(group_id);
            if (groupe == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Groupe inexistant"));
                return;
            }
            final String duration = (args.length == 4) ? args[3] : "";
            if (action.equalsIgnoreCase("add")) {
                plugin.executePersistenceRunnable(new VoidRunner() {
                    @Override
                    protected void run() {
                        if (duration.isEmpty())
                            u.addGroup(groupe, null);
                        else
                            u.addGroup(groupe, BungeeGuardUtils.parseDuration(duration));
                    }
                });
                MB.invalidatePermissionUser(u.getUuid());
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Groupe ajouté"));
            }
            if (action.equalsIgnoreCase("del")) {
                plugin.executePersistenceRunnable(new VoidRunner() {
                    @Override
                    protected void run() {
                        u.removeGroup(groupe);
                    }
                });
                MB.invalidatePermissionUser(u.getUuid());
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Groupe supprimé"));
            }


        }
    }

    private void usage(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /user <pseudo>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ou /user <pseudo> add <groupe>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ou /user <pseudo> add <groupe> <duree>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ou /user <pseudo> del <groupe>"));
    }
}
