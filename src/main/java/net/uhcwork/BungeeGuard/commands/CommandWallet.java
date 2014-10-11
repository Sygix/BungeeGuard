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
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Wallet.WalletManager;

import java.util.UUID;

public class CommandWallet extends Command {

    Main plugin;
    WalletManager WM;
    MultiBungee MB;
    String prefix = "[" + ChatColor.AQUA + "Wallet" + ChatColor.RESET + "] ";

    public CommandWallet(Main plugin) {
        super("wallet", "wallet.admin");
        this.plugin = plugin;
        this.WM = plugin.getWM();
        this.MB = plugin.getMB();
    }

    public void execute(CommandSender sender, String[] args) {
        String name;
        UUID uuid;
        if (!sender.hasPermission("wallet.admin")) {
            return;
        }

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
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le solde de " + ChatColor.AQUA + name + " " + ChatColor.GRAY + "est de " + ChatColor.GREEN + WM.getBalance(uuid) + ChatColor.GOLD + " UHCoins"));
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
            } else if (args[0].equalsIgnoreCase("toggle")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                if (WM.isActive(uuid)) {
                    WM.setInactive(uuid);
                    sender.sendMessage(TextComponent.fromLegacyText(prefix + "§cLe compte du joueur " + name + " a été désactivé !"));
                } else {
                    WM.setActive(uuid);
                    sender.sendMessage(TextComponent.fromLegacyText(prefix + "§aLe compte du joueur " + name + " a été activé !"));
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                WM.setBalance(uuid, amount);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le compte du joueur " + ChatColor.AQUA + name + ChatColor.GRAY + " a été defini à " + ChatColor.GREEN + amount + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
            } else if (args[0].equalsIgnoreCase("add")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                WM.addToBalance(uuid, amount);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le compte du joueur " + ChatColor.AQUA + name + ChatColor.GRAY + " a été crédité de " + ChatColor.GREEN + amount + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le nouveau solde de " + ChatColor.AQUA + name + ChatColor.GRAY + " est de " + ChatColor.GREEN + WM.getBalance(uuid) + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
            } else if (args[0].equalsIgnoreCase("sub")) {
                name = args[1];
                uuid = MB.getUuidFromName(name);
                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inconnu"));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                WM.addToBalance(uuid, -amount);
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le compte du joueur " + ChatColor.AQUA + name + ChatColor.GRAY + " a été crédité de " + ChatColor.GREEN + amount + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
                sender.sendMessage(TextComponent.fromLegacyText(prefix + ChatColor.GRAY + "Le nouveau solde de " + ChatColor.AQUA + name + ChatColor.GRAY + " est de " + ChatColor.GREEN + WM.getBalance(uuid) + ChatColor.GOLD + " UHCoins " + ChatColor.GRAY + "!"));
            }
        }
    }

    private void help(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet " + ChatColor.GRAY + "[help/?]"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet " + ChatColor.GRAY + "<player>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet set " + ChatColor.GRAY + "<player> <amount>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet reset " + ChatColor.GRAY + "<player>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet add " + ChatColor.GRAY + "<player> <amount>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet sub " + ChatColor.GRAY + "<player> <amount>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "/wallet toggle " + ChatColor.GRAY + "<player>"));
    }
}
