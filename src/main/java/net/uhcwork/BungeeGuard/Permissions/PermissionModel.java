package net.uhcwork.BungeeGuard.Permissions;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 24:48
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("uhgestion_permissions")
@BelongsTo(parent = GroupModel.class, foreignKeyName = "group_id")

public class PermissionModel extends Model {
    public String getPermission() {
        return getString("permission");
    }
}
