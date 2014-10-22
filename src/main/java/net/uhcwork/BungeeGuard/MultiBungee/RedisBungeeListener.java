package net.uhcwork.BungeeGuard.MultiBungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee (bungeeguard)
 * Date: 14/09/2014
 * Time: 21:09
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class RedisBungeeListener implements Listener {
    private final Main plugin;
    Collection<Object> handlers = new ArrayList<>();

    public RedisBungeeListener(Main plugin) {
        this.plugin = plugin;
        addHandler(new BanHandler());
        addHandler(new BroadcastHandler());
        addHandler(new IgnoreHandler());
        addHandler(new KickHandler());
        addHandler(new MessageHandler());
        addHandler(new MuteHandler());
        addHandler(new ReloadConfHandler());
        addHandler(new ServerSilenceHandler());
        addHandler(new StaffChatHandler());
        addHandler(new SummonHandler());
        addHandler(new PermissionHandler());
        addHandler(new PartyHandler());
    }

    @EventHandler
    public void onPubSubmessageEvent(com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent e) {
        String channel = e.getChannel();
        if (channel.startsWith("@" + Main.getMB().getServerId() + "/")) {
            // Si channel ressemble à @serveur/commande, on retire le préfixe :]
            channel = channel.replace("@" + Main.getMB().getServerId() + "/", "@");
        }
        dispatchEvent(new PubSubMessageEvent(channel, e.getMessage()));
    }

    public void addHandler(Object handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(Object handler) {
        this.handlers.remove(handler);
    }

    public void dispatchEvent(PubSubMessageEvent event) {
        for (Object handler : handlers) {
            dispatchEventTo(event, handler);
        }
    }

    protected void dispatchEventTo(PubSubMessageEvent event, Object handler) {
        Collection<Method> methods = findMatchingEventHandlerMethods(handler, event.getChannel());
        for (Method method : methods) {
            try {
                method.setAccessible(true);

                if (method.getParameterTypes().length == 0)
                    method.invoke(handler);
                if (method.getParameterTypes().length == 1)
                    method.invoke(handler, event);
                if (method.getParameterTypes().length == 2)
                    method.invoke(handler, plugin, event);
            } catch (Exception e) {
                System.err.println("Could not invoke event handler!");
                e.printStackTrace(System.err);
            }
        }
    }

    protected Collection<Method> findMatchingEventHandlerMethods(Object handler, String eventName) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        Collection<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (canHandleEvent(method, eventName)) {
                result.add(method);
            }
        }
        return result;
    }

    protected boolean canHandleEvent(Method method, String eventName) {
        PubSubHandler handleEventAnnotation = method.getAnnotation(PubSubHandler.class);
        if (handleEventAnnotation != null) {
            String value = handleEventAnnotation.value();
            return value.equals(eventName);
        }
        return false;
    }
}
