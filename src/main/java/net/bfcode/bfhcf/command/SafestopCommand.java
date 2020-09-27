package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class SafestopCommand implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (Player abc : Bukkit.getOnlinePlayers()) {
			Bukkit.dispatchCommand(abc, "lobby");
		}
		Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "savedata");
		HCFaction.getPlugin().getUserManager().saveUserData();
		HCFaction.getPlugin().getFactionManager().saveFactionData();
		HCFaction.getPlugin().getEconomyManager().saveEconomyData();
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(
				ChatColor.GOLD + "" + ChatColor.BOLD + "The server has been stopped by " + sender.getName());
		Bukkit.broadcastMessage(" ");
		Bukkit.shutdown();
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}
}
