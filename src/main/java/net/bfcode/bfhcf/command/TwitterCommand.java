package net.bfcode.bfhcf.command;

import net.bfcode.bfhcf.utils.ConfigurationService;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class TwitterCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Twitter" + ChatColor.GRAY  + " \u2a20 " + ChatColor.YELLOW + ConfigurationService.TWITTER);
        return true;
    }
}
