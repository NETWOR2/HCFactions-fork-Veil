package net.bfcode.bfhcf.timer.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;
import net.bfcode.bfhcf.utils.UUIDFetcher;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class TimerCheckArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public TimerCheckArgument(HCFaction plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
        this.permission = "hcf.command.timer.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <timerName> <playerName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        PlayerTimer temporaryTimer = null;
        for (Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer) {
                if (!timer.getName().equalsIgnoreCase(args[1])) {
                    continue;
                }
                temporaryTimer = (PlayerTimer)timer;
                break;
            }
        }
        if (temporaryTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }
        PlayerTimer playerTimer = temporaryTimer;
        new BukkitRunnable() {
            public void run() {
                UUID uuid;
                try {
                    uuid = UUIDFetcher.getUUIDOf(args[2]);
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                    return;
                }
                if (uuid == null) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                    return;
                }
                long remaining = playerTimer.getRemaining(uuid);
                sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + ChatColor.YELLOW + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
