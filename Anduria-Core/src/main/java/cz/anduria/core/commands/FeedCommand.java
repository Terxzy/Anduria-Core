package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public FeedCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.feed")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        int cd = plugin.getConfig().getInt("cooldowns.feed", 30);
        if (plugin.getCooldownManager().isOnCooldown(p.getUniqueId(), "feed")) {
            p.sendMessage(plugin.getMessageManager().get("cooldown", "{time}",
                String.valueOf(plugin.getCooldownManager().getRemainingSeconds(p.getUniqueId(), "feed"))));
            return true;
        }
        p.setFoodLevel(20); p.setSaturation(20f);
        plugin.getCooldownManager().setCooldown(p.getUniqueId(), "feed", cd);
        p.sendMessage(plugin.getMessageManager().get("feed.success"));
        return true;
    }
}