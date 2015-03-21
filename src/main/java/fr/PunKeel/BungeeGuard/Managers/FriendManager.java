package fr.PunKeel.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeFriend;
import fr.PunKeel.BungeeGuard.Persistence.SaveRunner;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class FriendManager {
    @Getter(AccessLevel.PUBLIC)
    private static final String TAG = "[" + ChatColor.RED + "❤" + ChatColor.WHITE + "] ";
    private static final String FRIENDS_LIST = TAG + ChatColor.AQUA + "Amis en ligne " + ChatColor.WHITE +
            "[" + ChatColor.GOLD + "%d" + ChatColor.WHITE + "/" + ChatColor.AQUA + "%d" + ChatColor.WHITE + "] " +
            ChatColor.AQUA + ": " + ChatColor.GOLD + "%s";
    private static final String PENDING_COUNT = TAG + ChatColor.AQUA + "Vous avez " + ChatColor.GOLD + "%d" + ChatColor.AQUA + " nouvelle%s invitation%s. Faites " + ChatColor.ITALIC + "/friends list" + ChatColor.AQUA + " pour les afficher";
    private static final Joiner joiner = Joiner.on(ChatColor.WHITE + ", " + ChatColor.GOLD);
    final Multimap<UUID, UUID> friendships = HashMultimap.create();
    Main plugin;

    public FriendManager(Main main) {
        this.plugin = main;
    }

    public void loadFriends() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                List<BungeeFriend> friends = BungeeFriend.findAll();
                Multimap<UUID, UUID> _friendships = HashMultimap.create();
                for (BungeeFriend bf : friends) {
                    _friendships.put(bf.getSender(), bf.getReceiver());
                }
                friendships.clear();
                friendships.putAll(_friendships);
                Calendar cal = new GregorianCalendar();
                cal.add(Calendar.DAY_OF_MONTH, -7);
                Date aWeekAgo = cal.getTime();

                for (BungeeFriend bf : friends) {
                    if (!getFriendship(bf.getSender(), bf.getReceiver()).equals(STATE.MUTUAL) && aWeekAgo.after(bf.getCreationDate())) {
                        bf.delete();
                    }
                }
            }
        });
    }

    public STATE getFriendship(final UUID userA, final UUID userB) {
        boolean AB = askedFriend(userA, userB);
        boolean BA = askedFriend(userB, userA);
        if (AB)
            if (BA)
                return STATE.MUTUAL;
            else
                return STATE.PENDING;
        if (BA)
            return STATE.PENDING_OTHER;
        else
            return STATE.NONE;
    }

    public boolean askedFriend(final UUID userA, final UUID userB) {
        return friendships.containsEntry(userA, userB);
    }

    public void removeFriend(final BungeeFriend bf, final boolean saveToBdd) {
        friendships.remove(bf.getSender(), bf.getReceiver());
        if (!saveToBdd)
            return;
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                bf.delete();
            }
        });
    }

    public void removeFriend(final UUID userA, final UUID userB, final boolean saveToBdd) {
        friendships.remove(userA, userB);
        if (!saveToBdd)
            return;
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                BungeeFriend bf = BungeeFriend.find(userA, userB);
                if (bf == null)
                    return;
                bf.delete();
            }
        });
    }

    public void addFriend(final UUID userA, final UUID userB, final boolean saveToBdd) {
        if (friendships.containsEntry(userA, userB))
            return;
        friendships.put(userA, userB);
        if (!saveToBdd)
            return;
        BungeeFriend bf = new BungeeFriend();
        bf.setSender(userA);
        bf.setReceiver(userB);
        plugin.executePersistenceRunnable(new SaveRunner(bf));
    }

    public Collection<UUID> getFriends(final UUID user, STATE state) {
        Multimap<UUID, UUID> collection = friendships;
        if (state.equals(STATE.PENDING_OTHER)) {
            collection = Multimaps.invertFrom(collection, ArrayListMultimap.<UUID, UUID>create());
        }
        final STATE finalstate = state;
        return Collections2.filter(collection.get(user), new Predicate<UUID>() {
            @Override
            public boolean apply(final UUID uuid) {
                return getFriendship(user, uuid).equals(finalstate);
            }
        });
    }

    public Collection<UUID> getFriends(final UUID user, STATE... state) {
        Collection<UUID> friends = new HashSet<>();
        for (STATE _state : state) {
            friends.addAll(getFriends(user, _state));
        }
        return friends;
    }

    public void sendJoinMessage(ProxiedPlayer p) {
        UUID user = p.getUniqueId();
        Collection<UUID> friends = getFriends(user, STATE.MUTUAL);
        // Collection<UUID> friends_pending = getFriends(user, STATE.PENDING);
        Collection<UUID> friends_other_pending = getFriends(user, STATE.PENDING_OTHER);
        Collection<String> online_friends = Collections2.transform(Collections2.filter(friends, new Predicate<UUID>() {
            @Override
            public boolean apply(UUID uuid) {
                return Main.getMB().isPlayerOnline(uuid);
            }
        }), new Function<UUID, String>() {
            @Override
            public String apply(UUID uuid) {
                return Main.getMB().getNameFromUuid(uuid);
            }
        });
        if (friends.size() + friends_other_pending.size() != 0) {
            p.sendMessage(Main.SEPARATOR);

            if (online_friends.size() != 0)
                p.sendMessage(TextComponent.fromLegacyText(String.format(FRIENDS_LIST, online_friends.size(), friends.size(), joiner.join(online_friends))));
            else if (friends.size() != 0)
                p.sendMessage(TextComponent.fromLegacyText(String.format(FRIENDS_LIST, online_friends.size(), friends.size(), ChatColor.YELLOW + "Aucun :(")));
            if (friends_other_pending.size() != 0) {
                String _s = s(friends_other_pending.size());
                p.sendMessage(TextComponent.fromLegacyText(String.format(PENDING_COUNT, friends_other_pending.size(), _s, _s)));
            }

            p.sendMessage(Main.SEPARATOR);
        }
    }

    private String s(int size) {
        return (size > 1) ? "s" : "";
    }

    public enum STATE {PENDING, PENDING_OTHER, MUTUAL, NONE}
    // Pending: user A asked B and waits for a reply
    // Pending_other: user B asked A and waits for a reply
    // Mutual: users A and B are friends
    // None: nothing between A and B, not even sex.
}
