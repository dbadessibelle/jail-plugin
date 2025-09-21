package net.dessibelle.JailPlugin.managers;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.JailedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JailManager {
    
    private final JailPlugin plugin;
    
    public JailManager(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean jailPlayer(Player player, String reason, String jailer) {
        try {
            // Check if player is already jailed
            if (plugin.getDatabaseManager().isPlayerJailed(player.getUniqueId())) {
                return false;
            }
            
            // Get jail location
            Location jailLocation = plugin.getConfigManager().getJailLocation();
            if (jailLocation == null) {
                return false;
            }
            
            // Create jailed player record
            JailedPlayer jailedPlayer = new JailedPlayer(
                player.getUniqueId(),
                player.getName(),
                reason,
                jailer,
                LocalDateTime.now(),
                player.getLocation()
            );
            
            // Save to database
            plugin.getDatabaseManager().addJailedPlayer(jailedPlayer);
            
            // Teleport player to jail
            player.teleport(jailLocation);
            
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error jailing player: " + e.getMessage());
            return false;
        }
    }
    
    public boolean unjailPlayer(UUID uuid) {
        try {
            JailedPlayer jailedPlayer = plugin.getDatabaseManager().getJailedPlayer(uuid);
            if (jailedPlayer == null) {
                return false;
            }
            
            // Remove from database
            plugin.getDatabaseManager().removeJailedPlayer(uuid);
            
            // Remove NPC if exists
            plugin.getNPCManager().removeNPC(uuid);
            
            // Teleport player back if online
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                Location originalLocation = jailedPlayer.getOriginalLocation();
                if (originalLocation != null) {
                    player.teleport(originalLocation);
                }
            }
            
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error unjailing player: " + e.getMessage());
            return false;
        }
    }
    
    public JailedPlayer getJailedPlayer(UUID uuid) {
        try {
            return plugin.getDatabaseManager().getJailedPlayer(uuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting jailed player: " + e.getMessage());
            return null;
        }
    }
    
    public List<JailedPlayer> getAllJailedPlayers() {
        try {
            return plugin.getDatabaseManager().getAllJailedPlayers();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting all jailed players: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean isPlayerJailed(UUID uuid) {
        return plugin.getDatabaseManager().isPlayerJailed(uuid);
    }
}