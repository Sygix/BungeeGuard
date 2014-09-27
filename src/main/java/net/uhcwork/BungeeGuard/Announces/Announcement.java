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
public class Announcement {
    private String text = "";
    private List<String> servers = new ArrayList<>();

    public Announcement(BungeeAnnouncements ba) {
        setText(ba.getText());
        setServers(ba.getServer());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(String servers) {
        Collections.addAll(this.servers, servers.split(":"));
    }
}
