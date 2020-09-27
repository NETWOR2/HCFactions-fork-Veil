package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.util.CC;

public class BlockTNTCartListener implements Listener {
	
	@EventHandler
	public void onBlockPlace(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		ItemStack air = new ItemStack(Material.AIR);
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if (item.getType().equals(Material.EXPLOSIVE_MINECART)) {
				event.setCancelled(true);
				player.sendMessage(CC.translate("&cLos " + item.getType() + " estan prohibidos!"));
				player.setItemInHand(air);
				player.updateInventory();
				return;
			}
		}
	}
}
