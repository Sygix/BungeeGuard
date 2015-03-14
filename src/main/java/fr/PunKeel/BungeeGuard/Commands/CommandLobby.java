package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandLobby extends Command {

    private final Main plugin;

    public CommandLobby(Main plugin) {
        super("lobby", "", "leave", "hub", "quit", "spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Pong").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if (Main.getServerManager().isLobby(p.getServer().getInfo())) {
            p.sendMessage(new ComponentBuilder("Vous êtes déjà au lobby.").color(ChatColor.RED).create());
        } else {
            p.connect(ProxyServer.getInstance().getServerInfo("hub"));
            p.sendMessage(new ComponentBuilder("Connexion vers le lobby.").color(ChatColor.GREEN).create());
        }

    }
}