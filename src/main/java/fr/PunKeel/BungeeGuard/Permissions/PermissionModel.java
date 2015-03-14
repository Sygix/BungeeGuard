package fr.PunKeel.BungeeGuard.Permissions;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

@Table("uhgestion_permissions")
@BelongsTo(parent = GroupModel.class, foreignKeyName = "group_id")

public class PermissionModel extends Model {
    public String getPermission() {
        return getString("permission");
    }
}
