package fr.PunKeel.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Timestamp;

@Table("bungee_tokens")
public class BungeeToken extends Model {
    public String getIdentifier() {
        return getToken();
    }

    public String getToken() {
        return getString("token");
    }

    public void setToken(String token) {
        setString("token", token);
    }

    public Integer getUtilisations() {
        return getInteger("usages");
    }

    public void setUtilisations(Integer usages) {
        if (usages == null)
            return;
        setInteger("usages", usages);
    }

    public Long getLifetime() {
        Timestamp t = getTimestamp("valid_until");
        if (t == null)
            return null;
        return t.getTime();
    }

    public void setLifetime(Long lifetime) {
        if (lifetime == null)
            return;
        setTimestamp("valid_until", new Timestamp(System.currentTimeMillis() + lifetime));
    }

    public String getCreatedBy() {
        return getString("created_by");
    }

    public void setCreatedBy(String createdBy) {
        setString("created_by", createdBy);
    }

    public boolean isBroken(long times_used) {
        int usages = getInteger("usages");
        return usages != -1 && usages <= times_used;
    }

    public boolean hasExpired() {
        Timestamp lifetime = getTimestamp("valid_until");
        return lifetime.before(new Timestamp(System.currentTimeMillis()));
    }

    public String getAction() {
        return getString("action");
    }

    public void setAction(String action) {
        setString("action", action);
    }


}
