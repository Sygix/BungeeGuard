package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import net.uhcwork.BungeeGuard.Utils.DateUtil;
import org.javalite.activejdbc.Base;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Commands (BungeeGuard)
 * Date: 02/11/2014
 * Time: 12:11
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandSeen extends Command {
    private final Main plugin;

    public CommandSeen(Main plugin) {
        super("seen", "bungee.seen", "b:seen");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length == 1) {
            final String playerName = args[0];
            final UUID u = Main.getMB().getUuidFromName(playerName);
            plugin.executePersistenceRunnable(new VoidRunner() {
                @Override
                protected void run() {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + " -- " + playerName + " -- "));
                    long lastOnline = Main.getMB().getLastOnline(u);
                    if (lastOnline == 0)
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "En ligne."));
                    else
                        sender.sendMessage(TextComponent.fromLegacyText("Dernière connexion il y a " + ChatColor.GREEN + DateUtil.formatDateDiff(lastOnline)));
                    long presence = Long.valueOf(String.valueOf(Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE uuid = ? AND leaved_at IS NOT NULL", "" + u)));
                    sender.sendMessage(TextComponent.fromLegacyText("Temps en ligne : " + ChatColor.GREEN + DateUtil.formatDateDiff(System.currentTimeMillis() + 1000 * presence)));

                }
            });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /b:seen <pseudo>"));
        }
    }
}
