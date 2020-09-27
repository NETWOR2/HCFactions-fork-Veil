package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.CommandExecutor;

public class HelpCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String messages : HCFaction.getPlugin().getConfig().getStringList("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages).replace
            		("%OVERWORLD%", HCFaction.getPlugin().getServerHandler().getWorldBorder() + "").replace
            		("%NETHER%", HCFaction.getPlugin().getServerHandler().getNetherBorder() + "").replace
            		("%END%", HCFaction.getPlugin().getServerHandler().getEndBorder() + "").replace
            		("%TEAMSPEAK%", ConfigurationService.TEAMSPEAK + "").replace
            		("%TWITTER%", ConfigurationService.TWITTER + "").replace
            		("%DISCORD%", ConfigurationService.DISCORD + "").replace
            		("%STORE%", ConfigurationService.STORE + "").replace
            		("%SERVER_NAME%", ConfigurationService.SERVER_NAME + "").replace
            		("%WEBSITE%", ConfigurationService.WEBSITE + "").replace
            		("*", "\u2a20"));
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
