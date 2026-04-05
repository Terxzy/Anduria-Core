package cz.anduria.core.managers;

import cz.anduria.core.AnduriaCore;
import java.util.*;

public class CooldownManager {
    /** command -> uuid -> expiry ms */
    private final Map<String, Map<UUID, Long>> data = new HashMap<>();
    public CooldownManager(AnduriaCore plugin) {}

    public boolean isOnCooldown(UUID u, String cmd) {
        Map<UUID, Long> m = data.get(cmd);
        if (m == null) return false;
        Long exp = m.get(u);
        return exp != null && System.currentTimeMillis() < exp;
    }

    public long getRemainingSeconds(UUID u, String cmd) {
        Map<UUID, Long> m = data.get(cmd);
        if (m == null) return 0;
        Long exp = m.get(u);
        return exp == null ? 0 : Math.max(0, (exp - System.currentTimeMillis()) / 1000L);
    }

    public void setCooldown(UUID u, String cmd, int seconds) {
        data.computeIfAbsent(cmd, k -> new HashMap<>())
            .put(u, System.currentTimeMillis() + seconds * 1000L);
    }
}