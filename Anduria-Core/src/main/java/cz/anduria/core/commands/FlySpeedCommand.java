package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FlySpeedCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public FlySpeedCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.flyspeed")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        if (a.length == 0) { p.sendMessage(plugin.getMessageManager().get("flyspeed.usage")); return true; }
        int level;
        try { level = Integer.parseInt(a[0]); } catch (NumberFormatException e) {
            p.sendMessage(plugin.getMessageManager().get("flyspeed.invalid")); return true;
        }
        float speed = switch (level) {
            case 1 -> (float) plugin.getConfig().getDouble("flyspeed.level1", 0.1);
            case 2 -> (float) plugin.getConfig().getDouble("flyspeed.level2", 0.3);
            default -> { p.sendMessage(plugin.getMessageManager().get("flyspeed.invalid-level")); yield -1f; }
        };
        if (speed < 0) return true;
        p.setFlySpeed(Math.max(0f, Math.min(1f, speed)));
        p.sendMessage(plugin.getMessageManager().get("flyspeed.success", "{level}", String.valueOf(level)));
        return true;
    }
}