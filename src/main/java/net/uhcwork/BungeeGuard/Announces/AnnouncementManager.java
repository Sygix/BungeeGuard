package net.uhcwork.BungeeGuard.Announces;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeAnnouncements;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of net.uhcwork.BungeeGuard.Announces (bungeeguard)
 * Date: 27/09/2014
 * Time: 16:42
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class AnnouncementManager {
    Main plugin;
    private List<Announcement> announcements = new ArrayList<>();

    public AnnouncementManager(Main main) {
        this.plugin = main;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<BungeeAnnouncements> _announcements) {
        announcements.clear();
        for (BungeeAnnouncements BA : _announcements) {
            announcements.add(new Announcement(BA));
        }
    }
}
