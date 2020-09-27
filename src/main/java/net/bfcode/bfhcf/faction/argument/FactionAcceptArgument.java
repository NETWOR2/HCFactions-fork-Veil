package net.bfcode.bfhcf.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionAcceptArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionAcceptArgument(HCFaction plugin) {
        super("accept", "Accept a join request from an existing faction.", new String[] { "join", "a" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }
    
    @SuppressWarnings("deprecation")
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
        if (this.plugin.getFactionManager().getPlayerFaction(player) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only join player factions.");
            return true;
        }
        PlayerFaction targetFaction = (PlayerFaction)faction;
        if (targetFaction.getMembers().size() >= ConfigurationService.MAX_PLAYERS_PER_FACTION) {
            sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + ConfigurationService.MAX_PLAYERS_PER_FACTION + '.');
            return true;
        }
        if (!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
            return true;
        }
        if (targetFaction.isLocked()) {
            sender.sendMessage(ChatColor.RED + "You cannot join locked factions.");
            return true;
        }
        if (targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined the faction.");
        }
        return true;
    }
}
