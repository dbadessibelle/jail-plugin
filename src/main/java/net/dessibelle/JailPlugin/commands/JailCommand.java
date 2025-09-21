package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public JailCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /jail <player> [jail] [reason]");
            return true;
        }
        
        String targetName = args[0];
        String jailName = "default";
        String reason = "No reason specified";
        
        if (args.length > 1) {
            // Check if second argument is a jail name
            if (plugin.getJailManager().getJail(args[1]) != null) {
                jailName = args[1];
                if (args.length > 2) {
                    reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                }
            } else {
                // Second argument is part of the reason
                reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            }
        }
        
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found", "player", targetName));
            return true;
        }
        
        if (plugin.getJailManager().isPlayerJailed(target.getUniqueId())) {
            sender.sendMessage(plugin.getConfigManager().getMessage("already_jailed", "player", targetName));
            return true;
        }
        
        if (plugin.getConfigManager().getJailLocation(jailName) == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("jail_not_set")
                .replace("{jail}", jailName));
            return true;
        }
        
        String jailerName = sender instanceof Player ? sender.getName() : "Console";
        
        if (plugin.getJailManager().jailPlayer(target, reason, jailerName, jailName)) {
            String message = plugin.getConfigManager().getMessage("jail_success", "player", targetName);
            message = message.replace("{reason}", reason);
            message = message.replace("{jail}", jailName);
            sender.sendMessage(message);
            
            target.sendMessage("§cYou have been jailed in §e" + jailName + " §cfor: §f" + reason);
        } else {
            sender.sendMessage("§cFailed to jail player. Check console for errors.");
        }
        
        return true;
    }
}