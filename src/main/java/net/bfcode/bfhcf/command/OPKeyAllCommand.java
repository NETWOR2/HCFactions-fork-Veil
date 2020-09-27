package net.bfcode.bfhcf.command;

import java.util.Collections;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.command.CommandExecutor;

public class OPKeyAllCommand implements CommandExecutor {
	
    private static List<String> COMPLETIONS;
    private HCFaction plugin;
    
    static {
        COMPLETIONS = ImmutableList.of("start", "end");
    }
    
    public OPKeyAllCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + "start h:m:s ");
                    sender.sendMessage(ChatColor.RED + "Example: /opkeyall start 3h3m");
                    return true;
                }
                long duration = JavaUtils.parse(args[1]);
                if (duration == -1L) {
                    sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                    return true;
                }
                if (duration < 1000L) {
                    sender.sendMessage(ChatColor.RED + "OPKeyAll time must last for at least 20 ticks.");
                    return true;
                }
                net.bfcode.bfhcf.timer.type.OPKeyAllTimer.OPKeyAllRunnable keyallRunnable = this.plugin.getOpKeyAllTimer().getOpKeyAllRunnable();
                if (keyallRunnable != null) {
                    sender.sendMessage(ChatColor.RED + "OPKeyAll is already enabled, use /" + label + " cancel to end it.");
                    return true;
                }
                this.plugin.getOpKeyAllTimer().start(duration);
                sender.sendMessage(ChatColor.GREEN + "Started the OPKeyAll for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                return true;
            }
            else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
                if (this.plugin.getOpKeyAllTimer().cancel()) {
                    sender.sendMessage(ChatColor.RED + "The OPKeyAll has ended!");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "The OPKeyAll is already active!");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + "start h:m:s ");
        sender.sendMessage(ChatColor.RED + "Example: /opkeyall start 3h3m");
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, OPKeyAllCommand.COMPLETIONS) : Collections.emptyList();
    }
}
