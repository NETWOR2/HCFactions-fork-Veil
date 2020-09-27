package net.bfcode.bfhcf.faction.argument;

import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionLeaderArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionLeaderArgument(HCFaction plugin) {
        super("leader", "Sets the new leader for your faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "setleader", "newleader" };
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction leaders.");
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
        UUID uuid = player.getUniqueId();
        FactionMember selfMember = playerFaction.getMember(uuid);
        Role selfRole = selfMember.getRole();
        if (selfRole != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be the current faction leader to transfer the faction.");
            return true;
        }
        @SuppressWarnings("deprecation")
		FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not in your faction.");
            return true;
        }
        if (targetMember.getUniqueId().equals(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are already the faction leader.");
            return true;
        }
        targetMember.setRole(Role.LEADER);
        selfMember.setRole(Role.CAPTAIN);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + selfMember.getRole().getAstrix() + selfMember.getName() + ChatColor.YELLOW + " has transferred leadership of the faction to " + ConfigurationService.TEAMMATE_COLOUR + targetMember.getRole().getAstrix() + targetMember.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<String>();
        Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            @SuppressWarnings("unused")
			OfflinePlayer target;
            String targetName;
            if (entry.getValue().getRole() != Role.LEADER && (targetName = (target = Bukkit.getOfflinePlayer((UUID)entry.getKey())).getName()) != null) {
                if (results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
