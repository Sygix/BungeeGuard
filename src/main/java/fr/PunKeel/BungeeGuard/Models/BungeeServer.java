package fr.PunKeel.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_servers")
public class BungeeServer extends Model {
    public String getName() {
        return getString("name");
    }

    public String getAddress() {
        return getString("address");
    }

    public String getPrettyName() {
        return ChatColor.translateAlternateColorCodes('&', getString("pretty_name"));
    }

    public String getShortName() {
        return ChatColor.translateAlternateColorCodes('&', getString("short_name"));
    }

    public boolean isRestricted() {
        return getBoolean("restricted");
    }

    public void setRestricted(boolean isRestricted) {
        setBoolean("restricted", isRestricted);
    }

}
