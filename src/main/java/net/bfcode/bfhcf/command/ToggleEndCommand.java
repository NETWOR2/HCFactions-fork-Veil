package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.CommandExecutor;

public class ToggleEndCommand implements CommandExecutor, TabCompleter
{
    @SuppressWarnings("unused")
	private HCFaction plugin;
    
    public ToggleEndCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newMode = !BasePlugin.getPlugin().getServerHandler().isEnd();
        BasePlugin.getPlugin().getServerHandler().setEnd(newMode);
        Bukkit.broadcastMessage((newMode ? (ChatColor.GREEN + "The End is now open.") : (ChatColor.RED + "The End is now closed.")));
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
