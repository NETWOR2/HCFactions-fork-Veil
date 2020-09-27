package net.bfcode.bfhcf.kothgame.koth.argument;

import java.time.format.DateTimeFormatter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;

public class KothScheduleArgument extends CommandArgument {
	
    public static DateTimeFormatter HHMMA;
    
    public KothScheduleArgument(HCFaction plugin) {
        super("schedule", "View the schedule for KOTH arenas");
        this.aliases = new String[] { "info", "i", "time" };
        this.permission = "hcf.command.koth.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.RED + "No koths are scheduled. Please check back later.");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
    
    static {
        HHMMA = DateTimeFormatter.ofPattern("h:mma");
    }
}
