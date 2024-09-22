package com.notjunar.simpleteleport.commands;

import com.notjunar.simpleteleport.SimpleTeleport;
import com.notjunar.simpleteleport.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final SimpleTeleport plugin;
    private Location spawnLocation;

    public SpawnCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.spawnLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("spawn")) {
            return teleportToSpawn(player);
        } else if (command.getName().equalsIgnoreCase("setspawn")) {
            return setSpawn(player);
        }

        return false;
    }

    private boolean teleportToSpawn(Player player) {
        if (!plugin.getCooldownManager().checkCooldown(player.getUniqueId(), "spawn")) {
            String remainingTime = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "spawn");
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("cooldown")
                    .replace("%time%", remainingTime));
            return true;
        }

        plugin.getTeleportManager().teleport(player, spawnLocation, "spawn");
        return true;
    }

    private boolean setSpawn(Player player) {
        if (!player.hasPermission("simpleteleport.setspawn")) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        spawnLocation = player.getLocation();
        player.getWorld().setSpawnLocation(spawnLocation);
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("spawn-set"));
        return true;
    }
}