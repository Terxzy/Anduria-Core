package cz.anduria.core;

import cz.anduria.core.commands.*;
import cz.anduria.core.listeners.JoinLeaveListener;
import cz.anduria.core.listeners.GameModeListener;
import cz.anduria.core.managers.*;
import cz.anduria.core.placeholders.AfkPlaceholder;
import org.bukkit.plugin.java.JavaPlugin;

public class AnduriaCore extends JavaPlugin {

    private HomeManager homeManager;
    private AFKManager afkManager;
    private CooldownManager cooldownManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        messageManager  = new MessageManager(this);
        homeManager     = new HomeManager(this);
        afkManager      = new AFKManager(this);
        cooldownManager = new CooldownManager(this);

        registerCommands();

        getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new GameModeListener(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AfkPlaceholder(this).register();
            getLogger().info("PlaceholderAPI hook zaregistrovan.");
        }

        getLogger().info("Anduria-Core byl uspesne spusten!");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.saveHomes();
        getLogger().info("Anduria-Core byl vypnut.");
    }

    private void registerCommands() {
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
        getCommand("afk").setExecutor(new AFKCommand(this));
        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
    }

    public HomeManager getHomeManager()         { return homeManager; }
    public AFKManager getAfkManager()           { return afkManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public MessageManager getMessageManager()   { return messageManager; }
}