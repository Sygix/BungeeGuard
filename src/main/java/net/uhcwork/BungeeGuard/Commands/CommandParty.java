package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.CharMatcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PartyManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.UUID;

public class CommandParty extends Command {
    private final Main plugin;
    private final PartyManager PM;
    private final MultiBungee MB;
    private final BaseComponent[] MSG_CREATION = new ComponentBuilder("Vous venez de créer une ").color(ChatColor.GRAY)
            .append("Party").color(ChatColor.GREEN)
            .append(" !").color(ChatColor.GRAY)
            .create();
    private final BaseComponent[] MSG_CREATION2 = new ComponentBuilder("Tapez ").color(ChatColor.GRAY)
            .append("/party invite <nom de joueur>").color(ChatColor.GREEN)
            .append(" pour inviter un joueur").color(ChatColor.GRAY)
            .create();
    private final BaseComponent[] MSG_CREATION3 = new ComponentBuilder("Tapez ").color(ChatColor.GRAY)
            .append("/party help").color(ChatColor.GREEN)
            .append(" pour plus d'informations ...").color(ChatColor.GRAY)
            .create();
    private final CharMatcher partyNameMatcher = CharMatcher.anyOf(".-_").or(CharMatcher.JAVA_LETTER_OR_DIGIT);


    public CommandParty(Main plugin) {
        super("party", "bungee.party.use", "p", "g", "group", "groupe");
        this.plugin = plugin;
        this.PM = plugin.getPartyManager();
        this.MB = Main.getMB();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || !(sender instanceof ProxiedPlayer)) {
            help(sender);
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        String action = args[0];
        boolean showHelp = false;
        switch (action) {
            case "create":
            case "c":
                create(p, args);
                break;
            case "list":
            case "ls":
                list(p);
                break;
            case "invite":
            case "inv":
                invite(p, args);
                break;
            case "accept":
            case "a":
            case "join":
                join(p, args);
                break;
            case "leave":
                leave(p);
                break;
            case "disband":
                disband(p, args);
                break;
            case "chat":
                chat(p);
                break;
            case "owner":
                owner(p, args);
                break;
            case "kick":
                kick(p, args);
                break;
            case "public":
            case "pub":
            case "publique":
                publique(p);
                break;
            default:
                showHelp = true;
                break;
        }
        if (showHelp)
            help(sender);

    }

    private void disband(ProxiedPlayer sender, String[] args) {
        if (!sender.hasPermission("bungee.party.disband")) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Permission refusée."));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party disband <nom de la party>"));
            return;
        }
        String partyName = args[1];
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Party supprimée."));
        MB.disbandParty(partyName);
    }

    private void kick(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party kick <joueur>"));
            return;
        }
        String player = args[1];
        UUID u = MB.getUuidFromName(player);
        PartyManager.Party party = PM.getPartyByPlayer(u);
        if (!sender.hasPermission("bungee.party.kick")) {
            PartyManager.Party p = PM.getPartyByPlayer(sender);
            if (p == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
                return;
            }
            if (!p.isOwner(sender)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Accès refusé."));
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
                return;
            }
        }

        if (u == null || !MB.isPlayerOnline(u)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas connecté."));
            return;
        }

        MB.kickFromParty(party, u);
    }

    private void owner(ProxiedPlayer sender, String[] args) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (p.isOwner(sender) && args.length == 2) {
            String player = args[1];
            UUID u = MB.getUuidFromName(player);
            if (u == null || !MB.isPlayerOnline(u)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas connecté."));
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
                return;
            }
            MB.setPartyOwner(p, u);
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Owner: " + MB.getNameFromUuid(p.getOwner())));
    }

    private void chat(ProxiedPlayer sender) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        boolean isPartyChat = !p.isPartyChat(sender);
        if (isPartyChat)
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "activé."));
        else
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "désactivé."));
        MB.setPartyChat(p, sender.getUniqueId(), isPartyChat);
    }

    private void leave(ProxiedPlayer sender) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        MB.playerLeaveParty(p, sender);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Tu as quitté ta Party"));
    }

    private void publique(ProxiedPlayer sender) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (!p.isOwner(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'as pas le droit de faire ceci."));
            return;
        }
        boolean publique = !p.isPublique();
        MB.setPartyPublique(p, publique);
        if (publique)
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Tout le monde peut désormais rejoindre cette Party sans invitation"));
        else
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Une invitation est requise pour rejoindre cette Party"));
    }

    private void join(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party join <nom>"));
            return;
        }
        if (PM.inParty(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu es déjà dans une Party. Tu ne peux pas en rejoindre une autre."));
            return;
        }
        String party = args[1];
        PartyManager.Party p = PM.getParty(party);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Party " + party + " inconnue."));
            return;
        }
        if (!p.canJoin(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Il vous est impossible de rejoindre cette Party."));
            return;
        }

        MB.addPlayerToParty(p, sender);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Tu es désormais dans la Party " + ChatColor.BOLD + p.getName()));
    }

    private void invite(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party invite <pseudo>"));
            return;
        }
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous ne pouvez lancer cette commande sans être dans une Party"));
            return;
        }
        String joueur = args[1];
        UUID u = Main.getMB().getUuidFromName(joueur);
        if (u == null || !Main.getMB().isPlayerOnline(u)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas en ligne"));
            return;
        }
        PartyManager.Party p2 = PM.getPartyByPlayer(u);
        if (p2 != null) {
            sender.sendMessage(TextComponent.fromLegacyText("Ce joueur est déjà dans une Party."));
            return;
        }
        Main.getMB().inviteParty(p, u);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Joueur invité!"));
    }

    private void list(ProxiedPlayer sender) {
        PM.clean();
        if (PM.getParties().values().size() == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Il n'y a aucune Party."));
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous pouvez en créer une: " + ChatColor.GREEN + "/party create <nom>"));
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Liste des Party"));
        TextComponent TC;
        for (PartyManager.Party p : PM.getParties().values()) {
            TC = new TextComponent("- ");
            TC.addExtra(p.getDisplay());
            sender.sendMessage(TC);
        }
    }

    private void create(ProxiedPlayer sender, String[] args) {
        if (PM.inParty(sender.getUniqueId())) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Il semble que vous soyez déjà membre d'une party"));
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous pouvez toujours la quitter en faisant " + ChatColor.GREEN + "/party leave"));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party create <nom>"));
            return;
        }
        String nom = args[1];
        if (!partyNameMatcher.matchesAllOf(nom)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party create <nom>"));
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Le nom de votre Party ne peut contenir que des chiffres ou des lettres"));
            return;
        }
        if (PM.getParty(nom) != null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce nom de Party est déjà pris, merci d'en choisir un autre"));
            return;
        }
        sender.sendMessage(MSG_CREATION);
        sender.sendMessage(MSG_CREATION2);
        sender.sendMessage(MSG_CREATION3);
        MB.createParty(nom, sender);
    }

    private void help(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Party: Usage"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party create <nom>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party public"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party chat"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party invite <joueur>"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party leave"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party owner [joueur]"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/party kick <joueur>"));
    }
}
