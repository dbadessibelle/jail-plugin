package net.dessibelle.JailPlugin.models;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

public class JailedPlayer {
    
    private final UUID uuid;
    private final String playerName;
    private final String reason;
    private final String jailer;
    private final LocalDateTime jailDate;
    private final Location originalLocation;
    private final String jailName;
    
    public JailedPlayer(UUID uuid, String playerName, String reason, String jailer, 
                       LocalDateTime jailDate, Location originalLocation, String jailName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.reason = reason;
        this.jailer = jailer;
        this.jailDate = jailDate;
        this.originalLocation = originalLocation;
        this.jailName = jailName;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public String getReason() {
        return reason;
    }
    
    public String getJailer() {
        return jailer;
    }
    
    public LocalDateTime getJailDate() {
        return jailDate;
    }
    
    public Location getOriginalLocation() {
        return originalLocation;
    }
    
    public String getJailName() {
        return jailName;
    }
}