package net.bfcode.bfhcf.command.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;

import net.bfcode.bfbase.BaseConstants;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesSetArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public LivesSetArgument(HCFaction plugin) {
        super("set", "Set the lives of a player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <playerName> <amount>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        Integer amount = JavaUtil.tryParseInt(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        this.plugin.getDeathbanManager().setLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + ((amount == 1) ? " life" : " lives") + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
