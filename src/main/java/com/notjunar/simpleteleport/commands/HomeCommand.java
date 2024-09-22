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
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final SimpleTeleport plugin;
    private final Map<UUID, Location> homes;

    public HomeCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.homes = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("home")) {
            return teleportHome(player);
        } else if (command.getName().equalsIgnoreCase("sethome")) {
            return setHome(player);
        }

        return false;
    }

    private boolean teleportHome(Player player) {
        if (!homes.containsKey(player.getUniqueId())) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("home-not-set"));
            return true;
        }

        if (!plugin.getCooldownManager().checkCooldown(player.getUniqueId(), "home")) {
            String remainingTime = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "home");
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("cooldown")
                    .replace("%time%", remainingTime));
            return true;
        }

        Location home = homes.get(player.getUniqueId());
        plugin.getTeleportManager().teleport(player, home, "home");
        return true;
    }

    private boolean setHome(Player player) {
        homes.put(player.getUniqueId(), player.getLocation());
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("home-set"));
        return true;
    }
}