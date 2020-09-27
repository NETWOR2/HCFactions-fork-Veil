package net.bfcode.bfhcf.faction.argument;

import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionClaimsArgument extends CommandArgument {
	
	private HCFaction plugin;

	public FactionClaimsArgument(HCFaction plugin) {
		super("claims", "View all claims for a faction.");
		this.plugin = plugin;
	}

	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName() + " [factionName]";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		@SuppressWarnings("deprecation")
		PlayerFaction selfFaction = (sender instanceof Player)
				? this.plugin.getFactionManager().getPlayerFaction((Player) sender)
				: null;
		ClaimableFaction targetFaction;
		if (args.length < 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
				return true;
			}
			if (selfFaction == null) {
				sender.sendMessage(ChatColor.RED + "You are not in a faction.");
				return true;
			}
			targetFaction = selfFaction;
		} else {
			Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
			if (faction == null) {
				sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1]
						+ " not found.");
				return true;
			}
			if (!(faction instanceof ClaimableFaction)) {
				sender.sendMessage(ChatColor.RED + "You can only check the claims of factions that can have claims.");
				return true;
			}
			targetFaction = (ClaimableFaction) faction;
		}
		Collection<Claim> claims = targetFaction.getClaims();
		if (claims.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Faction " + targetFaction.getDisplayName(sender) + ChatColor.RED
					+ " has no claimed land.");
			return true;
		}
		if (sender instanceof Player && !sender.isOp() && targetFaction instanceof PlayerFaction
				&& ((PlayerFaction) targetFaction).getHome() == null
				&& (selfFaction == null || !selfFaction.equals(targetFaction))) {
			sender.sendMessage(ChatColor.RED + "You cannot view the claims of " + targetFaction.getDisplayName(sender)
					+ ChatColor.RED + " because their home is unset.");
			return true;
		}
		sender.sendMessage(ChatColor.YELLOW + "Claims of " + targetFaction.getDisplayName(sender));
		for (Claim claim : claims) {
			sender.sendMessage(ChatColor.GRAY + " " + claim.getFormattedName());
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}
		if (args[1].isEmpty()) {
			return null;
		}
		Player player = (Player) sender;
		List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
		for (Player target : Bukkit.getServer().getOnlinePlayers()) {
			if (player.canSee(target) && !results.contains(target.getName())) {
				results.add(target.getName());
			}
		}
		return results;
	}
}
