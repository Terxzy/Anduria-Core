package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public HealCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.heal")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        int cd = plugin.getConfig().getInt("cooldowns.heal", 30);
        if (plugin.getCooldownManager().isOnCooldown(p.getUniqueId(), "heal")) {
            p.sendMessage(plugin.getMessageManager().get("cooldown", "{time}",
                String.valueOf(plugin.getCooldownManager().getRemainingSeconds(p.getUniqueId(), "heal"))));
            return true;
        }
        p.setHealth(p.getMaxHealth()); p.setFoodLevel(20); p.setSaturation(20f); p.setFireTicks(0);
        plugin.getCooldownManager().setCooldown(p.getUniqueId(), "heal", cd);
        p.sendMessage(plugin.getMessageManager().get("heal.success"));
        return true;
    }
}