package net.bfcode.bfhcf.command.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesCheckArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public LivesCheckArgument(HCFaction plugin) {
        super("check", "Check Lives");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " [playerName]";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        OfflinePlayer target;
        if (args.length > 1) {
            target = Bukkit.getOfflinePlayer(args[1]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found.");
            return true;
        }
        int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " has " + ChatColor.GOLD + targetLives + ChatColor.YELLOW + ' ' + ((targetLives == 1) ? "life" : "lives") + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
