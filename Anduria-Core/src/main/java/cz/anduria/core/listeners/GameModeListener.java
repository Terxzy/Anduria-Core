package cz.anduria.core.listeners;

import cz.anduria.core.AnduriaCore;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Odesilani custom zprav pri zmene herni modu.
 *
 * Zpravy jsou konfigurovatelne v messages/messages.yml pod klicem gamemode.*
 *
 * Placeholdery:
 *   {player}   - jmeno hrace
 *   {gamemode} - nazev noveho modu v cestine
 */
public class GameModeListener implements Listener {

    private final AnduriaCore plugin;

    public GameModeListener(AnduriaCore plugin) { this.plugin = plugin; }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        String name      = e.getPlayer().getName();
        String modeName  = translateMode(e.getNewGameMode());

        // Zprava hracovi, ktery zmenil mod
        e.getPlayer().sendMessage(plugin.getMessageManager().get(
                "gamemode.change", "{player}", name, "{gamemode}", modeName));

        // Volitelny broadcast na cely server
        if (plugin.getMessageManager().getConfig().getBoolean("gamemode.broadcast", false)) {
            plugin.getServer().broadcast(plugin.getMessageManager().get(
                    "gamemode.broadcast-message", "{player}", name, "{gamemode}", modeName));
        }
    }

    private String translateMode(GameMode mode) {
        return switch (mode) {
            case SURVIVAL  -> "Survival";
            case CREATIVE  -> "Kreativni";
            case ADVENTURE -> "Dobrodruzny";
            case SPECTATOR -> "Divak";
        };
    }
}