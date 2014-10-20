package net.uhcwork.BungeeGuard.Permissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 01:03
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Data
@EqualsAndHashCode
@ToString
public class Group {
    String id, name, prefix, suffix, color, inherit;
    Integer weight;
    Set<String> permissions = new HashSet<>();

    public Group(GroupModel model) {
        id = model.getIdentifier();
        name = model.getName();
        prefix = model.getPrefix();
        suffix = model.getSuffix();
        color = model.getColor();
        inherit = model.getInherit();
        weight = model.getWeight();
        permissions.clear();
        List<PermissionModel> _permissions = model.get(PermissionModel.class, null);
        for (PermissionModel PM : _permissions) {
            permissions.add(PM.getPermission());
        }
    }
}
