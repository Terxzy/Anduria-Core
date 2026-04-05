package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AFKCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public AFKCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.afk")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        plugin.getAfkManager().toggleAFK(p.getUniqueId());
        boolean afk = plugin.getAfkManager().isAFK(p.getUniqueId());
        plugin.getServer().broadcast(plugin.getMessageManager().get(
            afk ? "afk.now-afk" : "afk.no-longer-afk", "{player}", p.getName()));
        return true;
    }
}