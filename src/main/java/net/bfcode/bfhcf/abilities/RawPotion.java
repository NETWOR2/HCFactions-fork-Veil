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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class RawPotion implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;

	public RawPotion() {
		RawPotion.cooldown = new HashMap<String, Long>();
	}

	static {
		RawPotion.cooldown = new HashMap<String, Long>();
		RawPotion.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.RAW-POTION.NAME"));
		RawPotion.soundString = AbilitysFile.getConfig().getString("ABILITIES.RAW-POTION.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return RawPotion.cooldown.containsKey(player.getName())
				&& RawPotion.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (RawPotion.cooldown.containsKey(player.getName())
				&& RawPotion.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = RawPotion.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static double getSeconds(final Player player) {
		if (RawPotion.cooldown.containsKey(player.getName())
				&& RawPotion.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = RawPotion.cooldown.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	@SuppressWarnings("deprecation")
	public static void getRawPotion(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(RawPotion.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.RAW-POTION.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.RAW-POTION.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.RAW-POTION.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(RawPotion.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(RawPotion.name + " &ahas been added to your inventory."));
			sender.sendMessage(
					CC.translate(RawPotion.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
		if (inHand.getItemMeta().getDisplayName().equals(RawPotion.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (RawPotion.cooldown.containsKey(event.getPlayer().getName()) && RawPotion.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = RawPotion.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.RAW-POTION.COOLDOWN-MESSAGE"))
						.replace("<RAW-POTION>", RawPotion.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
			if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
						.replace("<ABILITY>", RawPotion.name));
				player.updateInventory();
                return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
			RawPotion.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.COOLDOWN") * 1000));
			player.playSound(player.getLocation(), sound, 1F, 1F);
			this.giveEffect(player);
			this.decrementItemInHand(player);
			return;
		}
	}

	private void giveEffect(Player player) {
		int duration = AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.STRENGTH.DURATION");
		int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.STRENGTH.AMPLIFIER");
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, amplifier - 1));
		player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.RAW-POTION.PLAYER-MESSAGE"))
				.replace("<RAW-POTION>", RawPotion.name)
				.replace("<AMPLIFIER>", String.valueOf(amplifier)));
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
