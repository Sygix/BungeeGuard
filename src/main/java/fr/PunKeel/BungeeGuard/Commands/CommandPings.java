package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Utils.ArrayUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;

import static fr.PunKeel.BungeeGuard.Utils.ArrayUtils.quartiles;
import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class CommandPings extends Command {
    private final Main plugin;

    public CommandPings(Main plugin) {
        super("pings", "bungee.pings");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<Integer> transform = Collections2.transform(plugin.getProxy().getPlayers(),
                new Function<ProxiedPlayer, Integer>() {
                    @Override
                    public Integer apply(ProxiedPlayer p) {
                        return p.getPing();
                    }
                });

        int[] pings = ArrayUtils.toPrimitive(transform.toArray(new Integer[transform.size()]));

        Number[] quartiles = quartiles(pings);
        double average = ArrayUtils.average(pings);

        sender.sendMessage(fromLegacyText(ChatColor.DARK_GREEN + "Statistiques ping (" + pings.length + " players)"));
        sender.sendMessage(fromLegacyText(ChatColor.GREEN + "Quartile 1: " + pingColor(quartiles[0])));
        sender.sendMessage(fromLegacyText(ChatColor.GREEN + "Mediane: " + pingColor(quartiles[1])));
        sender.sendMessage(fromLegacyText(ChatColor.GREEN + "Quartile 3: " + pingColor(quartiles[2])));
        sender.sendMessage(fromLegacyText(ChatColor.GREEN + "Average: " + pingColor(average)));
    }

    private String pingColor(double value) {
        ChatColor color;
        if (value < 50)
            color = ChatColor.DARK_GREEN;
        else if (value < 70)
            color = ChatColor.GREEN;
        else if (value < 100)
            color = ChatColor.GRAY;
        else if (value < 150)
            color = ChatColor.YELLOW;
        else if (value < 200)
            color = ChatColor.GOLD;
        else
            color = ChatColor.RED;
        return "" + ChatColor.RESET + color + value;
    }

    private String pingColor(Number value) {
        return pingColor(value.doubleValue());
    }
}