package fr.PunKeel.BungeeGuard.MultiBungee;


import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

public class PubSubMessageEvent extends Event {
    @Getter
    private final String channel;
    @Getter
    private final String message;
    private final List<String> args;

    public PubSubMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
        this.args = Splitter.on(MultiBungee.SEPARATOR).splitToList(message);
    }

    public String getArg(int i) {
        return (args.size() > i) ? args.get(i) : "";
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("channel", channel)
                .add("message", message)
                .omitNullValues()
                .toString();
    }
}