package net.bfcode.bfhcf.command.death.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathhistory.DeathHistoryHandler;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class DeathHistoryArgument extends CommandArgument {
	
    public DeathHistoryArgument(HCFaction plugin) {
        super("history", "Check a history of death");
        this.permission = "hcf.command.death.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
			return true;
		}
		else if(args.length == 2) {
			OfflinePlayer arg = Bukkit.getOfflinePlayer(args[1]);
			if (DeathHistoryHandler.death_file.get(arg.getName()) == null) {
	    		sender.sendMessage(CC.translate("&cThe player '&f" + arg.getName() + "&c' doesn't exist in our database."));
	    		return true;
	    	}
			sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			sender.sendMessage(CC.translate("&3&lDeath History"));
			sender.sendMessage(CC.translate(""));
			sender.sendMessage(CC.translate("&b" + arg.getName() + " Death History"));
			sender.sendMessage("");
			List<String> data = DeathHistoryHandler.death_file.getStringList(arg.getName());
			Collections.reverse(data);
			if(ConfigurationService.KIT_MAP) {
		        for (int i = 0; i < 20 && i < data.size(); ++i) {
		        	String next = data.get(i);
		        	sender.sendMessage(CC.translate("&7" + (i + 1) + ") &c" + next));
				}	
			} else {
		        for (int i = 0; i < 25 && i < data.size(); ++i) {
		        	String next = data.get(i);
		        	sender.sendMessage(CC.translate("&7" + (i + 1) + ") &c" + next));
				}
			}
	        sender.sendMessage(CC.translate(""));
	        sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			return true;	
		}
		return true;
    }
    
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        List<String> results = new ArrayList<String>();
        for (String players : DeathHistoryHandler.death_file.getKeys(false)) {
        	results.add(players);
        }
        return BukkitUtils.getCompletions(args, results);
    }

}
