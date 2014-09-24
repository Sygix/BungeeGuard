package net.uhcwork.BungeeGuard.MultiBungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee (bungeeguard)
 * Date: 14/09/2014
 * Time: 21:09
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class RedisBungeeListener implements Listener {
    @EventHandler
    public void onPubSubmessageEvent(com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent e) {
        ProxyServer.getInstance().getPluginManager().callEvent(new PubSubMessageEvent(e.getChannel(), e.getMessage()));
    }
}
