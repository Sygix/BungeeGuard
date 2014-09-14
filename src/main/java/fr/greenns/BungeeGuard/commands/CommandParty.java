package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Party.Party;
import fr.greenns.BungeeGuard.Party.PartyManager;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.commands (bungeeguard)
 * Date: 10/09/2014
 * Time: 20:02
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandParty extends Command {
    public Main plugin;
    public PartyManager PM;
    MultiBungee MB;
    String TAG = "[" + ChatColor.BLUE + "Party" + ChatColor.RESET + "]";
    BaseComponent[] MSG_CREATION = new ComponentBuilder("Vous venez de créer une ").color(ChatColor.GRAY)
            .append("Party").color(ChatColor.GREEN)
            .append(" !").color(ChatColor.GRAY)
            .create();
    BaseComponent[] MSG_CREATION2 = new ComponentBuilder("Tapez ").color(ChatColor.GRAY)
            .append("/party invite <nom de joueur>").color(ChatColor.GREEN)
            .append(" pour inviter un joueur").color(ChatColor.GRAY)
            .create();
    BaseComponent[] MSG_CREATION3 = new ComponentBuilder("Tapez ").color(ChatColor.GRAY)
            .append("/party help").color(ChatColor.GREEN)
            .append(" pour plus d'informations ...").color(ChatColor.GRAY)
            .create();


    public CommandParty(Main plugin) {
        super("party", "party.use", "p", "g", "group", "groupe");
        this.plugin = plugin;
        this.PM = plugin.getPM();
        this.MB = plugin.getMB();
    }

    public static boolean isAlphanumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
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
                list(p, args);
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
            case "disband":
                leave(p, args);
                break;
            case "chat":
                chat(p, args);
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
                publique(p, args);
                break;
            default:
                showHelp = true;
                break;
        }
        if (showHelp)
            help(sender);

    }

    private void kick(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /party kick <joueur>"));
            return;
        }
        if (!p.isOwner(sender)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Accès refusé."));
            return;
        }
        String player = args[1];
        UUID u = MB.getUuidFromName(player);
        if (u == null || !MB.isPlayerOnline(u)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ce joueur n'est pas connecté."));
            return;
        }
        if (!p.isMember(u)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
            return;
        }
        MB.kickFromParty(p, u);
    }

    private void owner(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (p.isOwner(sender) && args.length == 2) {
            String player = args[1];
            UUID u = MB.getUuidFromName(player);
            if (u == null || !MB.isPlayerOnline(u)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Ce joueur n'est pas connecté."));
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
                return;
            }
            MB.setPartyOwner(p, u);
            return;
        }
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Owner: " + MB.getNameFromUuid(p.getOwner())));
    }

    private void chat(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        boolean isPartyChat = !p.isPartyChat(sender);
        if (isPartyChat)
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "activé."));
        else
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "désactivé."));
        MB.setPartyChat(p, sender.getUniqueId(), isPartyChat);
    }

    private void leave(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        MB.playerLeaveParty(p, sender);
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Tu as quitté ta Party"));
    }

    private void publique(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (!p.isOwner(sender)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu n'as pas le droit de faire ceci."));
            return;
        }
        boolean publique = !p.isPublique();
        MB.setPartyPublique(p, publique);
        if (publique)
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Tout le monde peut désormais rejoindre cette Party sans invitation"));
        else
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Une invitation est requise pour rejoindre cette Party"));
    }

    private void join(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /party join <nom>"));
            return;
        }
        if (PM.inParty(sender)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Tu es déjà dans une Party. Tu ne peux pas en rejoindre une autre."));
            return;
        }
        String party = args[1];
        Party p = PM.getParty(party);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Party " + party + " inconnue."));
            return;
        }
        if (!p.canJoin(sender)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Il vous est impossible de rejoindre cette Party."));
            return;
        }

        MB.addPlayerToParty(p, sender);
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Tu es désormais dans la Party " + ChatColor.BOLD + p.getName()));
    }

    private void invite(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /party invite <pseudo>"));
            return;
        }
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Vous ne pouvez lancer cette commande sans être dans une Party"));
            return;
        }
        String joueur = args[1];
        UUID u = plugin.getMB().getUuidFromName(joueur);
        if (u == null || !plugin.getMB().isPlayerOnline(u)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ce joueur n'est pas en ligne"));
            return;
        }
        Party p2 = PM.getPartyByPlayer(u);
        if (p2 != null) {
            sender.sendMessage(new TextComponent("Ce joueur est déjà dans une Party."));
            return;
        }
        plugin.getMB().inviteParty(p, u);
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Joueur invité!"));
    }

    private void list(ProxiedPlayer sender, String[] args) {
        if (PM.getParties().values().size() == 0) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Il n'y a aucune Party."));
            sender.sendMessage(new TextComponent(ChatColor.RED + "Vous pouvez en créer une: " + ChatColor.GREEN + "/party create <nom>"));
            return;
        }
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Liste des Party"));
        TextComponent TC;
        for (Party p : PM.getParties().values()) {
            TC = new TextComponent("- ");
            TC.addExtra(p.getDisplay());
            sender.sendMessage(TC);
        }
    }

    private void create(ProxiedPlayer sender, String[] args) {
        if (PM.inParty(sender.getUniqueId())) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Il semble que vous soyez déjà membre d'une party"));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Vous pouvez toujours la quitter en faisant " + ChatColor.GREEN + "/party leave"));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /party create <nom>"));
            return;
        }
        String nom = args[1];
        if (!isAlphanumeric(nom)) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Usage: /party create <nom>"));
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Le nom de votre Party ne peut contenir que des chiffres ou des lettres"));
            return;
        }
        if (PM.getParty(nom) != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ce nom de Party est déjà pris, merci d'en choisir un autre"));
            return;
        }
        sender.sendMessage(MSG_CREATION);
        sender.sendMessage(MSG_CREATION2);
        sender.sendMessage(MSG_CREATION3);
        MB.createParty(nom, sender);
    }

    private void help(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Party: Usage"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party create <nom>"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party public"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party chat"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party invite <joueur>"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party leave"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party owner [joueur]"));
        sender.sendMessage(new TextComponent(ChatColor.GREEN + "/party kick <joueur>"));
    }
}
