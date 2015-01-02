package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.FriendManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Utils.MyBuilder;

import java.util.Collection;
import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;
import static net.uhcwork.BungeeGuard.Managers.FriendManager.STATE.*;

public class CommandFriend extends Command {
    FriendManager FM;
    MultiBungee MB;
    Joiner joiner = Joiner.on(ChatColor.RESET + ", " + ChatColor.YELLOW).skipNulls();

    public CommandFriend(final Main plugin) {
        super("friend", "bungee.command.friend", "friends");
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
        if (playerUuid == null || !MB.isPlayerOnline(playerUuid)) {
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
        showHelp(sender);
    }

    private void addFriend(ProxiedPlayer p, UUID userB) {
        UUID userA = p.getUniqueId();
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

    private void listFriends(ProxiedPlayer p) {
        boolean first;
        MyBuilder friendList;

        p.sendMessage(fromLegacyText(ChatColor.GREEN + "Votre liste d'amis"));
        Collection<UUID> friendsMutual = FM.getFriends(p.getUniqueId(), MUTUAL);
        Collection<UUID> friendsPending = FM.getFriends(p.getUniqueId(), PENDING);
        Collection<UUID> friendsInvitations = FM.getFriends(p.getUniqueId(), PENDING_OTHER);
        if (!friendsMutual.isEmpty()) {
            friendList = new MyBuilder(ChatColor.GREEN + "Amis ");
            first = true;
            for (UUID _u : friendsMutual) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.WHITE + ", ");
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

        if (!friendsPending.isEmpty()) {
            friendList = new MyBuilder(ChatColor.GREEN + "Demandes en attente: ");
            first = true;
            for (UUID _u : friendsPending) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.WHITE + ", ");
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
            friendList = new MyBuilder(ChatColor.GREEN + "Invitations en attente: ");
            first = true;
            for (UUID _u : friendsInvitations) {
                String name = Main.getMB().getNameFromUuid(_u);
                if (first)
                    first = false;
                else
                    friendList.append(ChatColor.WHITE + ", ");
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

    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(fromLegacyText(ChatColor.RED + "-- Liste d'amis --"));
        sender.sendMessage(fromLegacyText(ChatColor.RED + "Commandes: "));
        sender.sendMessage(fromLegacyText(ChatColor.RED + "/friend list"));
        sender.sendMessage(fromLegacyText(ChatColor.RED + "/friend add " + ChatColor.ITALIC + "pseudo"));
        sender.sendMessage(fromLegacyText(ChatColor.RED + "/friend del " + ChatColor.ITALIC + "pseudo"));

    }
}
