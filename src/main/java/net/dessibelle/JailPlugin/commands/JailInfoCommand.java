package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.JailedPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class JailInfoCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public JailInfoCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.info")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /jailinfo <player>");
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
        
        JailedPlayer jailedPlayer = plugin.getJailManager().getJailedPlayer(targetUUID);
        if (jailedPlayer == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("not_jailed", "player", targetName));
            return true;
        }
        
        String dateFormatted = jailedPlayer.getJailDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        String message = plugin.getConfigManager().getMessage("jail_info", "player", jailedPlayer.getPlayerName());
        message = message.replace("{reason}", jailedPlayer.getReason());
        message = message.replace("{jailer}", jailedPlayer.getJailer());
        message = message.replace("{date}", dateFormatted);
        message = message.replace("{jail}", jailedPlayer.getJailName());
        
        sender.sendMessage(message);
        
        return true;
    }
}