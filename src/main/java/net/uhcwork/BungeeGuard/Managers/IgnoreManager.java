package net.uhcwork.BungeeGuard.Managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import net.uhcwork.BungeeGuard.Main;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class IgnoreManager {
    private final Main plugin;
    // null = ignore *
    @Getter(AccessLevel.PUBLIC)
    Multimap<UUID, UUID> ignoreList = HashMultimap.create();

    public IgnoreManager(Main plugin) {
        this.plugin = plugin;
    }

    Collection<UUID> getIgnoreList(UUID joueur) {
        return ignoreList.get(joueur);
    }

    public void unIgnore(UUID joueur, UUID toIgnore) {
        ignoreList.remove(joueur, toIgnore);
    }

    public void ignore(UUID joueur, UUID toIgnore) {
        getIgnoreList(joueur).add(toIgnore);
    }

    public boolean playerIgnores(UUID uniqueId, UUID toIgnore) {
        return getIgnoreList(uniqueId).contains(toIgnore);
    }

    public void setIgnoreList(Map<UUID, Collection<UUID>> ignoresList) {
        ignoreList.clear();
        for (UUID ignores : ignoresList.keySet()) {
            ignoreList.putAll(ignores, ignoresList.get(ignores));
        }
    }
}
