package net.uhcwork.BungeeGuard.Commands;

/**
 * Part of net.uhcwork.BungeeGuard.Wallet (bungeeguard)
 * Date: 27/09/2014
 * Time: 21:34
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.WalletManager;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

public class CommandPoints extends Command {
    private final WalletManager WM;

    public CommandPoints(Main plugin) {
        super("points", "", "money", "coins", "mycoins", "uhcoins", "uhcoin");
        this.WM = plugin.getWalletManager();

    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!WM.isActive(p.getUniqueId())) {
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Votre compte est desactivé !"));
                return;
            }
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Vous avez " + ChatColor.GOLD + WM.getDisplayedBalance(p.getUniqueId()) + ChatColor.AQUA + " UHCoins !"));
            p.sendMessage(PrettyLinkComponent.fromLegacyText(ChatColor.GRAY + "Vous n'avez pas de multiplicateur ! " + ChatColor.AQUA + "(" + ChatColor.RED + " https://STORE.UHCGames.COM/ " + ChatColor.AQUA + ")"));
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous n'etes pas un joueur !"));
        }
    }
}