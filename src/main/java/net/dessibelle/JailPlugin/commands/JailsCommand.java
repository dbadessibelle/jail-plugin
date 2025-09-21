package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import net.dessibelle.JailPlugin.models.Jail;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JailsCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public JailsCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.list")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        List<Jail> jails = plugin.getJailManager().getAllJails();
        
        if (jails.isEmpty()) {
            sender.sendMessage("§cNo jails have been created yet. Use /setjail <name> to create one.");
            return true;
        }
        
        sender.sendMessage("§6=== Available Jails ===");
        
        for (Jail jail : jails) {
            String worldName = jail.getLocation().getWorld() != null ? 
                jail.getLocation().getWorld().getName() : "Unknown";
            
            sender.sendMessage(String.format("§7- §e%s §7in §f%s §7at §f%.1f, %.1f, %.1f",
                jail.getName(),
                worldName,
                jail.getLocation().getX(),
                jail.getLocation().getY(),
                jail.getLocation().getZ()
            ));
            
            if (jail.getDescription() != null && !jail.getDescription().isEmpty()) {
                sender.sendMessage("  §7Description: §f" + jail.getDescription());
            }
        }
        
        return true;
    }
}