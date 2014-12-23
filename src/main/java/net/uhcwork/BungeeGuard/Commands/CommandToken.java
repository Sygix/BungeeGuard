package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeToken;
import net.uhcwork.BungeeGuard.Models.BungeeTokenUse;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import net.uhcwork.BungeeGuard.Utils.DateUtil;

public class CommandToken extends Command {
    private final Main plugin;

    public CommandToken(Main plugin) {
        super("token", "bungee.command.token");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        boolean canCreate = sender.hasPermission("bungee.token.create");
        if (args.length == 0
                || (!canCreate && args.length != 1)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /token <token>"));
            if (canCreate) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /token + <token> <action> <nb utilisations> <validité>"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Exemple: /token + halloween vip:1mo 10 10d"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Pour un token valide 10 fois, pendant 10 jours, et qui donne 1 mois de vip"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Exemple: /token + noel2014 1000c 30 3h"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Pour un token valide 30 fois, pendant 3 heures, et qui donne 1000 uhcoins"));
            }
            return;
        }
        if (args.length == 1) {
            if (!(sender instanceof ProxiedPlayer))
                return;
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            final String token = args[0];
            plugin.executePersistenceRunnable(new VoidRunner() {
                @Override
                protected void run() {
                    BungeeToken bt = BungeeToken.findFirst("token = ?", token);
                    if (bt == null) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Token invalide."));
                        return;
                    }
                    long count = BungeeTokenUse.count("token = ? AND uuid = ?", token, "" + p.getUniqueId());
                    if (count != 0) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous avez déjà utilisé ce token."));
                        return;
                    }
                    if (bt.hasExpired()) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce token a expiré."));
                        return;
                    }
                    count = BungeeTokenUse.count("token = ?", token);
                    if (bt.isBroken(count)) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce token a déjà été utilisé par trop de joueurs."));
                        return;
                    }
                    if (!applyToken(bt, p)) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Une erreur est survenue."));
                        return;
                    }
                    BungeeTokenUse btu = new BungeeTokenUse();
                    btu.setToken(token);
                    btu.setUuid(p.getUniqueId());
                    btu.saveIt();
                }
            });
            return;
        }
        if (!args[0].equals("+")) {
            sender.sendMessage(TextComponent.fromLegacyText("Action inconnue ..."));
            return;
        }
        if (args.length != 5) {
            sender.sendMessage(TextComponent.fromLegacyText("Usage: Fais /token pour l'avoir!"));
            return;
        }
        final String token = args[1];
        if (token.length() > 10) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Nom de token trop long."));
        }
        final String action = args[2];
        final String utilisations = args[3];
        final String duree = args[4];
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                long count = BungeeToken.count("token = ?", token);
                if (count != 0) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Nom de token déjà utilisé"));
                    return;
                }
                Integer usages = Integer.valueOf(utilisations);
                Long duration = BungeeGuardUtils.parseDuration(duree);

                BungeeToken bt = new BungeeToken();
                bt.setToken(token);
                bt.setAction(action);
                bt.setUtilisations(usages);
                bt.setLifetime(duration);
                bt.setCreatedBy(sender.getName());
                bt.saveIt();
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Token ajouté ! <3"));
            }
        });
    }

    private boolean applyToken(BungeeToken bt, ProxiedPlayer p) {
        String action = bt.getAction();
        if (action.startsWith("vip:")) {
            String duration = action.substring("vip:".length());
            String duree = DateUtil.formatDateDiff(DateUtil.parseDateDiff(duration, true), false);
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + p.getName() + " add vip " + duration);
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous venez de gagner le grade VIP pour une durée de " + ChatColor.GOLD + duree + ChatColor.GREEN + "."));
            return true;
        }

        if (action.startsWith("lutin:")) {
            String duration = action.substring("lutin:".length());
            String duree = DateUtil.formatDateDiff(DateUtil.parseDateDiff(duration, true), false);
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + p.getName() + " add lutin " + duration);
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous venez de gagner le grade Lutin pour une durée de " + ChatColor.GOLD + duree + ChatColor.GREEN + "."));
            return true;
        }
        if (action.endsWith("c")) {
            double coins;
            try {
                coins = Double.parseDouble(action.substring(0, action.length() - 1));
            } catch (NumberFormatException e) {
                return false;
            }
            plugin.getWalletManager().addToBalance(p.getUniqueId(), coins);
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous venez de gagner " + ChatColor.GOLD + coins + " UHCoins" + ChatColor.GREEN + "."));
            return true;
        }
        return false;
    }
}
