package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Party.Party;
import fr.greenns.BungeeGuard.Party.PartyManager;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang.StringUtils;

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
        super("party", "party.help");
        this.plugin = plugin;
        this.PM = plugin.getPM();
        this.MB = plugin.getMB();
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
            sender.sendMessage(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite.");
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.GREEN + "Usage: /party kick <joueur>");
            return;
        }
        if (!p.isOwner(sender)) {
            sender.sendMessage(ChatColor.RED + "Accès refusé.");
            return;
        }
        String player = args[1];
        UUID u = MB.getUuidFromName(player);
        if (u == null || !MB.isPlayerOnline(u)) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas connecté.");
            return;
        }
        if (!p.isMember(u)) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta Party.");
            return;
        }
        MB.kickFromParty(p, u);
    }

    private void owner(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite.");
            return;
        }
        if (p.isOwner(sender) && args.length == 2) {
            String player = args[1];
            UUID u = MB.getUuidFromName(player);
            if (u == null || !MB.isPlayerOnline(u)) {
                sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas connecté.");
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta Party.");
                return;
            }
            MB.setPartyOwner(p, u);
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Owner: " + MB.getNameFromUuid(p.getOwner()));
    }

    private void chat(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite.");
            return;
        }
        boolean isPartyChat = !p.isPartyChat(sender);
        if (isPartyChat)
            sender.sendMessage(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "activé.");
        else
            sender.sendMessage(ChatColor.GREEN + "Party Chat " + ChatColor.BOLD + "désactivé.");
        MB.setPartyChat(p, sender.getUniqueId(), isPartyChat);
    }

    private void leave(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite.");
            return;
        }
        MB.playerLeaveParty(p, sender);
        sender.sendMessage(ChatColor.GREEN + "Tu as quitté ta Party");
    }

    private void publique(ProxiedPlayer sender, String[] args) {
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite.");
            return;
        }
        if (!p.isOwner(sender)) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas le droit de faire ceci.");
            return;
        }
        boolean publique = !p.isPublique();
        MB.setPartyPublique(p, publique);
        if (publique)
            sender.sendMessage(ChatColor.GRAY + "Tout le monde peut désormais rejoindre cette Party sans invitation");
        else
            sender.sendMessage(ChatColor.GRAY + "Une invitation est requise pour rejoindre cette Party");
    }

    private void join(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "Usage: /party join <nom>");
            return;
        }
        if (PM.inParty(sender)) {
            sender.sendMessage(ChatColor.RED + "Tu es déjà dans une Party. Tu ne peux pas en rejoindre une autre.");
            return;
        }
        String party = args[1];
        Party p = PM.getParty(party);
        if (p.canJoin(sender)) {
            MB.addPlayerToParty(p, sender);
            sender.sendMessage(ChatColor.GREEN + "Tu es désormais dans la Party " + ChatColor.BOLD + p.getName());
        }
    }

    private void invite(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "Usage: /party invite <pseudo>");
            return;
        }
        Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Vous ne pouvez lancer cette commande sans être dans une Party");
            return;
        }
        String joueur = args[2];
        UUID u = plugin.getMB().getUuidFromName(joueur);
        if (u == null || !plugin.getMB().isPlayerOnline(u)) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas en ligne");
            return;
        }
        Party p2 = PM.getPartyByPlayer(u);
        if (p2 != null) {
            sender.sendMessage("Ce joueur est déjà dans une Party.");
            return;
        }
        plugin.getMB().inviteParty(p, u);
        sender.sendMessage(ChatColor.GREEN + "Joueur invité!");
    }

    private void list(ProxiedPlayer sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Liste des Party");
        for (Party p : PM.getParties().values()) {
            String membres = "";
            membres += ChatColor.BOLD + plugin.getMB().getNameFromUuid(p.getOwner());
            for (UUID m : p.getMembers()) {
                if (p.getOwner().equals(m))
                    continue;
                membres += " " + plugin.getMB().getNameFromUuid(m);
            }
            sender.sendMessage(new ComponentBuilder("- ")
                    .append(p.getName()).bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(membres).create()))
                    .create());
        }
    }

    private void create(ProxiedPlayer sender, String[] args) {
        if (PM.inParty(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Il semble que vous soyez déjà membre d'une party");
            sender.sendMessage(ChatColor.GRAY + "Vous pouvez toujours la quitter en faisant " + ChatColor.GREEN + "/party leave");
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "Usage: /party create <nom>");
            return;
        }
        String nom = args[1];
        if (!StringUtils.isAlphanumeric(nom)) {
            sender.sendMessage(ChatColor.GREEN + "Usage: /party create <nom>");
            sender.sendMessage(ChatColor.GREEN + "Le nom de votre Party ne peut contenir que des chiffres ou des lettres");
            return;
        }
        if (PM.getParty(nom) != null) {
            sender.sendMessage(ChatColor.RED + "Ce nom de Party est déjà pris, merci d'en choisir un autre");
            return;
        }
        sender.sendMessage(MSG_CREATION);
        sender.sendMessage(MSG_CREATION2);
        sender.sendMessage(MSG_CREATION3);
        PM.createParty(nom, sender);
    }

    private void help(CommandSender sender) {
        sender.sendMessage(new TextComponent("Bientôt de retour :]"));
    }

    /*
    Party:
        nom (String)
        owner (uuid)
        members: List<UUID>
        partyChat = List<UUID>

        Actions:
            create(String nom, UUID owner)
            setOwner(UUID newOwner)
            addPlayer(UUID joueur, boolean notification_joueur, boolean notification_party)
            removePlayer(UUID joueur, boolean notification_joueur, boolean notification_party)
            delete(PartyDeleteReason raison)
            invitePlayer(UUID joueur)
            chat(UUID sender, String message)

            isMember(UUID joueur) -> boolean
            canJoin(UUID joueur) -> boolean


    PartyManager:
        Party: Map<String name, Party p>

        Actions:
            getParty(UUID joueur)
            addToParty(UUID joueur, String partyName)
            togglePartyChat(UUID joueur)
            canJoin(UUID joueur
     */

    /*
    Processus:
    Création de team : /party create <nom>
    Affichage de l'aide rapide, TEAM LIMITEE A 10 JOUEURS

    Si quite serveur,
                     - si team vide: suppression, libération du nom
                     - sinon, annonce à la team + si owner quitte, prochain joueur owner

    Owner peut rendre la party publique avec /p public
        Si publique: tout membre peut rejoindre avec /p join
        Sinon, un membre de la team doit inviter d'autres joueurs à rejoindre:
            /p invite <pseudo>
            > <sender> vous a invité à rejoindre sa team <Nom>: [/p join <Nom>](Accepter) ?
            Ou acceptation manuelle : /p join ou /p join <Nom>

    Owner peut /p kick un joueur,
    Joueur peut à tout moment /p leave

    Si Owner rejoint un serveur de jeu (= non lobby), les autres rejoignent aussi.

    Joueur peut /p chat, qui (dés)active le chat en team

    Events: NewMember, RemovedMember, JoinServer : message aux membres de la team
     */
}
