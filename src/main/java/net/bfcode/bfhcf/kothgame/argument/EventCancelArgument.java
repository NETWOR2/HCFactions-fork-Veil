package net.bfcode.bfhcf.kothgame.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;

public class EventCancelArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public EventCancelArgument(HCFaction plugin) {
        super("cancel", "Cancels a running event", new String[] { "stop", "end" });
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        EventFaction eventFaction = eventTimer.getEventFaction();
        if (!eventTimer.clearCooldown()) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }
        Bukkit.broadcastMessage(sender.getName() + ChatColor.YELLOW + " has cancelled " + ((eventFaction == null) ? "the active event" : (ChatColor.AQUA + eventFaction.getName() + ChatColor.YELLOW)) + ".");
        return true;
    }
}
