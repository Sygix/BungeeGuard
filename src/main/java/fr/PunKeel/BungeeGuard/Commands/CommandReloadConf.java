package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSub.ReloadConfHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandReloadConf extends Command {
    private final Main plugin;

    public CommandReloadConf(Main plugin) {
        super("b:rl", "bungee.brl");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length != 0) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /b:rl "));
            return;
        }
        new ReloadConfHandler().handle(plugin);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Reload : en cours …"));
    }
}