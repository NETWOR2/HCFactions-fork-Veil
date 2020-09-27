package net.bfcode.bfhcf.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;

public class BeaconListener implements Listener {

	HashMap<String, Long> cooldown = new HashMap<String, Long>();
	Random rand = new Random();
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		Block block = (Block) event.getClickedBlock();
		int cooldownTime = 60;
		if (!(HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation()).isSafezone())) {
			return;
		}
		if(event.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		if (block.getType() != Material.BEACON) {
			return;
		}
		if (player.getItemInHand().getType() != Material.DIAMOND_SWORD) {
			player.sendMessage(ChatColor.RED + "This function only work using Diamond Sword in the hand!");
			return;
		}
		if (cooldown.containsKey(player.getName())) {
			long secondsLeft = ((cooldown.get(player.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
			if (secondsLeft > 0) {
				player.sendMessage(ChatColor.RED + "You can´t use this for " + secondsLeft + " seconds!");
				return;
			}
		}
		cooldown.put(player.getName(), System.currentTimeMillis());
		randomName(player);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 2, 1);
		return;
	}
	
	public void randomName(Player player) {
		ItemStack item = player.getItemInHand();
		ItemMeta meta = item.getItemMeta();
		if (player.getItemInHand().getType() != Material.DIAMOND_SWORD) {
			player.sendMessage(ChatColor.RED + "You need a Diamond Sword in hand for use currently");
			return;
		}
		List<String> names = HCFaction.getPlugin().getConfig().getStringList("beacon-random-names");
		String randomly = get(names);

		meta.setDisplayName(CC.translate(randomly));
		item.setItemMeta(meta);
		player.sendMessage(CC.translate("&eNow your sword rename is &6'" + randomly + "'"));
	}
	
	private String get(List<String> words) {
	    return words.get(new Random().nextInt(words.size()));
	}
}
