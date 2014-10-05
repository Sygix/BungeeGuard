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
package net.uhcwork.BungeeGuard.HTTPApi.httpserver;

import java.util.HashMap;
import java.util.Map;

public class BungeeJSONRequestManager {
    private Map<String, RequestHandler> endpoints = new HashMap<>();

    public void registerEndpoint(String endpoint, RequestHandler handler) {
        String realEndpoint = endpoint;
        if (!realEndpoint.startsWith("/"))
            realEndpoint = "/" + endpoint;
        endpoints.put(realEndpoint, handler);
    }

    public RequestHandler getHandlerForEndpoint(String uri) {
        return endpoints.get(uri);
    }
}
