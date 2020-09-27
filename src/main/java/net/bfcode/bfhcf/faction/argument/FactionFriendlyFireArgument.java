package net.bfcode.bfhcf.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

public  class FactionFriendlyFireArgument extends CommandArgument {

    private HCFaction plugin;

    public FactionFriendlyFireArgument(HCFaction plugin) {
        super("ff", "Activate or remove a pvp in members on this faction.", new String[] { "friendlyfire" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    
    }
    
	@SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction caption to do this.");
            return true;
        }
        boolean newFriendlyFire = !playerFaction.isFriendlyFire();
        playerFaction.setFriendlyFire(newFriendlyFire);
        playerFaction.broadcast(ChatColor.YELLOW  + "Friendly Fire has been " + (newFriendlyFire ? ChatColor.GREEN + "activated" : ChatColor.RED + "disabled") + ChatColor.YELLOW + " for " + ChatColor.LIGHT_PURPLE + sender.getName());
		return true;
	}
}
	
