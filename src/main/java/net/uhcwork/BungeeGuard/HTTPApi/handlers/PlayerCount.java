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
package net.uhcwork.BungeeGuard.HTTPApi.handlers;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ProxyServer;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.HttpServerApiRequest;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.RequestHandler;
import net.uhcwork.BungeeGuard.Main;

public class PlayerCount implements RequestHandler {
    @Override
    public Object handle(HttpServerApiRequest request) {
        return ImmutableMap.of("current_players", Main.getMB().getPlayerCount(), "max_players",
                ProxyServer.getInstance().getConfig().getListeners().iterator().next().getMaxPlayers());
        // Max Players: sale.
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
