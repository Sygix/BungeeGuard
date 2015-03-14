package fr.PunKeel.BungeeGuard.Utils;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MyReconnectHandler implements ReconnectHandler {
    @Override
    public ServerInfo getServer(ProxiedPlayer p) {
        PendingConnection con = p.getPendingConnection();
        if (con.getVirtualHost() == null)
            return null;

        String forced = Main.plugin.getConfig().getForcedHosts().get(con.getVirtualHost().getHostString());

        if (forced == null)
            forced = "hub";
        return ProxyServer.getInstance().getServerInfo(forced);
    }

    @Override
    public void setServer(ProxiedPlayer proxiedPlayer) {

    }

    @Override
    public void save() {

    }

    @Override
    public void close() {

    }
}
