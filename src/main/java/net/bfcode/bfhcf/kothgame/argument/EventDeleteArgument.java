package net.bfcode.bfhcf.kothgame.argument;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;

public class EventDeleteArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public EventDeleteArgument(HCFaction plugin) {
        super("delete", "Deletes an event");
        this.plugin = plugin;
        this.aliases = new String[] { "remove", "del" };
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <eventName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        if (this.plugin.getFactionManager().removeFaction(faction, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Deleted event faction " + ChatColor.AQUA + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
