package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import fr.PunKeel.BungeeGuard.Utils.DateUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.javalite.activejdbc.Base;

public class CommandBStats extends Command {
    private final static int VERSION_18 = 47;
    final Main plugin;

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
                Integer users = Integer.valueOf(String.valueOf(Base.firstCell("SELECT COUNT(DISTINCT uuid) FROM bungeelitycs;")));
                sender.sendMessage(TextComponent.fromLegacyText("Il y a eu " + ChatColor.GOLD + ChatColor.BOLD + users + ChatColor.RESET + " joueurs différents"));
                long presence = Long.valueOf(String.valueOf(Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE leaved_at IS NOT NULL;")));
                sender.sendMessage(TextComponent.fromLegacyText("Temps " + ChatColor.BOLD + "total" + ChatColor.RESET + " en jeu: " + ChatColor.GREEN + DateUtil.formatDateDiff(1000 * presence, true)));

            }
        });
    }
}
