package net.uhcwork.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import lombok.Getter;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Permissions.Group;
import net.uhcwork.BungeeGuard.Permissions.GroupModel;
import net.uhcwork.BungeeGuard.Permissions.PermissionModel;
import net.uhcwork.BungeeGuard.Permissions.UserModel;
import net.uhcwork.BungeeGuard.Persistence.DeleteRunner;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import org.javalite.activejdbc.LazyList;

import java.sql.Timestamp;
import java.util.*;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 24:41
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PermissionManager {
    private static final Ordering<Group> groupOrderer = new Ordering<Group>() {
        public int compare(Group left, Group right) {
            return Ints.compare(left.getWeight(), right.getWeight());
        }
    };
    @Getter
    final Map<String, Group> groups = new HashMap<>();
    final Multimap<UUID, UserModel> user_groups = HashMultimap.create();
    private final Main plugin;

    public PermissionManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadGroups() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                Map<String, Group> _groupes = new HashMap<>();
                LazyList<GroupModel> x = GroupModel.findAll().include(PermissionModel.class);
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

    public void loadUsers() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                List<UserModel> um = UserModel.findAll().load();
                Multimap<UUID, UserModel> tempUserGroups = keyToValuesMultimap(um, new Function<UserModel, UUID>() {
                    @Override
                    public UUID apply(UserModel userModel) {
                        return userModel.getUUID();
                    }
                }, new Function<UserModel, UserModel>() {
                    @Override
                    public UserModel apply(UserModel userModel) {
                        return userModel;
                    }
                });
                user_groups.clear();
                user_groups.putAll(tempUserGroups);

                System.out.println("Loaded " + um.size() + " user(s)");
            }

            public <E, K, V> ImmutableListMultimap<K, V> keyToValuesMultimap(Iterable<E> elements, Function<E, K> keyFunction, Function<E, V> valueFunction) {
                ImmutableListMultimap<K, E> keysToElements = Multimaps.index(elements, keyFunction);
                ListMultimap<K, V> keysToValuesLazy = Multimaps.transformValues(keysToElements, valueFunction);
                return ImmutableListMultimap.copyOf(keysToValuesLazy);

            }
        });
    }

    public Group getGroup(String groupName) {
        return groups.containsKey(groupName) ? groups.get(groupName) : null;
    }

    List<Group> getGroupsWithInherits(Set<String> groups) {
        Set<Group> _groupes = new HashSet<>();
        _groupes.add(getGroup("default"));
        Group g;
        for (String _g : groups) {
            g = getGroup(_g);

            if (g == null)
                continue;

            if (!_groupes.contains(g))
                _groupes.add(g);

            while (g.getInherit() != null) {
                g = getGroup(g.getInherit());
                if (!_groupes.contains(g))
                    _groupes.add(g);
            }
        }
        return groupOrderer.reverse().sortedCopy(_groupes);
    }

    public void invalidateUser(final UUID uuid) {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                user_groups.removeAll(uuid);
                List<UserModel> um = UserModel.find("uuid=?", uuid.toString());
                user_groups.putAll(uuid, um);
            }
        });
    }

    public Group getMainGroup(UUID user) {
        return getGroups(user).iterator().next();
    }

    public Group getMainGroup(String user) {
        return getMainGroup(Main.getMB().getUuidFromName(user));
    }

    // New code
    public Collection<Group> getGroups(UUID user) {
        return Collections2.transform(getGroupes(user), new Function<String, Group>() {
            @Override
            public Group apply(String groupName) {
                return getGroup(groupName);
            }
        });
    }

    public Set<String> getGroupes(UUID user) {
        Set<String> _groupes = new HashSet<>();
        UserModel _um;
        Iterator<UserModel> i = getUserModels(user).iterator();
        while (i.hasNext()) {
            _um = i.next();
            if (_um.isValid())
                _groupes.add(_um.getGroup());
            else {
                i.remove();
                plugin.executePersistenceRunnable(new DeleteRunner(_um));
            }
        }
        return _groupes;
    }

    public boolean inGroup(UUID user, String name) {
        for (String group : getGroupes(user)) {
            if (group.equals(name))
                return true;
        }
        return false;
    }

    public void addGroup(UUID user, Group group, Long duration) {
        // duration en ms
        for (UserModel _um : getUserModels(user)) {
            if (_um.getGroup().equals(group.getId()) && _um.isValid()) {
                if (duration == null) {
                    _um.setUntil(null);
                } else {
                    _um.setUntil(new Timestamp(_um.getUntil().getTime() + duration));
                }
                plugin.executePersistenceRunnable(new SaveRunner(_um));
                return;
            }
        }
        // Sinon, le groupe n'existe pas encore, et on le créé.
        UserModel _um = new UserModel();
        if (duration == null)
            _um.setUntil(null);
        else
            _um.setUntil(new Timestamp(System.currentTimeMillis() + duration));

        _um.setGroup(group.getId());
        _um.setUUID(user);
        plugin.executePersistenceRunnable(new SaveRunner(_um));
        user_groups.put(user, _um);
    }

    public void removeGroup(UUID user, Group group) {
        if (inGroup(user, group.getId())) {
            UserModel _um;
            Iterator<UserModel> i = getUserModels(user).iterator();
            while (i.hasNext()) {
                _um = i.next();
                if (_um.getGroup().equals(group.getId())) {
                    i.remove();
                    plugin.executePersistenceRunnable(new DeleteRunner(_um));
                }
            }
        }
    }

    public Collection<UserModel> getUserModels(UUID uuid) {
        return user_groups.get(uuid);
    }

    public List<Group> getGroupsWithInherits(UUID user) {
        return getGroupsWithInherits(getGroupes(user));
    }
}
