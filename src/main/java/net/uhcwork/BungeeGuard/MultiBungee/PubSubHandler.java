package net.uhcwork.BungeeGuard.MultiBungee;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee (BungeeGuard)
 * Date: 22/10/2014
 * Time: 16:37
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface PubSubHandler {
    String value();
}
