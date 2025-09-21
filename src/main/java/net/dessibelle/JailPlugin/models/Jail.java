package net.dessibelle.JailPlugin.models;

import org.bukkit.Location;

public class Jail {
    
    private final String name;
    private final Location location;
    private final String description;
    
    public Jail(String name, Location location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public String getDescription() {
        return description;
    }
}