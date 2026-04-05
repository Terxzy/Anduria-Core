package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public SetHomeCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.homes")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        String name = a.length > 0 ? a[0] : "home";
        boolean ok = plugin.getHomeManager().setHome(p.getUniqueId(), name, p.getLocation());
        if (ok) p.sendMessage(plugin.getMessageManager().get("home.set", "{name}", name));
        else    p.sendMessage(plugin.getMessageManager().get("home.max-reached",
                    "{max}", String.valueOf(plugin.getHomeManager().getMaxHomes())));
        return true;
    }
}