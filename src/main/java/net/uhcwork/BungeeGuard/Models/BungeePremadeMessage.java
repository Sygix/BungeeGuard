package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_premade_message")
public class BungeePremadeMessage extends Model {
    public String getSlug() {
        return getString("slug");
    }

    public String getText() {
        return getString("text");
    }
}
