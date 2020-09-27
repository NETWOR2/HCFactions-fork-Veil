package net.bfcode.bfhcf.faction.argument;

import com.google.common.collect.Lists;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.FactionRelationRemoveEvent;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import com.google.common.collect.Iterables;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class FactionUnallyArgument extends CommandArgument
{
    private static ImmutableList<String> COMPLETIONS;
    private HCFaction plugin;
    
    public FactionUnallyArgument(HCFaction plugin) {
        super("unally", "Remove an ally pact with other factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "unalliance", "neutral" };
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <all|factionName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to edit relations.");
            return true;
        }
        Relation relation = Relation.ALLY;
        HashSet<PlayerFaction> targetFactions = new HashSet<PlayerFaction>();
        if (args[1].equalsIgnoreCase("all")) {
            List<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your faction has no allies.");
                return true;
            }
            targetFactions.addAll(allies);
        }
        else {
            Faction searchedFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (!(searchedFaction instanceof PlayerFaction)) {
                sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            targetFactions.add((PlayerFaction)searchedFaction);
        }
        for (PlayerFaction targetFaction : targetFactions) {
            if (playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                sender.sendMessage(ChatColor.RED + "Your faction is not " + relation.getDisplayName() + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
                return true;
            }
            FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
                return true;
            }
            playerFaction.broadcast(ChatColor.YELLOW + "Your faction has broken its " + relation.getDisplayName() + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            targetFaction.broadcast(ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " has dropped their " + relation.getDisplayName() + ChatColor.YELLOW + " with your faction.");
        }
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }
        return (List<String>)Lists.newArrayList(Iterables.concat((Iterable)FactionUnallyArgument.COMPLETIONS, (Iterable)playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }
}
