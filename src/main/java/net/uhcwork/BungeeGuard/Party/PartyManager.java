package net.uhcwork.BungeeGuard.Party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.*;

/**
 * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 18:43
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyManager {
    Map<String, Party> parties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Map<String, Party> getParties() {
        clean();
        return parties;
    }

    public void setParties(Map<String, Party> parties) {
        this.parties = parties;
    }

    public void clean() {
        MultiBungee MB = Main.getMB();
        Iterator<UUID> joueurs;
        UUID u;
        Set<Party> to_remove = new HashSet<>();
        for (String partyName : parties.keySet()) {
            Party p = parties.get(partyName);
            joueurs = p.getMembers().iterator();
            while (joueurs.hasNext()) {
                u = joueurs.next();
                if (!MB.isPlayerOnline(u)) {
                    p.removeMember(u);
                }
            }
            if (p.getSize() == 0) {
                to_remove.add(p);
            }
        }
        for (Party p : to_remove) {
            removeParty(p);
            Main.getMB().disbandParty(p.getName());
        }

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
