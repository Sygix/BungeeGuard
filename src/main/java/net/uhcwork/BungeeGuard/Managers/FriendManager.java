package net.uhcwork.BungeeGuard.Managers;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeFriend;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FriendManager {
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

    public Collection<UUID> getFriends(final UUID user, final STATE state) {
        return Collections2.filter(friendships.get(user), new Predicate<UUID>() {
            @Override
            public boolean apply(final UUID uuid) {
                return getFriendship(user, uuid).equals(state);
            }
        });
    }

    public enum STATE {PENDING, PENDING_OTHER, MUTUAL, NONE}
    // Pending: user A asked B and waits for a reply
    // Pending_other: user B asked A and waits for a reply
    // Mutual: users A and B are friends
    // None: nothing between A and B, not even sex.
}
