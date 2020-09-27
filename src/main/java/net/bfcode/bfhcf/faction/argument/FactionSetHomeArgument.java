package net.bfcode.bfhcf.faction.argument;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetHomeArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionSetHomeArgument(HCFaction plugin) {
        super("sethome", "Sets the faction home location.");
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
        FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to set the home.");
            return true;
        }
        Location location = player.getLocation();
        boolean insideTerritory = false;
        for (Claim claim : playerFaction.getClaims()) {
            if (!claim.contains(location)) {
                continue;
            }
            insideTerritory = true;
            break;
        }
        if (!insideTerritory) {
            player.sendMessage(ChatColor.RED + "You may only set your home in your territory.");
            return true;
        }
        playerFaction.setHome(location);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has set the faction home.");
        return true;
    }
}
