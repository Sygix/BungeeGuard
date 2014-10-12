package net.uhcwork.BungeeGuard.Lobbies;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.uhcwork.BungeeGuard.Main;

import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.Lobbies (BungeeGuard)
 * Date: 12/10/2014
 * Time: 13:55
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ServerManager {
    @Getter
    Cache<String, ServerPing> serversCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.SECONDS).build();
    Main plugin;

    public ServerManager(Main main) {
        this.plugin = main;
    }

    public void ping(final String serverName, final Callback<ServerPing> pingBack) {
        if (serverName == null || serverName.isEmpty())
            return;
        final ServerPing SP = getServersCache().getIfPresent(serverName);

        if (SP == null) {
            Callback<ServerPing> pingCallback = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    if (serverPing == null)
                        return;
                    getServersCache().put(serverName, serverPing);
                    pingBack.done(serverPing, throwable);
                }
            };
            ProxyServer.getInstance().getServerInfo(serverName).ping(pingCallback);
        } else {
            pingBack.done(SP, null);
        }
    }
}
