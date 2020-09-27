package net.bfcode.bfhcf.command.lives.argument;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesClearDeathbansArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public LivesClearDeathbansArgument(HCFaction plugin) {
        super("cleardeathbans", "Clears the global deathbans");
        this.plugin = plugin;
        this.aliases = new String[] { "resetdeathbans" };
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.getName().equalsIgnoreCase("Risas")) || sender.getName().equalsIgnoreCase("JavaPinq")) {
            for (FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.removeDeathban();
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Must be console");
        return false;
    }
}
