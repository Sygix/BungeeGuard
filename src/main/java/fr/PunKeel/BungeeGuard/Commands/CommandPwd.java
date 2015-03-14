package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandPwd extends Command {

    private final Main plugin;

    public CommandPwd(Main plugin) {
        super("pwd", "bungee.command.pwd");
        this.plugin = plugin;

    }

    @Override
    public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = ((ProxiedPlayer) sender);
        p.sendMessage(TextComponent.fromLegacyText("/home/minecraft/" + p.getServer().getInfo().getName()));
    }
}

