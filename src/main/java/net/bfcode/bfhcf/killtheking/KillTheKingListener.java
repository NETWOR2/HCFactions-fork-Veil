package net.bfcode.bfhcf.killtheking;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KillTheKingListener implements Listener {
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (KillTheKingManager.isActive()) {
			if (player == KillTheKingManager.getKing()) {
				event.getDrops().clear();
				KillTheKingManager.setActive(false);
				if (player.getKiller() == null) {
					KillTheKingManager.stopMessage();
					return;
				}
				KillTheKingManager.kingDeathMessage(KillTheKingManager.getKing(), player.getKiller());
			}
		}
	}
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if(KillTheKingManager.isActive()) {
			if(player == KillTheKingManager.getKing()) {
				event.setCancelled(true);
			}
 		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (KillTheKingManager.isActive()) {
			if (player == KillTheKingManager.getKing()) {
				KillTheKingManager.clearInventory(KillTheKingManager.getKing());
				KillTheKingManager.stopMessage();
				KillTheKingManager.setActive(false);
			}
 		}
	}
}
