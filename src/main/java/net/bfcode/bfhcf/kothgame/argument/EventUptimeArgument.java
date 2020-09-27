package net.bfcode.bfhcf.kothgame.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.utils.DateTimeFormats;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class EventUptimeArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public EventUptimeArgument(HCFaction plugin) {
        super("uptime", "Check the uptime of an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        if (eventTimer.getRemaining() <= 0L) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }
        EventFaction eventFaction;
        sender.sendMessage(ChatColor.YELLOW + "Up-time of " + eventTimer.getName() + " timer" + (((eventFaction = eventTimer.getEventFaction()) == null) ? "" : (": " + ChatColor.BLUE + '(' + eventFaction.getDisplayName(sender) + ChatColor.BLUE + ')')) + ChatColor.YELLOW + " is " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.YELLOW + ", started at " + ChatColor.GOLD + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.YELLOW + '.');
        return true;
    }
}
