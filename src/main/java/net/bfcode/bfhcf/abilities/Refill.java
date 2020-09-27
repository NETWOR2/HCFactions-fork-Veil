package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class Refill implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;

	public Refill() {
		Refill.cooldown = new HashMap<String, Long>();
	}

	static {
		Refill.cooldown = new HashMap<String, Long>();
		Refill.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.REFILL.NAME"));
		Refill.soundString = AbilitysFile.getConfig().getString("ABILITIES.REFILL.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return Refill.cooldown.containsKey(player.getName())
				&& Refill.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (Refill.cooldown.containsKey(player.getName())
				&& Refill.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = Refill.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getRefill(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.REFILL.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.REFILL.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Refill.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.REFILL.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.REFILL.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.REFILL.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.REFILL.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Refill.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Refill.name + " &ahas been added to your inventory."));
			sender.sendMessage(
					CC.translate(Refill.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack inHand = player.getItemInHand();
		Sound sound = Sound.valueOf(Sound.class, soundString);
		if ((inHand == null) || (inHand.getType().equals(Material.AIR)) || (inHand.getItemMeta().getDisplayName() == null)) {
			return;
		}
		if (inHand.getItemMeta().getDisplayName().equals(Refill.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (Refill.cooldown.containsKey(event.getPlayer().getName()) && Refill.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = Refill.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.REFILL.COOLDOWN-MESSAGE"))
						.replace("<REFILL>", Refill.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			if (player.getInventory().firstEmpty() < 0) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.REFILL.PLAYER-INV-FULL")));
				player.updateInventory();
				return;
			}
			Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
			if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
						.replace("<ABILITY>", Refill.name));
				player.updateInventory();
                return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
			Refill.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.REFILL.COOLDOWN") * 1000));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.REFILL.PLAYER-MESSAGE"))
					.replace("<REFILL>", Refill.name));
			player.playSound(player.getLocation(), sound, 1F, 1F);
			this.decrementItemInHand(player);
			this.giveRefill(player);
			player.updateInventory();
			return;
		}
	}

	private void giveRefill(Player player) {
		ItemStack[] inv = player.getInventory().getContents();
		ItemStack item = new ItemStack(Material.POTION, 1, (short) 16421);
		ItemStack inHand = player.getItemInHand();
		if (inHand != null && inHand.getType().equals(Material.AIR)) {
			player.getInventory().setItemInHand(new ItemStack(Material.POTION, 1, (short) 16421));
		}
		for (int i = 0; i < inv.length; i++) {
			player.getInventory().addItem(item);
			player.updateInventory();
		}
	}

	private void decrementItemInHand(Player player) {
		final ItemStack itemStack = player.getItemInHand();
		if (itemStack.getAmount() <= 1) {
			player.setItemInHand(new ItemStack(Material.AIR, 1));
		} else {
			itemStack.setAmount(itemStack.getAmount() - 1);
		}
	}
}
