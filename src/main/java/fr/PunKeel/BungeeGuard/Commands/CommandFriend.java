package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.FriendManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Utils.MyBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class CommandFriend extends Command {
    private final static String SEPARATOR = ChatColor.YELLOW + "-----------------------------------------------------";
    private final static Ordering<UUID> orderer = new Ordering<UUID>() {
        @Override
        public int compare(UUID uuidA, UUID uuidB) {
            // Orders, online first
            int scoreA = Main.getMB().isPlayerOnline(uuidA) ? -1 : 0;
            int scoreB = Main.getMB().isPlayerOnline(uuidB) ? -1 : 0;
            return Ints.compare(scoreA, scoreB);
        }
    };
    FriendManager FM;
    MultiBungee MB;
    Joiner joiner = Joiner.on(ChatColor.RESET + ", " + ChatColor.YELLOW).skipNulls();

    public CommandFriend(final Main plugin) {
        super("friend", "bungee.command.friend", "friends", "f");
        FM = plugin.getFriendManager();
        MB = Main.getMB();
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return;
        }
        if (!(sender instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) sender;
        String action = args[0].toLowerCase();
        if (action.equals("ls") || action.equals("list")) {
            listFriends(p);
            return;
        }
        if (args.length != 2) {
            showHelp(sender);
            return;
        }
        String playerName = args[1];
        if (playerName.equals(p.getName())) {
            if (Main.getRandom().nextInt(10) == 5)
                p.sendMessage(fromLegacyText(ChatColor.GOLD + "Vous venez de gagner 1000 UHCoins. Ou pas."));
            else
                p.sendMessage(fromLegacyText(ChatColor.RED + "Vous ne pouvez pas vous ajouter en ami."));
            return;
        }
        UUID playerUuid = MB.getUuidFromName(playerName);
        if (playerUuid == null) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Ce joueur n'est pas en ligne."));
            return;
        }
        if (action.equals("add") || action.equals("ajouter") || action.equals("+") || action.equals("accepter") || action.equals("accept")) {
            addFriend(p, playerUuid);
            return;
        }
        if (action.equals("del") || action.equals("rm") || action.equals("remove") || action.equals("supprimer") || action.equals("-")) {
            delFriend(p, playerUuid);
            return;
        }
        if (action.equals("tp") || action.equals("teleport") || action.equals(">") || action.equals("go")) {
            tpFriend(p, playerUuid);
            return;
        }
        showHelp(sender);
    }

    private void addFriend(ProxiedPlayer p, UUID userB) {
        UUID userA = p.getUniqueId();
        if (!MB.isPlayerOnline(userB) && !FM.askedFriend(userB, userA)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Ce joueur n'est pas en ligne."));
            return;
        }
        if (FM.askedFriend(userA, userB)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous avez déjà ajouté ce joueur à votre liste d'amis."));
            return;
        }
        FM.addFriend(userA, userB, true);
        MB.addFriend(userA, userB);
        switch (FM.getFriendship(userA, userB)) {
            case MUTUAL:
                p.sendMessage(fromLegacyText(ChatColor.GREEN + "Vous êtes désormais mutuellement amis."));
                break;
            case PENDING:
                p.sendMessage(fromLegacyText(ChatColor.GREEN + "Ce joueur a bien été ajouté à votre liste d'amis."));
                p.sendMessage(fromLegacyText(ChatColor.GREEN + "Vous serez officiellement amis si celui-ci vous ajoute à son tour."));
                break;
            default:
                break;
        }
    }

    private void delFriend(ProxiedPlayer p, UUID userB) {
        UUID userA = p.getUniqueId();
        if (!FM.askedFriend(userA, userB)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous n'aviez pas ajouté ce joueur à votre liste d'amis."));
            return;
        }
        FM.removeFriend(userA, userB, true);
        MB.delFriend(userA, userB);
        p.sendMessage(fromLegacyText(ChatColor.GREEN + "Le joueur a été supprimé de votre liste d'amis."));
    }

    private void tpFriend(ProxiedPlayer p, UUID userB) {
        UUID userA = p.getUniqueId();
        if (!FM.getFriendship(userA, userB).equals(FriendManager.STATE.MUTUAL)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous devez être mutuellement amis pour effectuer cette action."));
            return;
        }
        ServerInfo SIA = Main.getMB().getServerFor(userA);
        ServerInfo SIB = Main.getMB().getServerFor(userB);
        if (SIA.equals(SIB)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous êtes sur le même serveur."));
            return;
        }
        if (!SIB.canAccess(p)) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Ce joueur est sur un serveur auquel vous n'avez pas accès"));
            return;
        }
        p.connect(SIB);
        p.sendMessage(fromLegacyText(ChatColor.GREEN + "Hop! Direction le serveur " + Main.getServerManager().getPrettyName(SIB.getName())));
    }

    private void listFriends(ProxiedPlayer p) {
        boolean first;
        MyBuilder friendList;
        p.sendMessage(fromLegacyText(SEPARATOR));
        p.sendMessage(fromLegacyText("" + ChatColor.AQUA + ChatColor.UNDERLINE + "Votre liste d'amis"));
        p.sendMessage(fromLegacyText(" "));
        Collection<UUID> friendsMutual = orderer.sortedCopy(FM.getFriends(p.getUniqueId(), MUTUAL));
        Collection<UUID> friendsPending = orderer.sortedCopy(FM.getFriends(p.getUniqueId(), PENDING));
        Collection<UUID> friendsInvitations = FM.getFriends(p.getUniqueId(), PENDING_OTHER);
        if (!friendsMutual.isEmpty()) {
            friendList = new MyBuilder(ChatColor.RED + "Amis : ");
            first = true;
            for (UUID _u : friendsMutual) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.GRAY + ", ");
                boolean isOnline = Main.getMB().isPlayerOnline(_u);
                friendList.append((isOnline ? ChatColor.GREEN : ChatColor.GRAY) + name);
                if (isOnline) {
                    friendList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.GREEN + "Cliquez pour envoyer un message privé")));
                    friendList.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mp " + name + " "));
                } else {
                    friendList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.RED + "Actuellement déconnecté")));
                }
            }
            p.sendMessage(friendList.create());
            p.sendMessage(fromLegacyText(""));
        }

        if (!friendsPending.isEmpty()) {
            friendList = new MyBuilder(ChatColor.GOLD + "Invitations envoyées : ");
            first = true;
            for (UUID _u : friendsPending) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.GRAY + ", ");
                boolean isOnline = Main.getMB().isPlayerOnline(_u);
                friendList.append(ChatColor.YELLOW + (isOnline ? "" + ChatColor.ITALIC : "") + name);
                if (isOnline) {
                    friendList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.GREEN + "Cliquez pour envoyer un message privé")));
                    friendList.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mp " + name + " "));
                } else {
                    friendList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.RED + "Actuellement déconnecté")));
                }
            }
            p.sendMessage(friendList.create());
        }
        if (!friendsInvitations.isEmpty()) {
            friendList = new MyBuilder(ChatColor.GOLD + "Invitations reçues : ");
            first = true;
            for (UUID _u : friendsInvitations) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.GRAY + ", ");
                friendList.append(ChatColor.YELLOW + name)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.GREEN + "Cliquez pour ajouter en ami")))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend + " + name));
            }
            p.sendMessage(friendList.create());
        }
        if (friendsMutual.isEmpty() && friendsPending.isEmpty()) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous n'avez aucun ami ... Pour le moment."));
            p.sendMessage(fromLegacyText(ChatColor.RED + "Pour ajouter un ami: " + ChatColor.GREEN + "/friend add " + ChatColor.ITALIC + "pseudo"));
        }
        p.sendMessage(fromLegacyText(SEPARATOR));
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(fromLegacyText(SEPARATOR + "\n" +
                ChatColor.GREEN + ChatColor.UNDERLINE + "Aide : Commandes /friend\n" +
                "\n" +
                ChatColor.GOLD + "/friend list" + ChatColor.YELLOW + ": Afficher la liste de vos amis\n" +
                ChatColor.GOLD + "/friend add [pseudo]" + ChatColor.YELLOW + ": Ajouter un ami\n" +
                ChatColor.GOLD + "/friend del [pseudo]" + ChatColor.YELLOW + ": Retirer un ami\n" +
                ChatColor.GOLD + "/friend tp [pseudo]" + ChatColor.YELLOW + ": Permet de se téléporter à un ami\n" +
                "\n" +
                SEPARATOR));
    }
}
