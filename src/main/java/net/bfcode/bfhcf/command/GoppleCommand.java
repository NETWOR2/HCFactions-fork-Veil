package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.type.NotchAppleTimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class GoppleCommand implements CommandExecutor, TabCompleter
{
    private HCFaction plugin;
    
    public GoppleCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        NotchAppleTimer timer = this.plugin.getTimerManager().notchAppleTimer;
        Player player = (Player)sender;
        long remaining = timer.getRemaining(player);
        if (remaining <= 0L) {
            sender.sendMessage(ChatColor.RED + "No active Gopple timer.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Your " + timer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCFaction.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
