package net.bfcode.bfhcf.faction.argument;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.timer.type.StuckTimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionStuckArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionStuckArgument(HCFaction plugin) {
        super("stuck", "Teleport to a safe position.", new String[] { "trap", "trapped" });
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
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return true;
        }
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
            return true;
        }
        StuckTimer stuckTimer = this.plugin.getTimerManager().stuckTimer;
        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getDisplayName() + ChatColor.RED + " timer is already active.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + stuckTimer.getDisplayName() + ChatColor.YELLOW + " timer has started. Teleportation will commence in " + ChatColor.LIGHT_PURPLE + HCFaction.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". This will cancel if you move more than " + 5 + " blocks.");
        return true;
    }
}
