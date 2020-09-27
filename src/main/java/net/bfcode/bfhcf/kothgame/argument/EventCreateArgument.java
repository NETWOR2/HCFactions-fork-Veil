package net.bfcode.bfhcf.kothgame.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.EventType;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class EventCreateArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public EventCreateArgument(HCFaction plugin) {
        super("create", "Defines a new event", new String[] { "make", "define" });
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <eventName> <Conquest|KOTH|CITADEL>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
            return true;
        }
        String upperCase2;
        String upperCase = upperCase2 = args[2].toUpperCase();
        switch (upperCase2) {
            case "CONQUEST": {
                faction = new ConquestFaction(args[1]);
                break;
            }
            case "KOTH": {
                faction = new KothFaction(args[1]);
                break;
            }
            case "CITADEL": {
                faction = new CitadelFaction(args[1]);
                break;
            }
            default: {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
        }
        this.plugin.getFactionManager().createFaction(faction, sender);
        sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            return Collections.emptyList();
        }
        EventType[] eventTypes = EventType.values();
        ArrayList<String> results = new ArrayList<String>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            results.add(eventType.name());
        }
        return results;
    }
}
