package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
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
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class BelchBomb implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;
	public static int radius;

	public BelchBomb() {
		BelchBomb.cooldown = new HashMap<String, Long>();
	}

	static {
		BelchBomb.cooldown = new HashMap<String, Long>();
		BelchBomb.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.NAME"));
		BelchBomb.soundString = AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.SOUND").toUpperCase();
		BelchBomb.radius = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.RADIUS");
	}

	public static boolean isOnCooldown(final Player player) {
		return BelchBomb.cooldown.containsKey(player.getName())
				&& BelchBomb.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (BelchBomb.cooldown.containsKey(player.getName())
				&& BelchBomb.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = BelchBomb.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getBelchBomb(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(BelchBomb.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.BELCH-BOMB.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.BELCH-BOMB.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.BELCH-BOMB.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(BelchBomb.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(BelchBomb.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(BelchBomb.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
		if (inHand.getItemMeta().getDisplayName().equals(BelchBomb.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (BelchBomb.cooldown.containsKey(event.getPlayer().getName()) && BelchBomb.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = BelchBomb.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.COOLDOWN-MESSAGE"))
						.replace("<BELCH-BOMB>", BelchBomb.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			for (Entity entity : player.getNearbyEntities(BelchBomb.radius - 1, BelchBomb.radius - 1, BelchBomb.radius - 1)) {
				if (entity instanceof Player) {
					Player target = (Player) entity;
					Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
					Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(target.getLocation());
					if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", BelchBomb.name));
						player.updateInventory();
	                    return;
					}
					if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt2 instanceof ConquestFaction) {
						player.updateInventory();
	                    return;
					}
					if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()) != null && HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()) != null) {
						PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
						PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId());
						if(playerFaction.equals(victimFaction)) {
							return;
						}
					}
					if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(player)) {
						return;
					}
					if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(target)) {
						return;
					}
	            	if(FFACommand.mode == true) {
	            		return;
	            	}
					BelchBomb.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.COOLDOWN") * 1000));
					player.playSound(player.getLocation(), sound, 1F, 1F);
					this.giveEffect(target);
					this.decrementItemInHand(player);
					player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.PLAYER-MESSAGE"))
							.replace("<BELCH-BOMB>", BelchBomb.name)
							.replace("<TARGET>", target.getName()));
					target.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.TARGET-MESSAGE"))
							.replace("<BELCH-BOMB>", BelchBomb.name)
							.replace("<PLAYER>", player.getName()));
					return;
				}
			}
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.BELCH-BOMB.RADIUS-MESSAGE")).replace("<RADIUS>", String.valueOf(BelchBomb.radius)));
			return;
		}
	}

	private void giveEffect(Player target) {
		int _blindness_duration = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.BLINDNESS.DURATION");
		int _resistance_duration = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.SLOWNESS.DURATION");
		int _blindness_amplifier = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.BLINDNESS.AMPLIFIER");
		int _resistance_amplifier = AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.SLOWNESS.AMPLIFIER");
		target.removePotionEffect(PotionEffectType.BLINDNESS);
		target.removePotionEffect(PotionEffectType.SLOW);
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, _blindness_duration * 20, _blindness_amplifier - 1));
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, _resistance_duration * 20, _resistance_amplifier - 1));
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
