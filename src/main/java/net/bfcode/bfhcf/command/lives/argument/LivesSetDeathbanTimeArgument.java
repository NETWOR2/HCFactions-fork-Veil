package net.bfcode.bfhcf.command.lives.argument;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class LivesSetDeathbanTimeArgument extends CommandArgument
{
    public LivesSetDeathbanTimeArgument() {
        super("setdeathbantime", "Sets the base deathban time");
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <time>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        long duration = JavaUtils.parse(args[1]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        ConfigurationService.DEFAULT_DEATHBAN_DURATION = duration;
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Base death-ban time set to " + DurationFormatUtils.formatDurationWords(duration, true, true) + " (not including multipliers, etc).");
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
