package net.dessibelle.JailPlugin;

import net.dessibelle.JailPlugin.commands.*;
import net.dessibelle.JailPlugin.listeners.PlayerJoinLeaveListener;
import net.dessibelle.JailPlugin.managers.ConfigManager;
import net.dessibelle.JailPlugin.managers.JailManager;
import net.dessibelle.JailPlugin.managers.NPCManager;
import net.dessibelle.JailPlugin.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class JailPlugin extends JavaPlugin {
    
    private static JailPlugin instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private JailManager jailManager;
    private NPCManager npcManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        jailManager = new JailManager(this);
        npcManager = new NPCManager(this);
        
        // Initialize database
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register commands
        registerCommands();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        
        // Initialize NPCs for offline jailed players
        npcManager.initializeNPCs();
        
        getLogger().info("JailPlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.removeAllNPCs();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("JailPlugin has been disabled!");
    }
    
    private void registerCommands() {
        //Method invocator 'setExecutor' may produce 'NullPointerException'

        getCommand("jail").setExecutor(new JailCommand(this));
        getCommand("unjail").setExecutor(new UnjailCommand(this));
        getCommand("jailinfo").setExecutor(new JailInfoCommand(this));
        getCommand("jaillist").setExecutor(new JailListCommand(this));
        getCommand("setjail").setExecutor(new SetJailCommand(this));


        //Replace with 'Objects.requireNonNull(getCommand("name"))'
        /*
        Objects.requireNonNull(getCommand("jail")).setExecutor(new JailCommand(this));
        Objects.requireNonNull(getCommand("unjail")).setExecutor(new UnjailCommand(this));
        Objects.requireNonNull(getCommand("jailinfo")).setExecutor(new JailInfoCommand(this));
        Objects.requireNonNull(getCommand("jaillist")).setExecutor(new JailListCommand(this));
        Objects.requireNonNull(getCommand("setjail")).setExecutor(new SetJailCommand(this));
        */
    }
    
    public static JailPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public JailManager getJailManager() {
        return jailManager;
    }
    
    public NPCManager getNPCManager() {
        return npcManager;
    }
}