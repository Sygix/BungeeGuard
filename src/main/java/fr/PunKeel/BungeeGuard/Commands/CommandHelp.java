package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandHelp extends Command {
    public CommandHelp(Main plugin) {
        super("help", "bungee.help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Alerte enlèvement !"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vu pour la dernière fois le 14 Mai 2015," +
                "cette commande est depuis portée disparue et activement recherchée par les forces de l'ordre."));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Si vous possédez la moindre information, " +
                "contactez-nous sur Twitter: @UHCGames."));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Merci d'avance pour votre aide !"));
    }
}
