package net.bfcode.bfhcf.command.reclaim.argument;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetClaimCommand
  implements CommandExecutor
{
  private HCFaction plugin;
  
  public SetClaimCommand(HCFaction plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "Only player can execute this command.");
      return true;
    }
    if (args.length == 0)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /setclaim <player> <boolean>");
      return true;
    }
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Please enter either True or False.");
      return true;
    }
    if (Bukkit.getPlayer(args[0]) == null)
    {
      sender.sendMessage(ChatColor.RED + "Player does not exist or isn't online.");
      return true;
    }
    Player target = Bukkit.getPlayer(args[0]);
    Boolean b = Boolean.valueOf(Boolean.parseBoolean(args[1].toUpperCase()));
    FactionUser targetProfile = this.plugin.getUserManager().getUser(target.getUniqueId());
    targetProfile.setReclaimed(b.booleanValue());
    sender.sendMessage(ChatColor.YELLOW + target.getName() + "'s reclaim has been set to " + ChatColor.GOLD + b);
    return false;
  }
}
