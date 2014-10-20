package net.uhcwork.BungeeGuard.Permissions;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 24:48
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("uhgestion_users")
public class UserModel extends Model {
    public UUID getUUID() {
        return UUID.fromString(getString("uuid"));
    }

    public String getGroup() {
        return getString("group_id");
    }

    public Integer getUntil() {
        return getInteger("until");
    }

    public boolean isValid() {
        return getUntil() == null || getUntil() == -1 || getUntil() > (System.currentTimeMillis() / 1000);
    }
}
