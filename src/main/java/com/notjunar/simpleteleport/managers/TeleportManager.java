package com.notjunar.simpleteleport.managers;

import com.notjunar.simpleteleport.SimpleTeleport;
import com.notjunar.simpleteleport.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final SimpleTeleport plugin;
    private final Map<UUID, BukkitRunnable> pendingTeleports;

    public TeleportManager(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.pendingTeleports = new HashMap<>();
    }

    public void teleport(Player player, Location destination, String teleportType) {
        int delay = plugin.getConfigManager().getDelay(teleportType);
        
        if (delay > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("teleport-starting")
                    .replace("%time%", String.valueOf(delay)));

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    executeTeleport(player, destination);
                    pendingTeleports.remove(player.getUniqueId());
                }
            };

            task.runTaskLater(plugin, delay * 20L);
            pendingTeleports.put(player.getUniqueId(), task);
        } else {
            executeTeleport(player, destination);
        }
    }

    private void executeTeleport(Player player, Location destination) {
        player.teleport(destination);
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("teleport-success"));
    }

    public void cancelPendingTeleport(Player player) {
        BukkitRunnable task = pendingTeleports.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("teleport-cancelled"));
        }
    }

    public boolean hasPendingTeleport(Player player) {
        return pendingTeleports.containsKey(player.getUniqueId());
    }
}