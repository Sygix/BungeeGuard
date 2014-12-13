package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_broadcasts")
public class BungeeAnnouncements extends Model {
    public String getText() {
        return getString("message");
    }

    public String getServer() {
        // Liste de serveurs séparée par ":"
        return getString("servers");
    }
}
