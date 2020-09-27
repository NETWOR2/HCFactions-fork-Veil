package net.bfcode.bfhcf.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.CC;

public class AutoFeedCommand implements CommandExecutor, Listener {
	
	public static HashMap<Player, String> mode;
	
	static {
		mode = new HashMap<Player, String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(mode.containsKey(player)) {
			player.sendMessage(CC.translate("&bAutoFeed ha sido &cdesactivado"));
			mode.remove(player);
			return true;
		} else if(!mode.containsKey(player)) {
			mode.put(player, player.getName());
			player.sendMessage(CC.translate("&bAutoFeed ha sido &aactivado"));
			player.setFoodLevel(20);
			return true;
		}
		
		return true;
	}
	
	@EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		if(mode.containsKey(player) || HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
			event.setCancelled(true);
			return;
		} else if(!mode.containsKey(event.getEntity())) {
			event.setCancelled(false);
			return;
		}
    }
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if(mode.containsKey(event.getPlayer())) {
			mode.remove(event.getPlayer());
		} else {
			return;
		}
	}

}
