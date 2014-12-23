package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("bungee_token_uses")
public class BungeeTokenUse extends Model {
    public void setToken(String token) {
        setString("token", token);
    }

    public void setUuid(UUID uuid) {
        setString("uuid", "" + uuid);
    }
}
