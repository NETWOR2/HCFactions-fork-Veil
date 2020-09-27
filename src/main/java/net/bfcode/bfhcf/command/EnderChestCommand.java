package net.bfcode.bfhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class EnderChestCommand implements CommandExecutor
{
    @SuppressWarnings("unused")
	private HCFaction plugin;
    
    public EnderChestCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return false;
        }
        Player target;
        Player player = target = (Player)sender;
        if (player.hasPermission("hcf.command.enderchest.others") && args.length != 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found or isn't online.");
                return true;
            }
        }
        if(FFACommand.mode == true) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in FFA Mode."));
        	return true;
        }
        player.openInventory(target.getEnderChest());
        player.sendMessage(CC.translate("&eOpened " + (player.equals(target) ? "your" : (target.getName() + "'s")) + " player vault."));
        return true;
    }
}
