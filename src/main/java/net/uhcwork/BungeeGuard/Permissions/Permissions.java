package net.uhcwork.BungeeGuard.Permissions;

import net.uhcwork.BungeeGuard.Main;

import java.util.List;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.utils
 * Date: 30/08/2014
 * Time: 14:16
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class Permissions {
    // Works even with offline players !
    public static boolean hasPerm(String player, String permission) {
        PermissionManager PM = Main.plugin.getPermissionManager();
        UUID uuid = Main.getMB().getUuidFromName(player);
        User u = PM.getUser(uuid);
        boolean allowed;
        for (Group g : PM.getGroups(u.getGroups())) {
            if (g == null)
                continue;
            for (String perm : g.getPermissions()) {
                allowed = !perm.startsWith("-");
                if (miniglob(perm.substring(allowed ? 0 : 1), permission))
                    return allowed;
            }
        }
        return false;
    }

    public static boolean miniglob(String[] pattern, String line) {
        if (pattern.length == 0)
            return line.isEmpty();
        if (pattern.length == 1)
            return line.equals(pattern[0]);
        if (!line.startsWith(pattern[0]))
            return false;

        int idx = pattern[0].length();
        String patternTok;
        int nextIdx;
        for (int i = 1; i < pattern.length - 1; ++i) {
            patternTok = pattern[i];
            nextIdx = line.indexOf(patternTok, idx);
            if (nextIdx < 0)
                return false;
            idx = nextIdx + patternTok.length();
        }
        return line.endsWith(pattern[pattern.length - 1]);

    }

    public static boolean miniglob(String pattern, String line) {
        // miniglob : parseur de permissions, avec support léger pour les wildcard :)
        // ("a.b.c", "a.b.c") -> true
        // ("a.*", "a.b.c") -> true
        return miniglob(pattern.split("\\*+", -1), line);
    }

    public static boolean miniglob(List<String> patterns, String line) {
        if (patterns.contains(line))
            return true;
        for (String pattern : patterns) {
            if (miniglob(pattern, line)) {
                return true;
            }
        }
        return false;
    }
}
