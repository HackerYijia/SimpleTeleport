package com.notjunar.simpleteleport.commands;

import com.notjunar.simpleteleport.SimpleTeleport;
import com.notjunar.simpleteleport.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final SimpleTeleport plugin;
    private final Map<UUID, UUID> tpaRequests;

    public TpaCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.tpaRequests = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "tpa":
                return sendTpaRequest(player, args);
            case "tpahere":
                return sendTpaHereRequest(player, args);
            case "tpaccept":
                return acceptTpaRequest(player);
            case "tpdeny":
                return denyTpaRequest(player);
            default:
                return false;
        }
    }

    private boolean sendTpaRequest(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpa-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }

        tpaRequests.put(target.getUniqueId(), player.getUniqueId());
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpa-sent")
                .replace("%player%", target.getName()));
        MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("tpa-received")
                .replace("%player%", player.getName()));
        return true;
    }

    private boolean sendTpaHereRequest(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpahere-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }

        tpaRequests.put(player.getUniqueId(), target.getUniqueId());
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpahere-sent")
                .replace("%player%", target.getName()));
        MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("tpahere-received")
                .replace("%player%", player.getName()));
        return true;
    }

    private boolean acceptTpaRequest(Player player) {
        UUID requesterUUID = tpaRequests.remove(player.getUniqueId());
        if (requesterUUID == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("no-tpa-request"));
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("player-offline"));
            return true;
        }

        if (tpaRequests.containsKey(requesterUUID)) {
            // This is a tpahere request
            plugin.getTeleportManager().teleport(player, requester.getLocation(), "tpa");
        } else {
            // This is a regular tpa request
            plugin.getTeleportManager().teleport(requester, player.getLocation(), "tpa");
        }

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpa-accepted"));
        MessageUtil.sendMessage(requester, plugin.getConfigManager().getMessage("tpa-accepted-target"));
        return true;
    }

    private boolean denyTpaRequest(Player player) {
        UUID requesterUUID = tpaRequests.remove(player.getUniqueId());
        if (requesterUUID == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("no-tpa-request"));
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("tpa-denied"));
        if (requester != null) {
            MessageUtil.sendMessage(requester, plugin.getConfigManager().getMessage("tpa-denied-target"));
        }
        return true;
    }
}