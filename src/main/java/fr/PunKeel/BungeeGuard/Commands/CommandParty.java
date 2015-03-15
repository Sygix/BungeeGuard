package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PartyManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Utils.MyBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandParty extends Command {
    private final Main plugin;
    private final PartyManager PM;
    private final MultiBungee MB;
    private final BaseComponent[] MSG_HELP = new ComponentBuilder("Tapez ").color(ChatColor.GRAY)
            .append("/party help").color(ChatColor.GREEN)
            .append(" pour plus d'informations …").color(ChatColor.GRAY)
            .create();
    private final BaseComponent[] MSG_PARTYCHAT = new ComponentBuilder("Pour parler dans le chat ").color(ChatColor.GRAY)
            .append("party").color(ChatColor.AQUA)
            .append(", entrez une étoile (").color(ChatColor.GRAY)
            .append("*").color(ChatColor.WHITE)
            .append(") devant votre message.").color(ChatColor.GRAY)
            .create();

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
            case "owner":
                owner(p, args);
                break;
            case "kick":
                kick(p, args);
                break;
            case "info":
            case "infos":
                info(p);
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
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Party dissolvatée."));
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

    private void leave(ProxiedPlayer sender) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        MB.playerLeaveParty(p, sender);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Tu as quitté ta Party"));
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
        sender.sendMessage(MSG_PARTYCHAT);
    }

    private void invite(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /party invite <pseudo>"));
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
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous venez d'inviter " + ChatColor.YELLOW + joueur + ChatColor.GREEN + "dans votre party !"));
        if (p == null) {
            p = PM.createParty(sender.getName(), sender.getUniqueId());
            sender.sendMessage(MSG_HELP);
            sender.sendMessage(MSG_PARTYCHAT);
        }
        Main.getMB().inviteParty(p, u);
    }

    private void info(ProxiedPlayer sender) {
        if (!PM.inParty(sender.getUniqueId())) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        sender.sendMessage(Main.SEPARATOR);
        PartyManager.Party p = PM.getPartyByPlayer(sender.getUniqueId());
        MyBuilder partyList = new MyBuilder(ChatColor.AQUA + "Joueurs dans votre Party : ");
        partyList.append(ChatColor.GREEN + MB.getNameFromUuid(p.getOwner()));
        for (UUID uuid : p.getMembers()) {
            partyList.append(ChatColor.GREEN + MB.getNameFromUuid(uuid));
        }
        sender.sendMessage(partyList.create());
        sender.sendMessage(Main.SEPARATOR);
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
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Cette commande n'existe plus."));
    }

    private void help(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&e-----------------------------------------------------\n" +
                "&a&nAide : Commande /party\n" +
                "&r\n" +
                "&6/party invite [pseudo] &e: Inviter le joueur dans votre party\n" +
                "&6/party join [nom] &e: Rejoindre une party publique avec le nom indiqué\n" +
                "&6/party leave &e: Quitter la party \n" +
                "&6/party kick [pseudo] &e: Expulse le joueur indiqué\n" +
                "&6/party owner [pseudo] &e: Rend le joueur indiqué chef de la party\n" +
                "&r\n" +
                "&e-----------------------------------------------------")));
    }
}
