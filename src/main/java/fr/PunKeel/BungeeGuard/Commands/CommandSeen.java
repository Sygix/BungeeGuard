package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import fr.PunKeel.BungeeGuard.Utils.DateUtil;
import fr.PunKeel.BungeeGuard.Utils.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.javalite.activejdbc.Base;

import java.util.UUID;

public class CommandSeen extends Command {
    private final Main plugin;

    public CommandSeen(Main plugin) {
        super("seen", "bungee.seen", "b:seen");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length >= 1 && args.length <= 2) {
            final String playerName = args[0];
            final boolean onlyLobby = (args.length == 2 && args[1].equalsIgnoreCase("lobby"));
            final UUID u = Main.getMB().getUuidFromName(playerName);
            plugin.executePersistenceRunnable(new VoidRunner() {
                String serverNameCondition = "";

                @Override
                protected void run() {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + " -- " + playerName + " -- "));
                    if (!onlyLobby) {
                        long lastOnline = Main.getMB().getLastOnline(u);
                        if (lastOnline == 0)
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "En ligne."));
                        else
                            sender.sendMessage(TextComponent.fromLegacyText("Dernière connexion il y a " + ChatColor.GREEN + DateUtil.formatDateDiff(lastOnline, false)));
                    } else {
                        serverNameCondition = "AND server_name LIKE 'lobby%'";
                    }
                    long presence = Long.valueOf(String.valueOf(Base.firstCell("SELECT SUM(TIME_TO_SEC(TIMEDIFF(leaved_at, joined_at))) FROM bungeelitycs WHERE uuid = ? AND leaved_at IS NOT NULL " + serverNameCondition, UUIDUtils.toBytes(u))));
                    sender.sendMessage(TextComponent.fromLegacyText("Temps en ligne " + (onlyLobby ? "(sur lobby)" : "") + ": " + ChatColor.GREEN + DateUtil.formatDateDiff(1000 * presence, true)));

                }
            });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /b:seen <pseudo> [lobby|rien.]"));
        }
    }
}
