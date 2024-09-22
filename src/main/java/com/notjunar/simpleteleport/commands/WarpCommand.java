package com.notjunar.simpleteleport.commands;

import com.notjunar.simpleteleport.SimpleTeleport;
import com.notjunar.simpleteleport.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WarpCommand implements CommandExecutor {

    private final SimpleTeleport plugin;
    private final Map<String, Location> warps;

    public WarpCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.warps = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("warp")) {
            if (args.length < 1) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("warp-usage"));
                return true;
            }
            return teleportToWarp(player, args[0]);
        } else if (command.getName().equalsIgnoreCase("setwarp")) {
            if (args.length < 1) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("setwarp-usage"));
                return true;
            }
            return setWarp(player, args[0]);
        }

        return false;
    }

    private boolean teleportToWarp(Player player, String warpName) {
        if (!warps.containsKey(warpName)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("warp-not-found"));
            return true;
        }

        if (!plugin.getCooldownManager().checkCooldown(player.getUniqueId(), "warp")) {
            String remainingTime = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "warp");
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("cooldown")
                    .replace("%time%", remainingTime));
            return true;
        }

        Location warpLocation = warps.get(warpName);
        plugin.getTeleportManager().teleport(player, warpLocation, "warp");
        return true;
    }

    private boolean setWarp(Player player, String warpName) {
        if (!player.hasPermission("simpleteleport.setwarp")) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        warps.put(warpName, player.getLocation());
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("warp-set")
                .replace("%warp%", warpName));
        return true;
    }
}