/**
 * This file is part of BungeeJSON.
 *
 * BungeeJSON is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BungeeJSON is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BungeeJSON.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.uhcwork.BungeeGuard.HTTPApi;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.uhcwork.BungeeGuard.HTTPApi.handlers.Broadcast;
import net.uhcwork.BungeeGuard.HTTPApi.handlers.InvokeCommand;
import net.uhcwork.BungeeGuard.HTTPApi.handlers.PlayerCount;
import net.uhcwork.BungeeGuard.HTTPApi.handlers.PlayersOnline;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.AuthenticationProvider;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.BungeeJSONRequestManager;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.NettyBootstrap;
import net.uhcwork.BungeeGuard.Main;

public class HttpApi {
    protected static HttpApi self;
    private static BungeeJSONRequestManager requestManager = new BungeeJSONRequestManager();
    Main plugin;
    private NettyBootstrap nb = new NettyBootstrap();
    @Getter
    private AuthenticationProvider authenticationProvider = new AuthenticationProvider();

    public static HttpApi getSelf() {
        return self;
    }

    public static BungeeJSONRequestManager getRequestManager() {
        return requestManager;
    }

    public void onLoad(Main plugin) {
        this.plugin = plugin;
        self = this;
        requestManager.registerEndpoint("/broadcast", new Broadcast());
        requestManager.registerEndpoint("/run_command", new InvokeCommand());
        requestManager.registerEndpoint("/player_count", new PlayerCount());
        requestManager.registerEndpoint("/players_online", new PlayersOnline());
    }

    public void onEnable() {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                nb.initialize();
            }
        });
    }

    public void onDisable() {
        nb.getChannelFuture().channel().close().syncUninterruptibly();
        nb.getGroup().shutdownGracefully();
    }
}
