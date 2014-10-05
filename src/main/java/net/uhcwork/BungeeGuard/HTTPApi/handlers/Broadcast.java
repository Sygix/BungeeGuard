package net.uhcwork.BungeeGuard.HTTPApi.handlers;

import com.google.common.collect.ImmutableMap;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.HttpServerApiRequest;
import net.uhcwork.BungeeGuard.HTTPApi.httpserver.RequestHandler;
import net.uhcwork.BungeeGuard.Main;

/**
 * Part of net.uhcwork.BungeeGuard.Api.handlers (BungeeGuard)
 * Date: 05/10/2014
 * Time: 18:32
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class Broadcast implements RequestHandler {
    @Override
    public Object handle(HttpServerApiRequest request) {
        if (request.getParams().containsKey("message")) {
            Main.getMB().broadcastServers("*", request.getParams().get("message").get(0));
            return ImmutableMap.of("status", "OK");
        } else {
            return "No message specified.";
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}

