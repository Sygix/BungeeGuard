package fr.greenns.BungeeGuard.Party;

import com.google.common.collect.Iterables;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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
    Set<UUID> chatMembers = new HashSet<>();
    Set<UUID> members = new HashSet<>();
    Set<UUID> invitations = new HashSet<>();
    boolean publique = false;

    public Party(String nom, UUID owner) {
        this.name = nom;
        this.owner = owner;
        this.members.add(owner);
    }

    public boolean isPublique() {
        return publique;
    }

    public void setPublique(boolean publique) {
        this.publique = publique;
    }

    public Set<UUID> getInvitations() {
        return invitations;
    }

    public void setInvitations(Set<UUID> invitations) {
        this.invitations = invitations;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public Set<UUID> getChatMembers() {
        return chatMembers;
    }

    public void setChatMembers(Set<UUID> chatMembers) {
        this.chatMembers = chatMembers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
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
        if (members.size() < 10) {
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
        return owner == uniqueId;
    }

    public boolean togglePublique() {
        setPublique(!publique);
        return publique;
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
        if (owner == u) {
            owner = Iterables.getFirst(members, null);
        }
    }

    public int getSize() {
        return members.size();
    }
}
