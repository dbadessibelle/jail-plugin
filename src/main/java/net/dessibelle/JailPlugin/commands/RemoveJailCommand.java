package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveJailCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public RemoveJailCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /removejail <jail_name>");
            return true;
        }
        
        String jailName = args[0];
        
        if ("default".equals(jailName)) {
            sender.sendMessage("§cCannot remove the default jail!");
            return true;
        }
        
        if (plugin.getJailManager().getJail(jailName) == null) {
            sender.sendMessage("§cJail '" + jailName + "' does not exist!");
            return true;
        }
        
        if (plugin.getJailManager().removeJail(jailName)) {
            sender.sendMessage("§aJail '" + jailName + "' has been removed successfully!");
        } else {
            sender.sendMessage("§cFailed to remove jail. Check console for errors.");
        }
        
        return true;
    }
}