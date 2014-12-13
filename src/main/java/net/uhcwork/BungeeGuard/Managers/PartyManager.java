package net.uhcwork.BungeeGuard.Managers;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.io.Serializable;
import java.util.*;

public class PartyManager {
    private Map<String, Party> parties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
                    joueurs.remove();
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

    /**
     * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
     * Date: 09/09/2014
     * Time: 20:10
     * May be open-source & be sold (by mguerreiro, of course !)
     */
    public static class Party implements Serializable {
        private static final long serialVersionUID = 7009960713031110863L;
        final List<UUID> chatMembers = new ArrayList<>();
        @Getter
        final List<UUID> members = new ArrayList<>();
        final List<UUID> invitations = new ArrayList<>();
        @Getter
        @Setter
        String name = "";
        @Getter
        @Setter
        UUID owner = UUID.randomUUID();
        @Getter
        @Setter
        boolean publique = false;

        public Party(String nom, UUID owner) {
            this.name = nom;
            this.owner = owner;
            addMember(owner);
        }

        public void addMember(UUID player) {
            members.add(player);
            invitations.remove(player);
        }

        public boolean isMember(UUID joueur) {
            return members.contains(joueur);
        }

        public boolean canJoin(ProxiedPlayer sender) {
            return canJoin(sender.getUniqueId());
        }

        private boolean canJoin(UUID uniqueId) {
            if (getSize() < 20) {
                if (isPublique() || isInvited(uniqueId)) {
                    invitations.remove(uniqueId);
                    return true;
                }
            }
            return false;
        }

        private boolean isInvited(UUID uniqueId) {
            return invitations.contains(uniqueId);
        }

        public boolean isOwner(ProxiedPlayer sender) {
            return isOwner(sender.getUniqueId());
        }

        private boolean isOwner(UUID uniqueId) {
            return owner.equals(uniqueId);
        }


        public boolean isPartyChat(ProxiedPlayer sender) {
            return isPartyChat(sender.getUniqueId());
        }

        private boolean isPartyChat(UUID uniqueId) {
            return chatMembers.contains(uniqueId);
        }

        public void setPartyChat(UUID uuid, boolean isPartyChat) {
            if (isPartyChat)
                chatMembers.add(uuid);
            else
                chatMembers.remove(uuid);
        }

        public void addInvitation(UUID u) {
            invitations.add(u);
        }

        public void removeMember(UUID u) {
            members.remove(u);
            chatMembers.remove(u);
            if (owner.equals(u)) {
                owner = Iterables.getFirst(members, null);
            }
        }

        public int getSize() {
            return members.size();
        }

        public TextComponent getDisplay() {
            String membres = "" + ChatColor.RESET + ChatColor.BOLD + Main.getMB().getNameFromUuid(getOwner()) + ChatColor.RESET;
            for (UUID m : getMembers()) {
                if (getOwner().equals(m))
                    continue;
                membres += "\n" + Main.getMB().getNameFromUuid(m);
            }
            TextComponent result = new TextComponent(getName());
            result.setColor(ChatColor.BLUE);
            result.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(membres).create()));
            return result;
        }
    }
}
