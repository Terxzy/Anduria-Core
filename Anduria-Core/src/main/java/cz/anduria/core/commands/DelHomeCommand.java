package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public DelHomeCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.homes")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        if (a.length == 0) { p.sendMessage(plugin.getMessageManager().get("home.delhome-usage")); return true; }
        boolean ok = plugin.getHomeManager().deleteHome(p.getUniqueId(), a[0]);
        p.sendMessage(plugin.getMessageManager().get(ok ? "home.deleted" : "home.not-found", "{name}", a[0]));
        return true;
    }
}