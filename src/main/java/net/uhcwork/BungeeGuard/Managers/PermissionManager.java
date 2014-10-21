package net.uhcwork.BungeeGuard.Managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import lombok.Getter;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Permissions.*;
import net.uhcwork.BungeeGuard.Persistence.PersistenceRunnable;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import org.javalite.activejdbc.LazyList;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 24:41
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PermissionManager {
    static Ordering<Group> groupOrderer = new Ordering<Group>() {
        public int compare(Group left, Group right) {
            return Ints.compare(left.getWeight(), right.getWeight());
        }
    };
    Main plugin;
    Cache<UUID, User> playersCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build();
    @Getter
    Map<String, Group> groups = new HashMap<>();

    public PermissionManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadGroups() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                Map<String, Group> _groupes = new HashMap<>();
                LazyList<GroupModel> x = GroupModel.findAll();
                x.include(PermissionModel.class);
                for (GroupModel GM : x) {
                    Group G = new Group(GM);
                    _groupes.put(G.getId(), G);
                }
                groups.clear();
                groups.putAll(_groupes);
                System.out.println("Loaded " + groups.size() + " groupe(s)");
            }
        });
    }

    public User getUser(final UUID uuid) {
        User user = playersCache.getIfPresent(uuid);
        if (user == null) {
            Future<User> x = plugin.executePersistenceRunnable(new PersistenceRunnable<User>() {
                @Override
                public User call() {
                    List<UserModel> um = UserModel.find("uuid=? AND (`until` IS NULL OR `until`=-1 OR `until`>CURRENT_TIMESTAMP)", uuid.toString());
                    if (um == null || um.isEmpty())
                        return new User(uuid, null);
                    return new User(uuid, um);
                }
            });
            try {
                user = x.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            playersCache.put(uuid, user);
        }
        return user;
    }

    public Group getGroup(String groupName) {
        return groups.containsKey(groupName) ? groups.get(groupName) : null;
    }

    public List<Group> getGroups(Set<String> groups) {
        Set<Group> _groupes = new HashSet<>();
        _groupes.add(getGroup("default"));
        Group g;
        for (String _g : groups) {
            g = getGroup(_g);
            if (!_groupes.contains(g))
                _groupes.add(g);
        }
        for (String _g : groups) {
            g = getGroup(_g);
            if (g == null)
                continue;
            while (g.getInherit() != null) {
                if (!_groupes.contains(g))
                    _groupes.add(getGroup(g.getInherit()));
                g = getGroup(g.getInherit());
                if (!_groupes.contains(g))
                    break;
            }
        }
        return groupOrderer.sortedCopy(_groupes);
    }

    public List<Group> getGroups(User u) {
        if (u == null) {
            List<Group> groupes = new ArrayList<>();
            groupes.add(getGroup("default"));
            return groupes;
        }
        return getGroups(u.getGroups());
    }

    public void invalidateUser(UUID u) {
        playersCache.invalidate(u);
    }
}
