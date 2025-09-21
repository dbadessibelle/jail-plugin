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
            sender.sendMessage("§cUsage: /jail <player> [reason]");
            return true;
        }
        
        String targetName = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found", "player", targetName));
            return true;
        }
        
        if (plugin.getJailManager().isPlayerJailed(target.getUniqueId())) {
            sender.sendMessage(plugin.getConfigManager().getMessage("already_jailed", "player", targetName));
            return true;
        }
        
        if (plugin.getConfigManager().getJailLocation() == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("jail_not_set"));
            return true;
        }
        
        String jailerName = sender instanceof Player ? sender.getName() : "Console";
        
        if (plugin.getJailManager().jailPlayer(target, reason, jailerName)) {
            String message = plugin.getConfigManager().getMessage("jail_success", "player", targetName);
            message = message.replace("{reason}", reason);
            sender.sendMessage(message);
            
            target.sendMessage("§cYou have been jailed for: §f" + reason);
        } else {
            sender.sendMessage("§cFailed to jail player. Check console for errors.");
        }
        
        return true;
    }
}