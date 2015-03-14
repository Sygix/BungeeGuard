package fr.PunKeel.BungeeGuard.Announces;

import fr.PunKeel.BungeeGuard.Models.BungeeAnnouncements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
