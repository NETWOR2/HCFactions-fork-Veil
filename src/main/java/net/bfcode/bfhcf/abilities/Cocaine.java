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

public class Cocaine implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;

	public Cocaine() {
		Cocaine.cooldown = new HashMap<String, Long>();
	}

	static {
		Cocaine.cooldown = new HashMap<String, Long>();
		Cocaine.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.COCAINE.NAME"));
		Cocaine.soundString = AbilitysFile.getConfig().getString("ABILITIES.COCAINE.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return Cocaine.cooldown.containsKey(player.getName())
				&& Cocaine.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (Cocaine.cooldown.containsKey(player.getName())
				&& Cocaine.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = Cocaine.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static double getSeconds(final Player player) {
		if (Cocaine.cooldown.containsKey(player.getName())
				&& Cocaine.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = Cocaine.cooldown.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	@SuppressWarnings("deprecation")
	public static void getCocaine(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Cocaine.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.COCAINE.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.COCAINE.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.COCAINE.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Cocaine.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Cocaine.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(Cocaine.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
		if (inHand.getItemMeta().getDisplayName().equals(Cocaine.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (Cocaine.cooldown.containsKey(event.getPlayer().getName()) && Cocaine.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = Cocaine.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.COCAINE.COOLDOWN-MESSAGE"))
						.replace("<COCAINE>", Cocaine.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
			if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
						.replace("<ABILITY>", Cocaine.name));
				player.updateInventory();
                return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
			Cocaine.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.COOLDOWN") * 1000));
			player.playSound(player.getLocation(), sound, 1F, 1F);
			this.giveEffect(player);
			this.decrementItemInHand(player);
			return;
		}
	}

	private void giveEffect(Player player) {
		int _speed_duration = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.SPEED.DURATION");
		int _resistance_duration = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.RESISTANCE.DURATION");
		int _strength_duration = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.STRENGTH.DURATION");
		int _speed_amplifier = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.SPEED.AMPLIFIER");
		int _resistance_amplifier = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.RESISTANCE.AMPLIFIER");
		int _strength_amplifier = AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.STRENGTH.AMPLIFIER");
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, _speed_duration * 20, _speed_amplifier - 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, _resistance_duration * 20, _resistance_amplifier - 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, _strength_duration * 20, _strength_amplifier - 1));
		player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.COCAINE.PLAYER-MESSAGE"))
				.replace("<COCAINE>", Cocaine.name));
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
