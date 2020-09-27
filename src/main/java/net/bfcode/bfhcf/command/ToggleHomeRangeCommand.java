package net.bfcode.bfhcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.CC;

public class ToggleHomeRangeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage(CC.translate("&4No Console."));
    		return true;
    	}
    	Player player = (Player) sender;
    	FactionUser factionUser = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId());
    	if(factionUser.getHomeRangeMode() == true) {
    		factionUser.setHomeRangeMode(false);
    		player.sendMessage(CC.translate("&cHas desactivado el Home Range en la Scoreboard!."));
    	} else {
    		factionUser.setHomeRangeMode(true);
    		player.sendMessage(CC.translate("&aHas activado el Home Range en la Scoreboard!."));
    	}
		return true;
    }

}
