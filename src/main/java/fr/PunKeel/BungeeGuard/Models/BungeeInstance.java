package fr.PunKeel.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_instances")
public class BungeeInstance extends Model {
    public String getBindAddress() {
        return getString("bind_address");
    }
}
