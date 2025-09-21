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
            config.getString("npc.name_format", "&c[JAILED] &f{player}"));
    }
    
    public boolean isSkinEnabled() {
        return config.getBoolean("npc.skin_enabled", true);
    }
}