package net.bfcode.bfhcf.destroythecore;

import java.util.Iterator;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.CC;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class DTCListener implements Listener {

	DTCFile dtcFile;

	public DTCListener(HCFaction plugin) {
		this.dtcFile = DTCFile.getConfig();
	}

	@EventHandler
	public void onPlayerSelect(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		int x = event.getBlock().getX();
		int y = event.getBlock().getY();
		int z = event.getBlock().getZ();
		if (DTCHandler.isDTCWand(player)) {
			if (block.getType() == Material.OBSIDIAN) {
				event.setCancelled(true);
				DTCHandler.setCurrentSelection(x, y, z);
				player.sendMessage(
						CC.translate("&eYou have select the core &a(&c" + x + "&a, &c" + y + "&a, &c" + z + "&a)"));
			} else {
				event.setCancelled(true);
				player.sendMessage("asd");
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Iterator<String> iterator = this.dtcFile.getConfigurationSection("CurrentDTC").getKeys(false).iterator();
		if (iterator.hasNext()) {
			String dtc = iterator.next();
			if (block.getLocation().getBlockX() == this.dtcFile.getInt("DTC." + dtc + ".X")
					&& block.getLocation().getBlockY() == this.dtcFile.getInt("DTC." + dtc + ".Y")
					&& block.getLocation().getBlockZ() == this.dtcFile.getInt("DTC." + dtc + ".Z")
					&& block.getType().equals(Material.OBSIDIAN)) {
				event.setCancelled(true);
				DTCHandler.decrementPoints(dtc);
				Bukkit.broadcastMessage(CC
						.translate(HCFaction.getPlugin().getConfig().getString("DestroyTheCore.Destroyed")
								.replace("%playername%", player.getName()).replace("%destroythecore%", dtc))
						.replace("%points%", "" + DTCHandler.getDTCPoints(dtc)));
				if (DTCHandler.getDTCPoints(dtc) == 0) {
					DTCHandler.setDTCEvent(dtc, false);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_DTC").replace("%player%", player.getName()));
					Bukkit.broadcastMessage(
							CC.translate(HCFaction.getPlugin().getConfig().getString("DestroyTheCore.DestroyedCore")
									.replace("%playername%", player.getName()).replace("%destroythecore%", dtc)));
				}
			}
			return;
		}
		if (player.getGameMode().equals(GameMode.CREATIVE) && player.isOp()) {
			return;
		}
	}
}
