package cz.anduria.core.commands;

import cz.anduria.core.AnduriaCore;
import cz.anduria.core.gui.HomesGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {
    private final AnduriaCore plugin;
    public HomesCommand(AnduriaCore p) { this.plugin = p; }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { s.sendMessage("Pouze hrace!"); return true; }
        if (!p.hasPermission("anduria.homes")) { p.sendMessage(plugin.getMessageManager().get("no-permission")); return true; }
        new HomesGUI(plugin).open(p);
        return true;
    }
}