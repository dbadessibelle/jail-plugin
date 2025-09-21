package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnjailCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public UnjailCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /unjail <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Try to find player by name (online or offline)
        Player onlineTarget = plugin.getServer().getPlayer(targetName);
        UUID targetUUID = null;
        
        if (onlineTarget != null) {
            targetUUID = onlineTarget.getUniqueId();
        } else {
            // Try to find offline player
            try {
                targetUUID = plugin.getServer().getOfflinePlayer(targetName).getUniqueId();
            } catch (Exception e) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found", "player", targetName));
                return true;
            }
        }
        
        if (!plugin.getJailManager().isPlayerJailed(targetUUID)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("not_jailed", "player", targetName));
            return true;
        }
        
        if (plugin.getJailManager().unjailPlayer(targetUUID)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("unjail_success", "player", targetName));
            
            if (onlineTarget != null) {
                onlineTarget.sendMessage("§aYou have been unjailed!");
            }
        } else {
            sender.sendMessage("§cFailed to unjail player. Check console for errors.");
        }
        
        return true;
    }
}