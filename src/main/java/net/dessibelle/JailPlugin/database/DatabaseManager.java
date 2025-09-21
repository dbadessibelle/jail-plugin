package net.dessibelle.JailPlugin.database;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.JailedPlayer;
import org.bukkit.Location;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    
    private final JailPlugin plugin;
    private Connection connection;
    private final String databasePath;
    
    public DatabaseManager(JailPlugin plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getDataFolder().getAbsolutePath() + File.separator + "jail_data.db";
    }
    
    public boolean initialize() {
        try {
            // Create plugin data folder if it doesn't exist
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            
            // Connect to database
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            
            // Create tables
            createTables();
            
            plugin.getLogger().info("Database initialized successfully!");
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        String createJailedPlayersTable = """
            CREATE TABLE IF NOT EXISTS jailed_players (
                uuid TEXT PRIMARY KEY,
                player_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                jailer TEXT NOT NULL,
                jail_date TEXT NOT NULL,
                world TEXT,
                x REAL,
                y REAL,
                z REAL,
                yaw REAL,
                pitch REAL
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createJailedPlayersTable);
        }
    }
    
    public void addJailedPlayer(JailedPlayer jailedPlayer) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO jailed_players 
            (uuid, player_name, reason, jailer, jail_date, world, x, y, z, yaw, pitch) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, jailedPlayer.getUuid().toString());
            pstmt.setString(2, jailedPlayer.getPlayerName());
            pstmt.setString(3, jailedPlayer.getReason());
            pstmt.setString(4, jailedPlayer.getJailer());
            pstmt.setString(5, jailedPlayer.getJailDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Location originalLocation = jailedPlayer.getOriginalLocation();
            if (originalLocation != null) {
                pstmt.setString(6, originalLocation.getWorld().getName());
                pstmt.setDouble(7, originalLocation.getX());
                pstmt.setDouble(8, originalLocation.getY());
                pstmt.setDouble(9, originalLocation.getZ());
                pstmt.setFloat(10, originalLocation.getYaw());
                pstmt.setFloat(11, originalLocation.getPitch());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.REAL);
                pstmt.setNull(8, Types.REAL);
                pstmt.setNull(9, Types.REAL);
                pstmt.setNull(10, Types.REAL);
                pstmt.setNull(11, Types.REAL);
            }
            
            pstmt.executeUpdate();
        }
    }
    
    public void removeJailedPlayer(UUID uuid) throws SQLException {
        String sql = "DELETE FROM jailed_players WHERE uuid = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.executeUpdate();
        }
    }
    
    public JailedPlayer getJailedPlayer(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM jailed_players WHERE uuid = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createJailedPlayerFromResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    public List<JailedPlayer> getAllJailedPlayers() throws SQLException {
        List<JailedPlayer> jailedPlayers = new ArrayList<>();
        String sql = "SELECT * FROM jailed_players";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                jailedPlayers.add(createJailedPlayerFromResultSet(rs));
            }
        }
        
        return jailedPlayers;
    }
    
    private JailedPlayer createJailedPlayerFromResultSet(ResultSet rs) throws SQLException {
        UUID uuid = UUID.fromString(rs.getString("uuid"));
        String playerName = rs.getString("player_name");
        String reason = rs.getString("reason");
        String jailer = rs.getString("jailer");
        LocalDateTime jailDate = LocalDateTime.parse(rs.getString("jail_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        Location originalLocation = null;
        String worldName = rs.getString("world");
        if (worldName != null) {
            originalLocation = new Location(
                plugin.getServer().getWorld(worldName),
                rs.getDouble("x"),
                rs.getDouble("y"),
                rs.getDouble("z"),
                rs.getFloat("yaw"),
                rs.getFloat("pitch")
            );
        }
        
        return new JailedPlayer(uuid, playerName, reason, jailer, jailDate, originalLocation);
    }
    
    public boolean isPlayerJailed(UUID uuid) {
        try {
            return getJailedPlayer(uuid) != null;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking if player is jailed: " + e.getMessage());
            return false;
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
        }
    }
}