package net.uhcwork.BungeeGuard.Permissions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.uhcwork.BungeeGuard.Main;
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
    Main plugin;
    Cache<UUID, User> playersCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(3, TimeUnit.MINUTES).build();
    Map<String, Group> groupes = new HashMap<>();

    public PermissionManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadGroups() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                Map<String, Group> _groupes = new HashMap<>();
                //noinspection unchecked
                System.out.println(GroupModel.associations());
                LazyList<GroupModel> x = GroupModel.findAll();
                x.dump();
                for (GroupModel GM : x) {
                    Group G = new Group(GM);
                    _groupes.put(G.getId(), G);
                }
                groupes.clear();
                groupes.putAll(_groupes);
                System.out.println("Loaded " + groupes.size() + " groupe(s)");
            }
        });
    }

    public User getUser(final UUID uuid) {
        User user = playersCache.getIfPresent(uuid);
        if (user == null) {
            Future<User> x = plugin.executePersistenceRunnable(new PersistenceRunnable<User>() {
                @Override
                public User call() {
                    List<UserModel> um = UserModel.find("uuid=?", uuid.toString());
                    if (um == null || um.isEmpty())
                        return new User(null);
                    return new User(um);
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
        return groupes.containsKey(groupName) ? groupes.get(groupName) : null;
    }

    public Set<Group> getGroups(Set<String> groups) {
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
            while (g.getInherit() != null) {
                if (!_groupes.contains(g))
                    _groupes.add(getGroup(g.getInherit()));
                g = getGroup(g.getInherit());
                if (!_groupes.contains(g))
                    break;
            }
        }
        System.out.println(groups + " -->" + _groupes);
        return _groupes;
    }
}
