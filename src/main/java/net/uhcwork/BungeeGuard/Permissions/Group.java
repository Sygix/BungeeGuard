package net.uhcwork.BungeeGuard.Permissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javalite.activejdbc.LazyList;

import java.util.HashSet;
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
        LazyList<PermissionModel> _permissions = PermissionModel.find("group_id = ?", id);
        for (PermissionModel PM : _permissions) {
            permissions.add(PM.getPermission());
        }
        System.out.println("Group added : " + getId());
    }
}
