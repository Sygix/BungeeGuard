package net.uhcwork.BungeeGuard.Permissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 01:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Data
@EqualsAndHashCode
@ToString
public class User {
    Set<UserModel> groupes = new HashSet<>();

    public User(List<UserModel> um) {
        if (um != null)
            Collections.addAll(groupes, um.toArray(new UserModel[um.size()]));
    }

    public Set<String> getGroups() {
        Set<String> _groupes = new HashSet<>();
        for (UserModel _um : groupes) {
            if (_um.isValid())
                _groupes.add(_um.getGroup());
        }
        return _groupes;
    }
}