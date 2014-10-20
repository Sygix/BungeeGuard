package net.uhcwork.BungeeGuard.Permissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.*;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 01:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Data
@EqualsAndHashCode
@ToString
public class User {
    Set<UserModel> groupes = new HashSet<>();
    UUID uuid;

    public User(UUID u, List<UserModel> um) {
        this.uuid = u;
        if (um != null)
            Collections.addAll(groupes, um.toArray(new UserModel[um.size()]));
    }

    public Set<String> getGroups() {
        Set<String> _groupes = new HashSet<>();
        for (UserModel _um : groupes) {
            if (_um.isValid())
                _groupes.add(_um.getGroup());
            else
                groupes.remove(_um);
        }
        return _groupes;
    }

    public boolean inGroup(String name) {
        for (UserModel _um : groupes) {
            if (_um.getGroup().equals(name))
                return true;
        }
        return false;
    }

    public void addGroup(Group group, Long duration) {
        // duration en ms
        if (inGroup(group.getId())) {
            System.out.println("ingrp : " + group);
            for (UserModel _um : groupes) {
                if (_um.getGroup().equals(group.getId())) {
                    _um.setUntil(new Timestamp(_um.getUntil().getTime() + duration));
                    _um.saveIt();
                }
            }
        } else {
            System.out.println("notingrp : " + group + ":" + new Timestamp(System.currentTimeMillis() + duration));
            UserModel _um = new UserModel();
            _um.setUntil(new Timestamp(System.currentTimeMillis() + duration));
            _um.setGroup(group.getId());
            _um.setUUID(uuid);
            _um.saveIt();
            groupes.add(_um);
        }
    }

    public void removeGroup(Group group) {
        System.out.println("rem : " + group);
        if (inGroup(group.getId())) {
            for (UserModel _um : groupes) {
                if (_um.getGroup().equals(group.getId())) {
                    groupes.remove(_um);
                    _um.delete();
                }
            }
        }
    }
}