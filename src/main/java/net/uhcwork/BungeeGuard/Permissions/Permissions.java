package net.uhcwork.BungeeGuard.Permissions;

import com.google.common.base.Splitter;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PermissionManager;

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
        UUID uuid = Main.getMB().getUuidFromName(player);
        return hasPerm(uuid, permission);
    }

    public static boolean hasPerm(UUID uuid, String permission) {
        PermissionManager PM = Main.plugin.getPermissionManager();
        User u = PM.getUser(uuid);
        boolean allowed;
        for (Group g : PM.getGroups(u)) {
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

    private static boolean do_miniglob(List<String> pattern, String line) {
        if (pattern.size() == 0)
            return line.isEmpty();
        if (pattern.size() == 1)
            return line.equals(pattern.get(0));
        if (!line.startsWith(pattern.get(0)))
            return false;

        int idx = pattern.get(0).length();
        String patternTok;
        int nextIdx;
        for (int i = 1; i < pattern.size() - 1; ++i) {
            patternTok = pattern.get(i);
            nextIdx = line.indexOf(patternTok, idx);
            if (nextIdx < 0)
                return false;
            idx = nextIdx + patternTok.length();
        }
        return line.endsWith(pattern.get(pattern.size() - 1));

    }

    private static boolean miniglob(String pattern, String line) {
        // miniglob : parseur de permissions, avec support léger pour les wildcard :)
        // ("a.b.c", "a.b.c") -> true
        // ("a.*", "a.b.c") -> true
        return do_miniglob(Splitter.on('*').splitToList(pattern), line);
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
