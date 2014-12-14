package net.uhcwork.BungeeGuard.Announces;

import lombok.Getter;
import lombok.Setter;
import net.uhcwork.BungeeGuard.Models.BungeeAnnouncements;

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
