package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class FocusCommand implements CommandExecutor
{
    private HCFaction plugin;
    
    public FocusCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /focus <name>");
            return true;
        }
        PlayerFaction namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)sender).getUniqueId());
        if (namedFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
            return true;
        }
        PlayerFaction targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId());
        if (namedFaction == targetFaction) {
            sender.sendMessage(ChatColor.RED + "You can not focus your own faction.");
            return true;
        }
        Player previous = null;
        if (namedFaction.getFocused() != null) {
            previous = Bukkit.getPlayer(namedFaction.getFocused());
        }
        namedFaction.setFocused(target.getName());
        namedFaction.broadcast(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");
        for (Player player : namedFaction.getOnlinePlayers()) {
            HCFaction.getPlugin().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).init(target);
            if (previous != null) {
                HCFaction.getPlugin().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).init(previous);
            }
        }
        return true;
    }
    
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        Player player = (Player)sender;
        ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                if (results.contains(target.getName())) {
                    continue;
                }
                results.add(target.getName());
            }
        }
        return results;
    }
}
