package net.bfcode.bfhcf.kothgame.argument;

import java.util.Iterator;
import org.bukkit.ChatColor;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;

public class EventListArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public EventListArgument(HCFaction plugin) {
        super("list", "Check the uptime of an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Faction> events = this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).collect(Collectors.toList());
        sender.sendMessage(ChatColor.GREEN + "Current events:");
        for (Faction factionEvent : events) {
            sender.sendMessage(ChatColor.GREEN + factionEvent.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.YELLOW + this.getFactionEventType(factionEvent) + ChatColor.DARK_GRAY + ")");
        }
        return true;
    }
    
    private String getFactionEventType(Faction factionEvent) {
        if (factionEvent instanceof KothFaction) {
            return "Koth";
        }
        if (factionEvent instanceof CitadelFaction) {
            return "Palace";
        }
        return "Conquest";
    }
}
