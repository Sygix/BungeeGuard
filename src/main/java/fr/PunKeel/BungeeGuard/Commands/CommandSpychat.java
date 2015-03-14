package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandSpychat extends Command {

    private final Main plugin;

    public CommandSpychat(Main plugin) {
        super("spychat", "bungee.spychat", "sc");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette command !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        plugin.toggleSpy(p.getUniqueId());
        if (!plugin.isSpying(p.getUniqueId())) {
            p.sendMessage(new ComponentBuilder("Vous avez désactivé ").color(ChatColor.GRAY).append("SpyChat").color(ChatColor.RED).create());
        } else {
            p.sendMessage(new ComponentBuilder("Vous avez activé ").color(ChatColor.GRAY).append("SpyChat").color(ChatColor.GREEN).create());
        }
    }
}