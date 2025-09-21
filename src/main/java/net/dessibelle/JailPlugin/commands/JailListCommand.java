package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.JailedPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class JailListCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public JailListCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.list")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        List<JailedPlayer> jailedPlayers = plugin.getJailManager().getAllJailedPlayers();
        
        if (jailedPlayers.isEmpty()) {
            sender.sendMessage("§aNo players are currently jailed.");
            return true;
        }
        
        sender.sendMessage("§6=== Jailed Players ===");
        
        for (JailedPlayer jailedPlayer : jailedPlayers) {
            String dateFormatted = jailedPlayer.getJailDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            boolean isOnline = plugin.getServer().getPlayer(jailedPlayer.getUuid()) != null;
            String onlineStatus = isOnline ? "§aOnline" : "§cOffline";
            
            sender.sendMessage(String.format("§7- §f%s §7(%s) - §e%s §7- §f%s §7by §f%s",
                jailedPlayer.getPlayerName(),
                onlineStatus,
                jailedPlayer.getReason(),
                dateFormatted,
                jailedPlayer.getJailer()
            ));
        }
        
        return true;
    }
}