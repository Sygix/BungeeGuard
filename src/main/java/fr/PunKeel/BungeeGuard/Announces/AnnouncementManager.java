package fr.PunKeel.BungeeGuard.Announces;

import fr.PunKeel.BungeeGuard.Models.BungeeAnnouncements;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {
    @Getter
    private final List<Announcement> announcements = new ArrayList<>();
    @Setter
    @Getter
    private int broadcastDelay = 180;

    public void setAnnouncements(List<BungeeAnnouncements> _announcements) {
        announcements.clear();
        for (BungeeAnnouncements BA : _announcements) {
            announcements.add(new Announcement(BA));
        }
    }
}
