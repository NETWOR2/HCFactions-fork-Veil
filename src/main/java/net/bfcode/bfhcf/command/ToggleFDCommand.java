package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.CommandExecutor;

public class ToggleFDCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hcf.command.togglefd")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        boolean newStatus = ConfigurationService.DIAMOND_ORE_ALERTS = !ConfigurationService.DIAMOND_ORE_ALERTS;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', (newStatus ? ("&aYou have enabled found diamond ore notifications.") : ("&cYou have disabled found diamond ore notifications."))));
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command commanda, String label, String[] args) {
        return Collections.emptyList();
    }
}
