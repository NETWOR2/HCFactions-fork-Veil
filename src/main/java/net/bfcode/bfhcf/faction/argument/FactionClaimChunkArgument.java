package net.bfcode.bfhcf.faction.argument;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionClaimChunkArgument extends CommandArgument {
	
    @SuppressWarnings("unused")
	private static int CHUNK_RADIUS = 7;
    private HCFaction plugin;
    
    public FactionClaimChunkArgument(HCFaction plugin) {
        super("claimchunk", "Claim a chunk of land in the Wilderness.", new String[] { "chunkclaim" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return true;
        }
        Location location = player.getLocation();
        this.plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction, location.clone().add(7.0, 0.0, 7.0), location.clone().add(-7.0, 256.0, -7.0)));
        return true;
    }
}
