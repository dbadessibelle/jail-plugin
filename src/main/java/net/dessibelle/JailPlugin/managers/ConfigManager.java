package net.dessibelle.JailPlugin.managers;

import net.dessibelle.JailPlugin.JailPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final JailPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(JailPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public String getMessage(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String key, String placeholder, String value) {
        String message = getMessage(key);
        return message.replace("{" + placeholder + "}", value);
    }
    
    public Location getJailLocation() {
        return getJailLocation("default");
    }
    
    public Location getJailLocation(String jailName) {
        // First try to get from database
        try {
            var jail = plugin.getDatabaseManager().getJail(jailName);
            if (jail != null) {
                return jail.getLocation();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get jail location from database: " + e.getMessage());
        }
        
        // Fallback to config for default jail
        if ("default".equals(jailName)) {
            return getJailLocationFromConfig();
        }
        
        return null;
    }
    
    private Location getJailLocationFromConfig() {
        String worldName = config.getString("jail.world");
        if (worldName == null) return null;
        
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;
        
        double x = config.getDouble("jail.x");
        double y = config.getDouble("jail.y");
        double z = config.getDouble("jail.z");
        float yaw = (float) config.getDouble("jail.yaw");
        float pitch = (float) config.getDouble("jail.pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    public void setJailLocation(Location location) {
        setJailLocation("default", location, "Default jail location");
    }
    
    public void setJailLocation(String jailName, Location location, String description) {
        // Save to database
        try {
            var jail = new net.dessibelle.JailPlugin.models.Jail(jailName, location, description);
            plugin.getDatabaseManager().addJail(jail);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save jail to database: " + e.getMessage());
        }
        
        // Also save default jail to config for backwards compatibility
        if ("default".equals(jailName)) {
            saveJailToConfig(location);
        }
    }
    
    private void saveJailToConfig(Location location) {
        config.set("jail.world", location.getWorld().getName());
        config.set("jail.x", location.getX());
        config.set("jail.y", location.getY());
        config.set("jail.z", location.getZ());
        config.set("jail.yaw", location.getYaw());
        config.set("jail.pitch", location.getPitch());
        plugin.saveConfig();
    }
    
    public boolean isNPCEnabled() {
        return config.getBoolean("npc.enabled", true);
    }
    
    public double getNPCSpawnRadius() {
        return config.getDouble("npc.spawn_radius", 5.0);
    }
    
    public String getNPCNameFormat() {
        return ChatColor.translateAlternateColorCodes('&', 
            config.getString("npc.name_format", "&c[{jail}] &f{player}"));
    }
    
    public boolean isSkinEnabled() {
        return config.getBoolean("npc.skin_enabled", true);
    }
}