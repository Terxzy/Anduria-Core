package cz.anduria.core.managers;

import cz.anduria.core.AnduriaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HomeManager {

    private final AnduriaCore plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private FileConfiguration guiConfig;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeManager(AnduriaCore plugin) {
        this.plugin = plugin;
        setupFiles();
        loadHomes();
    }

    private void setupFiles() {
        File dir = new File(plugin.getDataFolder(), "homes");
        dir.mkdirs();

        File guiFile = new File(dir, "config.yml");
        if (!guiFile.exists()) plugin.saveResource("homes/config.yml", false);
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        InputStream def = plugin.getResource("homes/config.yml");
        if (def != null) guiConfig.setDefaults(YamlConfiguration.loadConfiguration(
                new InputStreamReader(def, StandardCharsets.UTF_8)));

        dataFile = new File(dir, "data.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { plugin.getLogger().severe("Cannot create homes/data.yml: " + e.getMessage()); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadHomes() {
        homes.clear();
        ConfigurationSection root = dataConfig.getConfigurationSection("homes");
        if (root == null) return;
        for (String uuidStr : root.getKeys(false)) {
            UUID uuid;
            try { uuid = UUID.fromString(uuidStr); } catch (IllegalArgumentException e) { continue; }
            ConfigurationSection ps = root.getConfigurationSection(uuidStr);
            if (ps == null) continue;
            Map<String, Location> ph = new LinkedHashMap<>();
            for (String name : ps.getKeys(false)) {
                World w = Bukkit.getWorld(ps.getString(name + ".world", ""));
                if (w == null) continue;
                ph.put(name, new Location(w,
                        ps.getDouble(name + ".x"), ps.getDouble(name + ".y"), ps.getDouble(name + ".z"),
                        (float) ps.getDouble(name + ".yaw"), (float) ps.getDouble(name + ".pitch")));
            }
            homes.put(uuid, ph);
        }
    }

    public void saveHomes() {
        dataConfig.set("homes", null);
        for (Map.Entry<UUID, Map<String, Location>> pe : homes.entrySet()) {
            String uid = pe.getKey().toString();
            for (Map.Entry<String, Location> he : pe.getValue().entrySet()) {
                String p = "homes." + uid + "." + he.getKey();
                Location l = he.getValue();
                dataConfig.set(p + ".world", l.getWorld().getName());
                dataConfig.set(p + ".x", l.getX());
                dataConfig.set(p + ".y", l.getY());
                dataConfig.set(p + ".z", l.getZ());
                dataConfig.set(p + ".yaw",   (double) l.getYaw());
                dataConfig.set(p + ".pitch", (double) l.getPitch());
            }
        }
        try { dataConfig.save(dataFile); }
        catch (IOException e) { plugin.getLogger().severe("Cannot save homes: " + e.getMessage()); }
    }

    public Map<String, Location> getHomes(UUID uuid) {
        return homes.getOrDefault(uuid, new LinkedHashMap<>());
    }

    public boolean setHome(UUID uuid, String name, Location loc) {
        Map<String, Location> ph = homes.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
        if (!ph.containsKey(name) && ph.size() >= getMaxHomes()) return false;
        ph.put(name, loc);
        saveHomes();
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Location> ph = homes.get(uuid);
        if (ph == null || !ph.containsKey(name)) return false;
        ph.remove(name);
        saveHomes();
        return true;
    }

    public boolean hasHome(UUID uuid, String name) {
        return homes.containsKey(uuid) && homes.get(uuid).containsKey(name);
    }

    public int getMaxHomes()                       { return guiConfig.getInt("homes.max-homes", 7); }
    public FileConfiguration getGuiConfig()        { return guiConfig; }
}