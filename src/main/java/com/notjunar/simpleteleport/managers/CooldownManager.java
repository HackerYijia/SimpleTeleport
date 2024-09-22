package com.notjunar.simpleteleport.managers;

import com.notjunar.simpleteleport.SimpleTeleport;
import com.notjunar.simpleteleport.utils.MessageUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final SimpleTeleport plugin;
    private final Map<String, Map<UUID, Long>> cooldowns;

    public CooldownManager(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    public boolean checkCooldown(UUID playerUUID, String commandType) {
        Map<UUID, Long> commandCooldowns = cooldowns.computeIfAbsent(commandType, k -> new HashMap<>());
        
        long cooldownTime = plugin.getConfigManager().getCooldown(commandType) * 1000L;
        long lastUsage = commandCooldowns.getOrDefault(playerUUID, 0L);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUsage < cooldownTime) {
            return false;
        }

        commandCooldowns.put(playerUUID, currentTime);
        return true;
    }

    public String getRemainingCooldown(UUID playerUUID, String commandType) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandType);
        if (commandCooldowns == null) {
            return "0";
        }

        long cooldownTime = plugin.getConfigManager().getCooldown(commandType) * 1000L;
        long lastUsage = commandCooldowns.getOrDefault(playerUUID, 0L);
        long currentTime = System.currentTimeMillis();
        long remainingTime = (lastUsage + cooldownTime - currentTime) / 1000;

        return remainingTime > 0 ? MessageUtil.formatTime((int) remainingTime) : "0";
    }

    public void resetCooldown(UUID playerUUID, String commandType) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandType);
        if (commandCooldowns != null) {
            commandCooldowns.remove(playerUUID);
        }
    }
}