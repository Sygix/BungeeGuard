package fr.greenns.BungeeGuard.Party;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 18:43
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyManager {
    private Map<String, Party> parties = new HashMap<>();

    public Map<String, Party> getParties() {
        return parties;
    }

    public void setParties(Map<String, Party> parties) {
        this.parties = parties;
    }

    public Party getParty(String nom) {
        return parties.containsKey(nom) ? parties.get(nom) : null;
    }

    public boolean inParty(UUID joueur) {
        for (Party p : parties.values()) {
            if (p.isMember(joueur)) {
                return true;
            }
        }
        return false;
    }

    public void createParty(String nom, ProxiedPlayer owner) {
        createParty(nom, owner.getUniqueId());
    }

    public void createParty(String nom, UUID owner) {
        Party party = new Party(nom, owner);
        parties.put(nom, party);
    }

    public Party getPartyByPlayer(ProxiedPlayer player) {
        return getPartyByPlayer(player.getUniqueId());
    }

    public Party getPartyByPlayer(UUID player) {
        for (Party p : parties.values()) {
            if (p.isMember(player)) {
                return p;
            }
        }
        return null;
    }

    public boolean inParty(ProxiedPlayer sender) {
        return inParty(sender.getUniqueId());
    }

    public void removeParty(Party p) {
        removeParty(p.getName());
    }

    private void removeParty(String name) {
        parties.remove(name);
    }
}
