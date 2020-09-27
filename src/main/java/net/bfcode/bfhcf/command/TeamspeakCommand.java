package net.bfcode.bfhcf.command;

import net.bfcode.bfhcf.utils.ConfigurationService;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class TeamspeakCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Teamspeak" + ChatColor.GRAY  + " \u2a20 " + ChatColor.AQUA + ConfigurationService.TEAMSPEAK);
        return true;
    }
}
