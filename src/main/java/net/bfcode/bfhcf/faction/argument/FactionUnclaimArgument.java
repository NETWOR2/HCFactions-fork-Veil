package net.bfcode.bfhcf.faction.argument;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import com.google.common.collect.ImmutableList;

import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

public class FactionUnclaimArgument extends CommandArgument
{
    @SuppressWarnings("unused")
	private static ImmutableList<String> COMPLETIONS;
    private static HashSet<String> stuff;
    private HCFaction plugin;
    
    public FactionUnclaimArgument(HCFaction plugin) {
        super("unclaim", "Unclaims land from your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " ";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
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
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to unclaim land.");
            return true;
        }
        Set<Claim> factionClaims = playerFaction.getClaims();
        if (factionClaims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your faction does not own any claims.");
            return true;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("yes") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                for (Claim claims : factionClaims) {
                    playerFaction.removeClaim(claims, (CommandSender)player);
                }
                factionClaims.clear();
                return true;
            }
            if (args[1].equalsIgnoreCase("no") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                FactionUnclaimArgument.stuff.remove(player.getName());
                player.sendMessage(ChatColor.YELLOW + "You have been removed the unclaim-set.");
                return true;
            }
        }
        FactionUnclaimArgument.stuff.add(player.getName());
        new Text(ChatColor.YELLOW + "Do you want to unclaim " + ChatColor.BOLD + "all" + ChatColor.YELLOW + " of your land?").send((CommandSender)player);
        new Text(ChatColor.YELLOW + "If so, " + ChatColor.DARK_GREEN + "/f unclaim yes" + ChatColor.YELLOW + " otherwise do" + ChatColor.DARK_RED + " /f unclaim no" + ChatColor.GRAY + " (Click here to unclaim)").setHoverText(ChatColor.GOLD + "Click here to unclaim all").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send((CommandSender)player);
        return true;
    }
    
    static {
        stuff = new HashSet<String>();
        COMPLETIONS = ImmutableList.of("all");
    }
}
