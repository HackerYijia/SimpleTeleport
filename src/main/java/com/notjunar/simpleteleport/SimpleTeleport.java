package com.notjunar.simpleteleport;

import org.bukkit.plugin.java.JavaPlugin;

public class SimpleTeleport extends JavaPlugin {

    private ConfigManager configManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.teleportManager = new TeleportManager(this);
        this.cooldownManager = new CooldownManager(this);

        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this));

        getLogger().info("SimpleTeleport has been enabled!");
    }

    @Override
    public void onDisable() {

        getLogger().info("SimpleTeleport has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}