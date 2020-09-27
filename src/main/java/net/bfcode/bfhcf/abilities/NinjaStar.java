package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class NinjaStar implements Listener {

	public static Map<String, Long> cooldown;
	public static Map<String, Long> teleport;
	public static String name;
	public static String soundString;
	public Location loc;
	public Player target;

	public NinjaStar() {
		NinjaStar.cooldown = new HashMap<String, Long>();
		NinjaStar.teleport = new HashMap<String, Long>();
	}

	static {
		NinjaStar.cooldown = new HashMap<String, Long>();
		NinjaStar.teleport = new HashMap<String, Long>();
		NinjaStar.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.NINJA-STAR.NAME"));
		NinjaStar.soundString = AbilitysFile.getConfig().getString("ABILITIES.NINJA-STAR.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return NinjaStar.cooldown.containsKey(player.getName())
				&& NinjaStar.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (NinjaStar.cooldown.containsKey(player.getName())
				&& NinjaStar.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = NinjaStar.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static double getSeconds(final Player player) {
		if (NinjaStar.cooldown.containsKey(player.getName())
				&& NinjaStar.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = NinjaStar.cooldown.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	public static boolean isOnCooldownTeleport(final Player player) {
		return NinjaStar.teleport.containsKey(player.getName())
				&& NinjaStar.teleport.get(player.getName()) > System.currentTimeMillis();
	}

	public static String getCooldownTeleport(final Player player) {
		final long millisLeft = NinjaStar.teleport.get(player.getName()) - System.currentTimeMillis();
		final double value = millisLeft / 1000.0;
		final double sec = Math.round(10.0 * value) / 10.0;
		return CC.translate("&f" + sec + "s");
	}

	public static Boolean hasCooldownTeleport(final Player player) {
		if (NinjaStar.teleport.containsKey(player.getName())
				&& NinjaStar.teleport.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static double getSecondsTeleport(final Player player) {
		if (NinjaStar.teleport.containsKey(player.getName())
				&& NinjaStar.teleport.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = NinjaStar.teleport.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	@SuppressWarnings("deprecation")
	public static void getNinjaStar(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.NINJA-STAR.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.NINJA-STAR.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(NinjaStar.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.NINJA-STAR.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.NINJA-STAR.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.NINJA-STAR.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.NINJA-STAR.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(NinjaStar.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(NinjaStar.name + " &ahas been added to your inventory."));
			sender.sendMessage(
					CC.translate(NinjaStar.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();
			NinjaStar.teleport.put(victim.getName(), System.currentTimeMillis() + (10 * 1000));
			loc = damager.getLocation();
			target = damager;
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack inHand = player.getItemInHand();
		if (NinjaStar.isOnCooldownTeleport(player)) {
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (inHand.getType().equals(Material.AIR) || inHand.getItemMeta().getDisplayName() == null) {
					return;
				}
				if (inHand.getItemMeta().getDisplayName().equals(NinjaStar.name) && inHand.getItemMeta().hasLore()) {
					event.setCancelled(true);
					if (NinjaStar.cooldown.containsKey(player.getName()) && NinjaStar.cooldown.get(player.getName()) > System.currentTimeMillis()) {
						long millisLeft = NinjaStar.cooldown.get(player.getName()) - System.currentTimeMillis();
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.NINJA-STAR.COOLDOWN-MESSAGE"))
								.replace("<NINJA-STAR>", NinjaStar.name)
								.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
						player.updateInventory();
						return;
					}
					Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
					if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", NinjaStar.name));
						player.updateInventory();
		                return;
					}
					if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(player)) {
						return;
					}
	            	if(FFACommand.mode == true) {
	            		return;
	            	}
					NinjaStar.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.NINJA-STAR.COOLDOWN") * 1000));
					player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.NINJA-STAR.PLAYER-MESSAGE"))
							.replace("<NINJA-STAR>", NinjaStar.name)
							.replace("<TARGET>", target.getName()));
					target.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.NINJA-STAR.TARGET-MESSAGE"))
							.replace("<NINJA-STAR>", NinjaStar.name)
							.replace("<PLAYER>", player.getName()));
					player.teleport(loc);
					player.playEffect(player.getLocation().add(0.5D, 2.0D, 0.5D), Effect.ENDER_SIGNAL, 5);
					player.playEffect(player.getLocation().add(0.5D, 1.5D, 0.5D), Effect.ENDER_SIGNAL, 5);
					player.playEffect(player.getLocation().add(0.5D, 1.0D, 0.5D), Effect.ENDER_SIGNAL, 5);
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 1F);
					this.decrementItemInHand(player);
					player.updateInventory();
					return;
				}
			}
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
