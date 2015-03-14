package fr.PunKeel.BungeeGuard.Permissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
@ToString
public class Group {
    final String id, name, prefix, suffix, color, inherit, chatPrefix, chatSuffix;
    final int weight;
    final Set<String> permissions = new HashSet<>();

    public Group(GroupModel model) {
        id = model.getIdentifier();
        name = model.getName();
        prefix = model.getPrefix();
        suffix = model.getSuffix();
        color = model.getColor();
        inherit = model.getInherit();
        weight = model.getWeight();
        chatPrefix = model.getChatPrefix();
        chatSuffix = model.getChatSuffix();
        permissions.clear();
        List<PermissionModel> _permissions = model.get(PermissionModel.class, null);
        for (PermissionModel PM : _permissions) {
            permissions.add(PM.getPermission());
        }
    }

    public boolean hasPermission(String perm) {
        return Permissions.miniglob(permissions, perm);
    }
}
