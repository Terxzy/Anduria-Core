package cz.anduria.core.managers;

import cz.anduria.core.AnduriaCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final AnduriaCore plugin;
    private FileConfiguration config;

    public MessageManager(AnduriaCore plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File dir = new File(plugin.getDataFolder(), "messages");
        dir.mkdirs();
        File file = new File(dir, "messages.yml");
        if (!file.exists()) plugin.saveResource("messages/messages.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
        InputStream def = plugin.getResource("messages/messages.yml");
        if (def != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(def, StandardCharsets.UTF_8)));
        }
    }

    public String getRaw(String key) {
        return config.getString(key, "&cMissing key: " + key);
    }

    public Component get(String key) {
        return parse(getRaw(key));
    }

    public Component get(String key, String... pairs) {
        String raw = getRaw(key);
        for (int i = 0; i + 1 < pairs.length; i += 2) raw = raw.replace(pairs[i], pairs[i + 1]);
        return parse(raw);
    }

    private Component parse(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public FileConfiguration getConfig() { return config; }
}