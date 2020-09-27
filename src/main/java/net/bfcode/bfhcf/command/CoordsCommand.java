package net.bfcode.bfhcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.CommandExecutor;

public class CoordsCommand implements CommandExecutor
{
    @SuppressWarnings("unused")
	private HCFaction plugin;
    
    public CoordsCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        for(String message : HCFaction.getPlugin().getConfig().getStringList("COORDS_COMMAND")) {
        	sender.sendMessage(CC.translate(message
        			.replace("%line%", CC.CHAT_BAR)));
        }
        return true;
    }
}
