package net.bfcode.bfhcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.Utils;

public class SetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if(args.length == 0) {
    		player.sendMessage(CC.translate(CC.CHAT_BAR));
    		player.sendMessage(CC.translate("&6&lSet Command"));
    		player.sendMessage(CC.translate(""));
    		player.sendMessage(CC.translate("&e/set endexit &6» &7Create location for exit end."));
    		player.sendMessage(CC.translate(CC.CHAT_BAR));
    		return true;
        }
		if(args[0].equalsIgnoreCase("endexit")) {
            HCFaction.getPlugin().getConfig().set("Locations.end_exit", Utils.stringifyLocation(player.getLocation()));
            HCFaction.getPlugin().saveConfig();
            sender.sendMessage(CC.translate("&aSuccessfully created endexit."));
            return true;
		}
		return true;
	}

}
