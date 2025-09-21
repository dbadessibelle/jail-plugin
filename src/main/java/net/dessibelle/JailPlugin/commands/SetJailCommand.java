package net.dessibelle.JailPlugin.commands;

import net.dessibelle.JailPlugin.JailPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetJailCommand implements CommandExecutor {
    
    private final JailPlugin plugin;
    
    public SetJailCommand(JailPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("jail.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /setjail <jail_name> [description]");
            return true;
        }
        
        Player player = (Player) sender;
        String jailName = args[0];
        String description = args.length > 1 ? 
            String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : 
            "Jail location";
        
        plugin.getConfigManager().setJailLocation(jailName, player.getLocation(), description);
        sender.sendMessage(plugin.getConfigManager().getMessage("jail_set")
            .replace("{jail}", jailName));
        
        return true;
    }
}