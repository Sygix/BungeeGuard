package fr.PunKeel.BungeeGuard.Models;

import fr.PunKeel.BungeeGuard.Utils.UUIDUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table("bungeelitycs")
public class BungeeLitycs extends Model {
    public void setUUID(UUID u) {
        set("uuid", UUIDUtils.toBytes(u));
    }

    public void setServerID(String name) {
        setString("server_id", name);
    }

    public void setJoinedAt(Timestamp joinedAt) {
        setTimestamp("joined_at", joinedAt);
    }

    public void setLeavedAt(Timestamp leavedAt) {
        setTimestamp("leaved_at", leavedAt);
    }

    public void join(ProxiedPlayer p, ServerInfo target) {
        setUUID(p.getUniqueId());
        setServerID(target.getName());
        setJoinedAt(new Timestamp(System.currentTimeMillis()));
    }

    public void leave(ProxiedPlayer p) {
        setLeavedAt(new Timestamp(System.currentTimeMillis()));
    }
}
