package fr.greenns.BungeeGuard.Party;

import com.google.common.collect.Iterables;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 09/09/2014
 * Time: 20:10
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class Party implements Serializable {
    private static final long serialVersionUID = 7009960713031110863L;
    String name = "";
    UUID owner = UUID.randomUUID();
    List<UUID> chatMembers = new ArrayList<>();
    List<UUID> members = new ArrayList<>();
    List<UUID> invitations = new ArrayList<>();
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

    public boolean isPublique() {
        return publique;
    }

    public void setPublique(boolean publique) {
        this.publique = publique;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMember(UUID joueur) {
        return members.contains(joueur);
    }

    public boolean canJoin(ProxiedPlayer sender) {
        return canJoin(sender.getUniqueId());
    }

    private boolean canJoin(UUID uniqueId) {
        if (getSize() < 10) {
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
}
