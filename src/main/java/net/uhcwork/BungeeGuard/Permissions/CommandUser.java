package net.uhcwork.BungeeGuard.Permissions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
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

    public Main plugin;
    MultiBungee MB;
    PermissionManager PM;

    public CommandUser(Main plugin) {
        super("user", "bungeeguard.admin.permission");
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
            sender.sendMessage("Joueur inconnu.");
            return;
        }
        final User u = PM.getUser(uuid);
        sender.sendMessage("Joueur " + ChatColor.GREEN + playerName);
        if (args.length == 1) {
            if (u == null) {
                sender.sendMessage(ChatColor.GOLD + "Groupe : ... Aucun :(");
                return;
            }
            plugin.executePersistenceRunnable(new VoidRunner() {
                @Override
                protected void run() {
                    for (UserModel g : u.getGroupes()) {
                        Timestamp until = g.getUntil();
                        Group groupe = PM.getGroup(g.getGroup());
                        if (until != null && until.getTime() > 0) {
                            sender.sendMessage("- " + groupe.getColor() + groupe.getName() + ChatColor.RESET + " pour encore " + ChatColor.GOLD +
                                    DateUtil.formatDateDiff(until.getTime()));
                        } else {
                            sender.sendMessage("- " + groupe.getColor() + groupe.getName() + ChatColor.RESET + " à vie.");
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
                sender.sendMessage(ChatColor.RED + "Groupe inexistant");
                return;
            }
            String duration = "";
            if (args.length == 4) {
                duration = args[3];
            }
            if (action.equalsIgnoreCase("add")) {
                final String finalDuration = duration;
                plugin.executePersistenceRunnable(new VoidRunner() {
                    @Override
                    protected void run() {
                        u.addGroup(groupe, BungeeGuardUtils.parseDuration(finalDuration));
                        PM.invalidateUser(uuid);
                    }
                });
                sender.sendMessage(ChatColor.GREEN + "Groupe ajouté");
            }
            if (action.equalsIgnoreCase("del")) {
                plugin.executePersistenceRunnable(new VoidRunner() {
                    @Override
                    protected void run() {
                        u.removeGroup(groupe);
                        PM.invalidateUser(uuid);
                    }
                });
                sender.sendMessage(ChatColor.GREEN + "Groupe supprimé");
            }


        }
    }

    private void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /user <pseudo>");
        sender.sendMessage(ChatColor.RED + "Ou /user <pseudo> add <groupe>");
        sender.sendMessage(ChatColor.RED + "Ou /user <pseudo> add <groupe> <duree>");
        sender.sendMessage(ChatColor.RED + "Ou /user <pseudo> del <groupe>");
    }
}
