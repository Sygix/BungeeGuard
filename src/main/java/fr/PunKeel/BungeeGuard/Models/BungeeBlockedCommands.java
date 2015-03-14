package fr.PunKeel.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_blocked_commands")
public class BungeeBlockedCommands extends Model {
    public String getCommand() {
        return getString("command");
    }
}
