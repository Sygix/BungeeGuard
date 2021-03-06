package fr.PunKeel.BungeeGuard.Permissions;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table("uhgestion_users")
@BelongsTo(parent = GroupModel.class, foreignKeyName = "group_id")
public class UserModel extends Model {
    public UUID getUUID() {
        return UUID.fromString(getString("uuid"));
    }

    public void setUUID(UUID uuid) {
        setString("uuid", "" + uuid);
    }

    public String getGroup() {
        return getString("group_id");
    }

    public void setGroup(String group) {
        setString("group_id", group);
    }

    public GroupModel getGroupe() {
        return parent(GroupModel.class);
    }

    public Timestamp getUntil() {
        return getTimestamp("until");
    }

    public void setUntil(Timestamp until) {
        setTimestamp("until", until);
    }

    public boolean isValid() {
        return getUntil() == null || getUntil().after(new Timestamp(System.currentTimeMillis()));
    }
}
