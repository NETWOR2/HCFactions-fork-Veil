package net.bfcode.bfhcf.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.InventoryMaker;
import net.bfcode.bfhcf.utils.ItemMaker;

public class InteractListener implements Listener {
	
	@EventHandler
	public void onCouldron(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if(!HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if(block.getType() != Material.CAULDRON) {
			return;
		}
    	ItemStack health_potion = new ItemMaker(Material.POTION).data((short)16421).create();
    	ItemStack speed_potion = new ItemMaker(Material.POTION).data((short)8226).create();
    	ItemStack fireres_potion = new ItemMaker(Material.POTION).data((short)8259).create();
    	ItemStack enderpearl = new ItemMaker(Material.ENDER_PEARL, 16).create();
    	ItemStack gold_sword = new ItemMaker(Material.GOLD_SWORD).create();
        player.openInventory(new InventoryMaker(null, 5, "Refill")
        		.setItem(0, fireres_potion)
        		.setItem(1, health_potion)
        		.setItem(2, health_potion)
        		.setItem(3, health_potion)
        		.setItem(4, health_potion)
        		.setItem(5, health_potion)
        		.setItem(6, health_potion)
        		.setItem(7, health_potion)
        		.setItem(8, gold_sword)
        		.setItem(9, fireres_potion)
        		.setItem(10, health_potion)
        		.setItem(11, health_potion)
        		.setItem(12, health_potion)
        		.setItem(13, health_potion)
        		.setItem(14, health_potion)
        		.setItem(15, health_potion)
        		.setItem(16, health_potion)
        		.setItem(17, gold_sword)
        		.setItem(18, speed_potion)
        		.setItem(19, health_potion)
        		.setItem(20, health_potion)
        		.setItem(21, health_potion)
        		.setItem(22, health_potion)
        		.setItem(23, health_potion)
        		.setItem(24, health_potion)
        		.setItem(25, health_potion)
        		.setItem(26, gold_sword)
        		.setItem(27, speed_potion)
        		.setItem(28, health_potion)
        		.setItem(29, health_potion)
        		.setItem(30, health_potion)
        		.setItem(31, health_potion)
        		.setItem(32, health_potion)
        		.setItem(33, health_potion)
        		.setItem(34, health_potion)
        		.setItem(35, gold_sword)
        		.setItem(36, speed_potion)
        		.setItem(37, health_potion)
        		.setItem(38, health_potion)
        		.setItem(39, health_potion)
        		.setItem(40, health_potion)
        		.setItem(41, health_potion)
        		.setItem(42, health_potion)
        		.setItem(43, health_potion)
        		.setItem(44, enderpearl)
        		.create());
	}

}
