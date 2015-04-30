package fr.PunKeel.BungeeGuard.Managers;

import com.google.gson.annotations.SerializedName;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.Serializable;
import java.util.*;

public class PartyManager {
    public static final String TAG = ChatColor.WHITE + "[" + ChatColor.RED + "Party" + ChatColor.WHITE + "] ";
    public static final String CHAT_TAG = ChatColor.WHITE + "[" + ChatColor.RED + "Party Chat" + ChatColor.WHITE + "] ";

    private Map<UUID, Party> parties = new HashMap<>();

    public Map<UUID, Party> getParties() {
        clean();
        return parties;
    }

    public void setParties(Map<UUID, Party> parties) {
        this.parties = parties;
        clean();
    }

    public void clean() {
        MultiBungee MB = Main.getMB();
        Iterator<UUID> joueurs;
        UUID u;
        for (UUID owner : parties.keySet()) {
            Party p = parties.get(owner);
            joueurs = p.getMembers().iterator();
            while (joueurs.hasNext()) {
                u = joueurs.next();
                if (!MB.isPlayerOnline(u)) {
                    joueurs.remove();
                }
            }
            if (p.getSize() == 0) {
                Main.getMB().disbandParty(p.getOwner());
            }
        }
    }

    public Party getParty(UUID owner) {
        return parties.containsKey(owner) ? parties.get(owner) : null;
    }

    public boolean inParty(UUID joueur) {
        if (Main.getRandom().nextInt(4) == 2)
            clean();
        for (Party p : parties.values()) {
            if (p.isMember(joueur)) {
                return true;
            }
        }
        return false;
    }

    public Party createParty(UUID owner) {
        Party party = new Party(owner);
        parties.put(owner, party);
        return party;
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
        removeParty(p.getOwner());
    }

    private void removeParty(UUID owner) {
        parties.remove(owner);
    }

    public void changePartyOwner(UUID partyOwner, UUID u) {
        Party p = getParty(partyOwner);
        p.setOwner(u);
        parties.remove(partyOwner);
        parties.put(u, p);
    }

    public static class Party implements Serializable {
        private static final long serialVersionUID = 7009960713031110863L;
        @SerializedName("chatMembers")
        final List<UUID> chatMembers = new ArrayList<>();
        @Getter
        @SerializedName("members")
        final List<UUID> members = new ArrayList<>();
        @SerializedName("invitations")
        final List<UUID> invitations = new ArrayList<>();
        @Getter
        @Setter
        @SerializedName("owner")
        UUID owner;
        @Getter
        @Setter
        @SerializedName("publique")
        boolean publique = false;

        public Party(UUID owner) {
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
                if (isInvited(uniqueId)) {
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

        public boolean isOwner(UUID uniqueId) {
            return owner.equals(uniqueId);
        }

        public void addInvitation(UUID u) {
            if (u == null)
                return;
            invitations.add(u);
        }

        public void removeMember(UUID u) {
            if (u == null)
                return;
            members.remove(u);
            chatMembers.remove(u);
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

        public String getName() {
            return Main.getMB().getNameFromUuid(getOwner());
        }
    }
}
