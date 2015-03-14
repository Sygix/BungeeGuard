package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import fr.PunKeel.BungeeGuard.Utils.DateUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.javalite.activejdbc.Base;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class CommandBStats extends Command {
    private final static int VERSION_18 = 47;
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
        sender.sendMessage(fromLegacyText(ChatColor.DARK_GREEN + "Répartition des versions MC"));
        int count_18 = 0, count_17 = 0;
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.getPendingConnection().getVersion() >= VERSION_18)
                count_18++;
            else
                count_17++;
        }
        int perc = Math.round(100 * count_18 / (count_18 + count_17));
        sender.sendMessage(fromLegacyText(ChatColor.GOLD + "Joueurs en 1.8: " + ChatColor.GREEN + count_18 + " (" + perc + "%)"));
        sender.sendMessage(fromLegacyText(ChatColor.YELLOW + "Joueurs en 1.7: " + ChatColor.GREEN + count_17 + " (le reste, lol)"));

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
