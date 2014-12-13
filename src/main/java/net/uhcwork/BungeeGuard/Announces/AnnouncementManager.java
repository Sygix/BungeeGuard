package net.uhcwork.BungeeGuard.Announces;

import lombok.Getter;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeAnnouncements;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {
    private final Main plugin;
    @Getter
    private final List<Announcement> announcements = new ArrayList<>();

    public AnnouncementManager(Main main) {
        this.plugin = main;
    }


    public void setAnnouncements(List<BungeeAnnouncements> _announcements) {
        announcements.clear();
        for (BungeeAnnouncements BA : _announcements) {
            announcements.add(new Announcement(BA));
        }
    }
}
