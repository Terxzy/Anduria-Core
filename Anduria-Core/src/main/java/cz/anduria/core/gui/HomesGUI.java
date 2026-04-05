package cz.anduria.core.gui;

import cz.anduria.core.AnduriaCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * HomesGUI - 27-slotovy chest v DonutSMP stylu.
 *
 * Layout (0-indexovano):
 *   Radek 0 (0-8)  : sklenena hranice
 *   Radek 1 (9-17) : homes (prvnich 9)
 *   Radek 2 (18-24): homes (dalsich 7)
 *   Slot 25-26     : VIP sloty
 */
public class HomesGUI implements Listener {

    private static final List<Integer> HOME_SLOTS = new ArrayList<>();
    private static final List<Integer> VIP_SLOTS  = List.of(25, 26);

    static {
        for (int i = 9; i <= 24; i++) HOME_SLOTS.add(i);
    }

    private final AnduriaCore plugin;
    private final Map<Integer, String> slotToHome = new HashMap<>();
    private Inventory inventory;
    private Player owner;

    public HomesGUI(AnduriaCore plugin) { this.plugin = plugin; }

    // Otevreni GUI

    public void open(Player player) {
        this.owner = player;
        boolean vip = player.hasPermission("anduria.vip");

        String rawTitle = plugin.getHomeManager().getGuiConfig()
                .getString("gui.title", "&8Homes");
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(rawTitle);

        inventory = Bukkit.createInventory(null, 27, title);
        fillBackground(vip);
        placeHomes(player, vip);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.openInventory(inventory);
    }

    // Pozadi

    private void fillBackground(boolean vip) {
        ItemStack border = glass(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inventory.setItem(i, border);

        if (vip) {
            // VIP sloty: zlute sklo (prazdne, dokud nejsou homes)
            ItemStack vipEmpty = glass(Material.YELLOW_STAINED_GLASS_PANE,
                    "&6&lVIP Slot", "&7Volny VIP slot - pouzij /sethome");
            VIP_SLOTS.forEach(s -> inventory.setItem(s, vipEmpty));
        } else {
            // Zamcene VIP sloty
            ItemStack locked = glass(Material.RED_STAINED_GLASS_PANE,
                    "&c&lVIP Slot", "&7Tento slot je pouze pro VIP hrace.");
            VIP_SLOTS.forEach(s -> inventory.setItem(s, locked));
        }
    }

    // Rozmisteni homes

    private void placeHomes(Player player, boolean vip) {
        Map<String, Location> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        List<String> names = new ArrayList<>(homes.keySet());

        if (names.isEmpty()) {
            inventory.setItem(13, buildItem(Material.GRAY_DYE,
                    "&7Zadne homes", "&8Pouzij &7/sethome &8pro nastaveni domu."));
            return;
        }

        int maxRegular = HOME_SLOTS.size(); // 16
        List<String> regular = names.subList(0, Math.min(names.size(), maxRegular));
        List<String> vipHomes = vip && names.size() > maxRegular
                ? names.subList(maxRegular, Math.min(names.size(), maxRegular + 2))
                : Collections.emptyList();

        // Radek 1: sloty 9-17 (9 poli)
        // Radek 2: sloty 18-24 (7 poli)
        placeRow(regular, homes, 0,  9, 9);
        if (regular.size() > 9) placeRow(regular, homes, 9, 7, 18);

        for (int i = 0; i < vipHomes.size(); i++) {
            String name = vipHomes.get(i);
            int slot = VIP_SLOTS.get(i);
            inventory.setItem(slot, homeItem(name, homes.get(name), true));
            slotToHome.put(slot, name);
        }
    }

    private void placeRow(List<String> allNames, Map<String, Location> homes,
                          int fromIndex, int rowWidth, int rowStart) {
        int count = Math.min(allNames.size() - fromIndex, rowWidth);
        if (count <= 0) return;
        int offset = (rowWidth - count) / 2;
        for (int i = 0; i < count; i++) {
            String name = allNames.get(fromIndex + i);
            int slot = rowStart + offset + i;
            inventory.setItem(slot, homeItem(name, homes.get(name), false));
            slotToHome.put(slot, name);
        }
    }

    // Stavba itemu

    private ItemStack homeItem(String name, Location loc, boolean vip) {
        // RED_BED = Material s nazvem RED_BED v Paper 1.21
        Material mat  = vip ? Material.NETHER_STAR : Material.RED_BED;
        String prefix = vip ? "&6&l* " : "&a";
        String world  = loc.getWorld() != null ? loc.getWorld().getName() : "?";
        return buildItem(mat, prefix + name,
                "&7Svet: &f" + world,
                String.format("&7X:&f%.1f &7Y:&f%.1f &7Z:&f%.1f",
                        loc.getX(), loc.getY(), loc.getZ()),
                "",
                "&eKlikni pro teleportaci");
    }

    private ItemStack buildItem(Material mat, String displayName, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(parse(displayName));
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) loreList.add(line.isEmpty() ? Component.empty() : parse(line));
        meta.lore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack glass(Material mat, String name, String... lore) {
        return buildItem(mat, name, lore);
    }

    private Component parse(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    // Eventy

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.equals(owner) || !e.getInventory().equals(inventory)) return;
        e.setCancelled(true);

        String name = slotToHome.get(e.getRawSlot());
        if (name == null) return;

        Location loc = plugin.getHomeManager().getHomes(p.getUniqueId()).get(name);
        p.closeInventory();
        if (loc == null) {
            p.sendMessage(plugin.getMessageManager().get("home.not-found", "{name}", name));
            return;
        }
        p.teleport(loc);
        p.sendMessage(plugin.getMessageManager().get("home.teleported", "{name}", name));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer().equals(owner) && e.getInventory().equals(inventory))
            HandlerList.unregisterAll(this);
    }
}