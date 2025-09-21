package net.dessibelle.JailPlugin.managers;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.JailedPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCManager {
    
    private final JailPlugin plugin;
    private final NPCRegistry npcRegistry;
    private final Map<UUID, NPC> jailedNPCs;
    
    public NPCManager(JailPlugin plugin) {
        this.plugin = plugin;
        this.npcRegistry = CitizensAPI.getNPCRegistry();
        this.jailedNPCs = new HashMap<>();
    }
    
    public void initializeNPCs() {
        if (!plugin.getConfigManager().isNPCEnabled()) {
            return;
        }
        
        List<JailedPlayer> jailedPlayers = plugin.getJailManager().getAllJailedPlayers();
        
        for (JailedPlayer jailedPlayer : jailedPlayers) {
            Player player = plugin.getServer().getPlayer(jailedPlayer.getUuid());
            
            // Only create NPC if player is offline
            if (player == null || !player.isOnline()) {
                createNPC(jailedPlayer);
            }
        }
    }
    
    public void createNPC(JailedPlayer jailedPlayer) {
        if (!plugin.getConfigManager().isNPCEnabled()) {
            return;
        }
        
        // Don't create NPC if one already exists
        if (jailedNPCs.containsKey(jailedPlayer.getUuid())) {
            return;
        }
        
        Location jailLocation = plugin.getConfigManager().getJailLocation();
        if (jailLocation == null) {
            return;
        }
        
        // Calculate spawn location within radius
        Location spawnLocation = calculateSpawnLocation(jailLocation);
        
        // Create NPC name
        String npcName = plugin.getConfigManager().getNPCNameFormat()
            .replace("{player}", jailedPlayer.getPlayerName());
        
        // Create NPC
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, npcName);
        
        // Set skin if enabled
        if (plugin.getConfigManager().isSkinEnabled()) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinName(jailedPlayer.getPlayerName());
        }
        
        // Spawn NPC
        npc.spawn(spawnLocation);
        
        // Store NPC reference
        jailedNPCs.put(jailedPlayer.getUuid(), npc);
        
        plugin.getLogger().info("Created NPC for offline jailed player: " + jailedPlayer.getPlayerName());
    }
    
    public void removeNPC(UUID playerUUID) {
        NPC npc = jailedNPCs.remove(playerUUID);
        if (npc != null) {
            npc.destroy();
            plugin.getLogger().info("Removed NPC for player: " + playerUUID);
        }
    }
    
    public void removeAllNPCs() {
        for (NPC npc : jailedNPCs.values()) {
            npc.destroy();
        }
        jailedNPCs.clear();
    }
    
    public void handlePlayerJoin(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        // If player is jailed and has an NPC, remove the NPC
        if (plugin.getJailManager().isPlayerJailed(playerUUID)) {
            removeNPC(playerUUID);
            
            // Teleport player to jail
            Location jailLocation = plugin.getConfigManager().getJailLocation();
            if (jailLocation != null) {
                player.teleport(jailLocation);
            }
        }
    }
    
    public void handlePlayerLeave(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        // If player is jailed, create an NPC
        if (plugin.getJailManager().isPlayerJailed(playerUUID)) {
            JailedPlayer jailedPlayer = plugin.getJailManager().getJailedPlayer(playerUUID);
            if (jailedPlayer != null) {
                createNPC(jailedPlayer);
            }
        }
    }
    
    private Location calculateSpawnLocation(Location jailLocation) {
        double radius = plugin.getConfigManager().getNPCSpawnRadius();
        
        // Generate random offset within radius
        double angle = Math.random() * 2 * Math.PI;
        double distance = Math.random() * radius;
        
        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;
        
        return jailLocation.clone().add(offsetX, 0, offsetZ);
    }
    
    public boolean hasNPC(UUID playerUUID) {
        return jailedNPCs.containsKey(playerUUID);
    }
}