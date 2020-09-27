package net.bfcode.bfhcf.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.inventory.PlayerInventory;

import net.bfcode.bfhcf.utils.CC;

import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class ClearInventoryListener implements Listener {
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign)event.getClickedBlock().getState();
            if (sign.getLine(1).contains("Clear")) {
            	PlayerInventory inv = player.getInventory();
                inv.clear();
                player.updateInventory();
                player.sendMessage(CC.translate("&aTu inventario se ha borrado exitosamente!"));
                return;
            }
        }
    }
}
