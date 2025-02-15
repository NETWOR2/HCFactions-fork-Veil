package net.bfcode.bfhcf.faction.argument;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

public class FactionAnnouncementArgument extends CommandArgument
{
    private static ImmutableList<String> CLEAR_LIST;
    private HCFaction plugin;
    
    public FactionAnnouncementArgument(HCFaction plugin) {
        super("announcement", "Set your faction announcement.", new String[] { "announce" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <newAnnouncement>";
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
            sender.sendMessage(ChatColor.RED + "You must be a officer to edit the faction announcement.");
            return true;
        }
        String oldAnnouncement = playerFaction.getAnnouncement();
        String newAnnouncement = (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("remove")) ? null : StringUtils.join((Object[])args, ' ', 1, args.length);
        if (oldAnnouncement == null && newAnnouncement == null) {
            sender.sendMessage(ChatColor.RED + "Your factions' announcement is already unset.");
            return true;
        }
        if (oldAnnouncement != null && newAnnouncement != null && oldAnnouncement.equals(newAnnouncement)) {
            sender.sendMessage(ChatColor.RED + "Your factions' announcement is already " + newAnnouncement + '.');
            return true;
        }
        playerFaction.setAnnouncement(newAnnouncement);
        if (newAnnouncement == null) {
            playerFaction.broadcast(ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has cleared the factions' announcement.");
            return true;
        }
        playerFaction.broadcast(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has updated the factions' announcement from " + ChatColor.GREEN + ((oldAnnouncement != null) ? oldAnnouncement : "none") + ChatColor.YELLOW + " to " + ChatColor.GREEN + newAnnouncement + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length == 2) {
            return (List<String>)FactionAnnouncementArgument.CLEAR_LIST;
        }
        return Collections.emptyList();
    }
    
    static {
        CLEAR_LIST = ImmutableList.of("clear");
    }
}
