package fr.PunKeel.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("bungee_token_uses")
@BelongsTo(parent = BungeeToken.class, foreignKeyName = "token")

public class BungeeTokenUse extends Model {
    public void setToken(String token) {
        setString("token", token);
    }

    public void setUuid(UUID uuid) {
        setString("uuid", "" + uuid);
    }
}
