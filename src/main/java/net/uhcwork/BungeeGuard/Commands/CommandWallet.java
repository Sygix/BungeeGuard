package net.uhcwork.BungeeGuard.Commands;

/**
 * Part of net.uhcwork.BungeeGuard.Wallet (bungeeguard)
 * Date: 27/09/2014
 * Time: 21:36
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.WalletManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.UUID;

public class CommandWallet extends Command {

    private final Main plugin;
    private final WalletManager WM;
    private final MultiBungee MB;
    private final String prefix = "[" + ChatColor.AQUA + "Wallet" + ChatColor.RESET + "] ";

    public CommandWallet(Main plugin) {
        super("wallet", "bungee.wallet.admin");
        this.plugin = plugin;
        this.WM = plugin.getWalletManager();
        this.MB = Main.getMB();
    }

    public void execute(CommandSender sender, String[] args) {
        String name;
        UUID uuid;
        if (args.length == 0) {
            help(sender);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                help(sender);
            } else {
                name = args[0];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le solde de " + ChatColor.AQUA + name + " " + ChatColor.GRAY + "est de " + ChatColor.GREEN + WM.getDisplayedBalance(uuid) + ChatColor.GOLD + " UHCoins"));
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reset")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                WM.setBalance(uuid, 0);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le solde de " + ChatColor.AQUA + name + ChatColor.GRAY + " a été remis a zero !"));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                double amount = Double.parseDouble(args[2]);
                WM.setBalance(uuid, amount);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le compte du joueur " + ChatColor.AQUA + name + ChatColor.GRAY + " a été defini à " + ChatColor.GREEN + WM.getDisplayedBalance(amount) + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
            } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("sub")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                double amount = Double.parseDouble(args[2]);
                amount *= (args[0].equalsIgnoreCase("sub") ? -1 : 1);
                WM.addToBalance(uuid, amount);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le compte du joueur " + ChatColor.AQUA + name + ChatColor.GRAY + " a été crédité de " + ChatColor.GREEN + WM.getDisplayedBalance(amount) + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le nouveau solde de " + ChatColor.AQUA + name + ChatColor.GRAY + " est de " + ChatColor.GREEN + WM.getDisplayedBalance(uuid) + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
            }
        }
    }

    private void help(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet " + ChatColor.GRAY + "[help/?]"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet " + ChatColor.GRAY + "<player>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet reset " + ChatColor.GRAY + "<player>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet (set,add,sub) " + ChatColor.GRAY + "<player> <amount>"));
    }
}
