package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.ServerManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.Collection;

public class CommandList extends Command {

    private final Main plugin;
    MultiBungee MB;
    ServerManager SM;

    public CommandList(Main plugin) {
        super("list", "", "who", "ls", "playerlist", "online", "plist");
        this.plugin = plugin;
        MB = Main.getMB();
        SM = Main.getServerManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int count;
        if (args.length == 0 || !sender.hasPermission("bungee.list.server")) {
            count = Main.getMB().getPlayerCount();
            sender.sendMessage(new ComponentBuilder("Il y a actuellement ").color(ChatColor.AQUA)
                    .append("" + count).color(ChatColor.RED)
                    .append(" joueur" + s(count) + " en ligne sur le serveur !").color(ChatColor.AQUA)
                    .create());
        } else if (args.length == 1) {
            String server = args[0];
            Collection<String> serverNames = SM.matchServer(server);
            if (serverNames.isEmpty()) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serveur inconnu"));
                return;
            }
            for (String serverName : serverNames) {
                count = MB.getPlayersOnServer(serverName).size();
                sender.sendMessage(new ComponentBuilder("Il y a actuellement ").color(ChatColor.AQUA)
                        .append("" + count).color(ChatColor.RED)
                        .append(" joueur" + s(count) + " en ligne sur le serveur ").color(ChatColor.AQUA)
                        .append(serverName).bold(true)
                        .create());
            }
        }
    }

    private String s(int count) {
        return count > 1 ? "s" : "";
    }
}
