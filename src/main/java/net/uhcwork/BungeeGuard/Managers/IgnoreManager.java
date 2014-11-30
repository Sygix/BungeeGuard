package net.uhcwork.BungeeGuard.Managers;

import lombok.AccessLevel;
import lombok.Getter;
import net.uhcwork.BungeeGuard.Main;

import java.util.*;

/**
 * Part of net.uhcwork.BungeeGuard.Ignore (bungeeguard)
 * Date: 20/09/2014
 * Time: 23:15
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class IgnoreManager {
    private final Main plugin;
    @Getter(AccessLevel.PUBLIC)
    // null value means "ignore all"
    private final Map<UUID, List<UUID>> ignoreList = new HashMap<>();

    public IgnoreManager(Main plugin) {
        this.plugin = plugin;
    }

    List<UUID> getIgnoreList(UUID joueur) {
        List<UUID> liste;
        if (!ignoreList.containsKey(joueur)) {
            liste = new ArrayList<>();
            ignoreList.put(joueur, liste);
            return liste;
        }
        return ignoreList.get(joueur);
    }

    public void unIgnore(UUID joueur, UUID toIgnore) {
        getIgnoreList(joueur).remove(toIgnore);
    }

    public void ignore(UUID joueur, UUID toIgnore) {
        getIgnoreList(joueur).add(toIgnore);
    }

    public boolean playerIgnores(UUID uniqueId, UUID toIgnore) {
        return getIgnoreList(uniqueId).contains(toIgnore);
    }

    public void setIgnoreList(Map<UUID, List<UUID>> ignoresList) {
        ignoreList.clear();
        ignoreList.putAll(ignoresList);
    }
}
