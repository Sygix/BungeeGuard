package net.uhcwork.BungeeGuard.Announces;

import net.uhcwork.BungeeGuard.Models.BungeeAnnouncements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Part of net.uhcwork.BungeeGuard.Announces (bungeeguard)
 * Date: 27/09/2014
 * Time: 16:44
 * May be open-source & be sold (by mguerreiro, of course !)
 */
class Announcement {
    private final List<String> servers = new ArrayList<>();
    private String text = "";

    public Announcement(BungeeAnnouncements ba) {
        setText(ba.getText());
        setServers(ba.getServer());
    }

    public String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    public List<String> getServers() {
        return servers;
    }

    void setServers(String servers) {
        Collections.addAll(this.servers, servers.split(":"));
    }
}
