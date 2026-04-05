package cz.anduria.core.placeholders;

import cz.anduria.core.AnduriaCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Dostupne placeholdery:
 *   %anduria_afk%        -> "[AFK]" pokud je hrac AFK, jinak ""
 *   %anduria_afk_status% -> "AFK" / "Online"
 *   %anduria_afk_bool%   -> "true" / "false"
 */
public class AfkPlaceholder extends PlaceholderExpansion {

    private final AnduriaCore plugin;

    public AfkPlaceholder(AnduriaCore plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "anduria"; }
    @Override public @NotNull String getAuthor()     { return "Anduria"; }
    @Override public @NotNull String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()               { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        boolean afk = plugin.getAfkManager().isAFK(player.getUniqueId());
        return switch (params.toLowerCase()) {
            case "afk"        -> afk ? "[AFK]" : "";
            case "afk_status" -> afk ? "AFK" : "Online";
            case "afk_bool"   -> String.valueOf(afk);
            default           -> null;
        };
    }
}