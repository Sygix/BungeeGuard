package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PartyManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Utils.MyBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class CommandParty extends Command {
    private static final String HELP_FRIENDS = "&6/party friends &e: Invite tous vos amis dans votre party\n";
    private static final String HELP_TELEPORT = "&6/party teleport &e: Vous envoie sur le serveur du chef de party\n";
    private final Main plugin;
    private final PartyManager PM;
    private final MultiBungee MB;
    private final BaseComponent[] MSG_HELP = new MyBuilder(PartyManager.TAG + ChatColor.GRAY + "Tapez ")
            .append(ChatColor.GREEN + "/party help")
            .append(ChatColor.GRAY + " pour plus d'informations …")
            .create();
    private final BaseComponent[] MSG_PARTYCHAT = new MyBuilder(PartyManager.TAG + ChatColor.GRAY + "Pour parler dans le chat ")
            .append(ChatColor.AQUA + "party")
            .append(ChatColor.GRAY + ", entrez une étoile (")
            .append(ChatColor.WHITE + "*")
            .append(ChatColor.GRAY + ") devant votre message.")
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
                create(p);
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
            case "chef":
            case "lead":
            case "leader":
                owner(p, args);
                break;
            case "kick":
                kick(p, args);
                break;
            case "info":
            case "infos":
            case "list":
                info(p);
                break;
            case "tp":
            case "teleport":
                teleport(p);
                break;
            case "friend":
            case "friends":
                friends(p);
                break;
            default:
                showHelp = true;
                break;
        }
        if (showHelp)
            help(sender);

    }

    private void friends(ProxiedPlayer sender) {
        if (!sender.hasPermission("bungee.party.friends")) {
            Main.missPermission(sender, "bungee.party.friends");
            return;
        }
        Collection<UUID> friends = plugin.getFriendManager().getOnlineFriends(sender.getUniqueId());
        if (friends.size() == 0) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Vous n'avez aucun ami en ligne."));
            return;
        }
        friends = Collections2.filter(friends, new Predicate<UUID>() {
            @Override
            public boolean apply(UUID uuid) {
                PartyManager.Party p = PM.getPartyByPlayer(uuid);
                return p == null;
            }
        });
        if (friends.size() == 0) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tous vos amis sont déjà dans une party."));
            return;
        }
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Vous venez d'inviter vos amis dans votre party !"));
        if (p == null) {
            p = PM.createParty(sender.getUniqueId());
            MB.createParty(sender);
            sender.sendMessage(MSG_HELP);
            sender.sendMessage(MSG_PARTYCHAT);
        }
        for (UUID friend : friends) {
            Main.getMB().inviteParty(p, friend);
        }
    }

    private void teleport(ProxiedPlayer sender) {
        if (!sender.hasPermission("bungee.party.teleport")) {
            Main.missPermission(sender, "bungee.party.teleport");
            return;
        }
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (p.isOwner(sender)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Vous êtes déjà sur le même serveur"));
            return;
        }
        UUID owner = p.getOwner();
        ServerInfo server = MB.getServerFor(owner);
        assert server != null;
        String serverName = server.getName();
        if (serverName.equals(sender.getServer().getInfo().getName())) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Vous êtes déjà sur le même serveur"));
            return;
        }
        if (!server.canAccess(sender)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "L'accès à ce serveur vous est interdit"));
            return;
        }
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Envoi sur le serveur " + Main.getServerManager().getPrettyName(serverName)));
        sender.connect(server);
    }


    private void disband(ProxiedPlayer sender, String[] args) {
        UUID partyOwner;
        if (args.length == 2) {
            if (!sender.hasPermission("bungee.party.disband")) {
                Main.missPermission(sender, "bungee.party.disband");
                return;
            }
            String partyName = args[1];
            partyOwner = UUID.fromString(partyName);
            if (plugin.getPartyManager().getParty(partyOwner) == null) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Party " + partyName + " inexistante."));
                return;
            }

            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Party " + partyName + " dissoute."));
        } else {
            PartyManager.Party p = PM.getPartyByPlayer(sender);
            if (p == null) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune party."));
                return;
            }
            if (!p.isOwner(sender)) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu dois être owner de la party pour la dissoudre."));
                return;
            }
            partyOwner = p.getOwner();
        }
        MB.disbandParty(partyOwner);
    }

    private void kick(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Usage: /party kick <joueur>"));
            return;
        }
        String player = args[1];
        UUID u = MB.getUuidFromName(player);
        PartyManager.Party party = PM.getPartyByPlayer(u);
        if (!sender.hasPermission("bungee.party.kick")) {
            PartyManager.Party p = PM.getPartyByPlayer(sender);
            if (p == null) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
                return;
            }
            if (!p.isOwner(sender)) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Accès refusé."));
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
                return;
            }
        }

        if (u == null || !MB.isPlayerOnline(u)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur n'est pas connecté."));
            return;
        }

        MB.kickFromParty(party, u);
    }

    private void owner(ProxiedPlayer sender, String[] args) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        if (p.isOwner(sender) && args.length == 2) {
            String player = args[1];
            UUID u = MB.getUuidFromName(player);
            if (u == null || !MB.isPlayerOnline(u)) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur n'est pas connecté."));
                return;
            }
            if (!p.isMember(u)) {
                sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur n'est pas dans ta Party."));
                return;
            }
            MB.setPartyOwner(p, u);
            return;
        }
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GRAY + "Chef de party: " + ChatColor.GREEN + MB.getNameFromUuid(p.getOwner())));
    }

    private void leave(ProxiedPlayer sender) {
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        if (p == null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        MB.playerLeaveParty(p, sender);
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Tu as quitté ta Party"));
    }

    private void join(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Usage: /party join <nom>"));
            return;
        }
        if (PM.inParty(sender)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu es déjà dans une Party. Tu ne peux pas en rejoindre une autre."));
            return;
        }
        String party = args[1];
        UUID partyOwner = MB.getUuidFromName(party);
        if (partyOwner == null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "La Party " + party + " n'existe pas."));
            return;
        }
        PartyManager.Party p = PM.getParty(partyOwner);
        if (p == null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "La Party " + party + " n'existe pas."));
            return;
        }
        if (!p.canJoin(sender)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Il vous est impossible de rejoindre cette Party."));
            return;
        }

        MB.addPlayerToParty(p, sender);
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Tu es désormais dans la Party de " + ChatColor.BOLD + p.getName()));
        sender.sendMessage(MSG_PARTYCHAT);
    }

    private void invite(ProxiedPlayer sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Usage: /party invite <pseudo>"));
            return;
        }
        String joueur = args[1];
        UUID u = Main.getMB().getUuidFromName(joueur);
        if (u == null || !Main.getMB().isPlayerOnline(u)) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur n'est pas en ligne"));
            return;
        }
        PartyManager.Party p2 = PM.getPartyByPlayer(u);
        if (p2 != null) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Ce joueur est déjà dans une Party."));
            return;
        }
        PartyManager.Party p = PM.getPartyByPlayer(sender);
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.GREEN + "Vous venez d'inviter " + ChatColor.YELLOW + joueur + ChatColor.GREEN + " dans votre party !"));
        if (p == null) {
            p = PM.createParty(sender.getUniqueId());
            MB.createParty(sender);
            sender.sendMessage(MSG_HELP);
            sender.sendMessage(MSG_PARTYCHAT);
        }
        Main.getMB().inviteParty(p, u);
    }

    private void info(ProxiedPlayer sender) {
        if (!PM.inParty(sender.getUniqueId())) {
            sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Tu n'es dans aucune Party, cette commande t'es interdite."));
            return;
        }
        sender.sendMessage(Main.SEPARATOR);
        PartyManager.Party p = PM.getPartyByPlayer(sender.getUniqueId());
        final UUID owner = p.getOwner();
        MyBuilder partyList = new MyBuilder(PartyManager.TAG + ChatColor.AQUA + "Joueurs dans votre Party : ");
        partyList.append(ChatColor.GREEN + MB.getNameFromUuid(owner) + " ");
        partyList.append(ChatColor.YELLOW + Joiner.on(" ").skipNulls().join(Collections2.transform(p.getMembers(), new Function<UUID, String>() {
            @Override
            public String apply(UUID uuid) {
                if (uuid.equals(owner))
                    return null;
                return MB.getNameFromUuid(uuid);
            }
        })));
        sender.sendMessage(partyList.create());
        sender.sendMessage(Main.SEPARATOR);
    }

    private void create(ProxiedPlayer sender) {
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Cette commande n'existe plus."));
        sender.sendMessage(fromLegacyText(PartyManager.TAG + ChatColor.RED + "Utilisez directement " + ChatColor.GREEN + "/party invite " + ChatColor.YELLOW + "joueur"));
    }

    private void help(CommandSender sender) {
        sender.sendMessage(fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&e-----------------------------------------------------\n" +
                "&a&nAide : Commande /party\n" +
                "&r\n" +
                "&6/party invite [pseudo] &e: Inviter le joueur dans votre party\n" +
                (sender.hasPermission("bungee.party.friends") ? HELP_FRIENDS : "") +
                "&6/party info &e: Liste les joueurs dans votre party\n" +
                "&6/party leave &e: Quitter la party \n" +
                "&6/party kick [pseudo] &e: Expulse le joueur indiqué\n" +
                "&6/party disband &e: Dissout la party\n" +
                "&6/party owner [pseudo] &e: Rend le joueur indiqué chef de la party\n" +
                (sender.hasPermission("bungee.party.teleport") ? HELP_TELEPORT : "") +
                "&r\n" +
                "&e-----------------------------------------------------")));
    }
}
