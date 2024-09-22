package com.notjunar.simpleteleport.managers;

import com.notjunar.simpleteleport.SimpleTeleport;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final SimpleTeleport plugin;
    private FileConfiguration config;

    public ConfigManager(SimpleTeleport plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Message not found: " + path);
    }

    public int getDelay(String teleportType) {
        return config.getInt("delays." + teleportType, 0);
    }

    public int getCooldown(String commandType) {
        return config.getInt("cooldowns." + commandType, 0);
    }

    public void saveConfig() {
        plugin.saveConfig();
    }
}