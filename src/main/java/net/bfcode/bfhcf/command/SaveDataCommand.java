package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.CommandExecutor;

public class SaveDataCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HCFaction.getPlugin().saveData();
        sender.sendMessage(ChatColor.GREEN + "Saved!");
        return true;
    }
    
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
