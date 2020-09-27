package net.bfcode.bfhcf.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class StatResetCommand implements CommandExecutor
{
    @SuppressWarnings("unused")
	private HCFaction plugin;
    
    public StatResetCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by console.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }
        target.getPlayer().setStatistic(Statistic.PLAYER_KILLS, 0);
        target.getPlayer().setStatistic(Statistic.DEATHS, 0);
        HCFaction.getPlugin().getUserManager().getUser(target.getUniqueId()).setKills(0);
        HCFaction.getPlugin().getUserManager().getUser(target.getUniqueId()).setDeaths(0);
        sender.sendMessage(ChatColor.GREEN + "You have reset " + target.getName() + " statistics.");
        return true;
    }
}
