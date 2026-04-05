package cz.anduria.core.listeners;

import cz.anduria.core.AnduriaCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    private final AnduriaCore plugin;

    public JoinLeaveListener(AnduriaCore plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();

        // Nastav join message (nahrazuje vychozi)
        e.joinMessage(plugin.getMessageManager().get("join.message", "{player}", name));

        // Zrus AFK pri opetovnem pripojeni
        plugin.getAfkManager().setAFK(e.getPlayer().getUniqueId(), false);

        // Specialni welcome zprava pouze pro konkretniho hrace
        String vipName = plugin.getMessageManager().getConfig().getString("welcome.player", "");
        if (!vipName.isEmpty() && name.equalsIgnoreCase(vipName)) {
            e.getPlayer().sendMessage(plugin.getMessageManager().get("welcome.message", "{player}", name));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        e.quitMessage(plugin.getMessageManager().get("leave.message", "{player}", name));
        plugin.getAfkManager().setAFK(e.getPlayer().getUniqueId(), false);
    }
}