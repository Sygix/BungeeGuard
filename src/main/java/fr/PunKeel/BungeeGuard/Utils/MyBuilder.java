package fr.PunKeel.BungeeGuard.Utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * MyBuilder simplifies creating basic messages by allowing the use of a
 * chainable builder.
 * </p>
 * <pre>
 * new MyBuilder("Hello ").color(ChatColor.RED).
 * append("World").color(ChatColor.BLUE). append("!").bold(true).create();
 * </pre>
 * <p>
 * All methods (excluding {@link #append(String)} and {@link #create()} work on
 * the last part appended to the builder, so in the example above "Hello " would
 * be {@link net.md_5.bungee.api.ChatColor#RED} and "World" would be
 * {@link net.md_5.bungee.api.ChatColor#BLUE} but "!" would be bold and
 * {@link net.md_5.bungee.api.ChatColor#BLUE} because append copies the previous
 * part's formatting
 * </p>
 */
public class MyBuilder {

    private final List<BaseComponent> parts = new ArrayList<>();
    private TextComponent current;

    /**
     * Creates a MyBuilder with the given text as the first part.
     *
     * @param text the first text element
     */
    public MyBuilder(String text) {
        current = new TextComponent(TextComponent.fromLegacyText(text));
    }

    /**
     * Appends the text to the builder and makes it the current target for
     * formatting. The text will have all the formatting from the previous part.
     *
     * @param text the text to append
     * @return this MyBuilder for chaining
     */
    public MyBuilder append(String text) {
        parts.add(current);
        current = new TextComponent(TextComponent.fromLegacyText(text));
        return this;
    }

    /**
     * Sets the click event for the current part.
     *
     * @param clickEvent the click event
     * @return this MyBuilder for chaining
     */
    public MyBuilder event(ClickEvent clickEvent) {
        current.setClickEvent(clickEvent);
        return this;
    }

    /**
     * Sets the hover event for the current part.
     *
     * @param hoverEvent the hover event
     * @return this MyBuilder for chaining
     */
    public MyBuilder event(HoverEvent hoverEvent) {
        current.setHoverEvent(hoverEvent);
        return this;
    }

    /**
     * Returns the components needed to display the message created by this
     * builder.
     *
     * @return the created components
     */
    public BaseComponent[] create() {
        parts.add(current);
        return parts.toArray(new BaseComponent[parts.size()]);
    }
}