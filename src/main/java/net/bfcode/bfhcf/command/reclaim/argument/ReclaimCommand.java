package net.bfcode.bfhcf.command.reclaim.argument;

import net.bfcode.bfhcf.HCFaction;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReclaimCommand
  implements CommandExecutor
{
  private HCFaction plugin;
  
  public ReclaimCommand(HCFaction plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	  
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Only player can execute this command.");
      return true;
    }
    
    if (args.length == 0) {
      this.plugin.getReclaimManager().getReclaim((Player)sender);
    }
    
    return false;
  	}
}
