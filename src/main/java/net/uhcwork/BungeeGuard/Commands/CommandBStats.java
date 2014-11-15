package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import net.uhcwork.BungeeGuard.Utils.DateUtil;
import org.javalite.activejdbc.Base;

/**
 * Part of net.uhcwork.BungeeGuard.Commands (BungeeGuard)
 * Date: 02/11/2014
 * Time: 12:39
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandBStats extends Command {
    Main plugin;

    public CommandBStats(Main plugin) {
        super("b:stats", "bungee.bstats");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /b:stats"));
            return;
        }
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + " -- Statistiques -- "));
                long presence = Long.valueOf(String.valueOf(Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE leaved_at IS NOT NULL;")));
                sender.sendMessage(TextComponent.fromLegacyText("Temps " + ChatColor.BOLD + "total" + ChatColor.RESET + " en jeu: " + ChatColor.GREEN + DateUtil.formatDateDiff(System.currentTimeMillis() + 1000 * presence)));
                Integer users = Integer.valueOf(String.valueOf(Base.firstCell("SELECT COUNT(DISTINCT uuid) FROM bungeelitycs;")));
                sender.sendMessage(TextComponent.fromLegacyText("Il y a eu " + ChatColor.GOLD + ChatColor.BOLD + users + ChatColor.RESET + " joueurs différents"));

            }
        });
    }
}
