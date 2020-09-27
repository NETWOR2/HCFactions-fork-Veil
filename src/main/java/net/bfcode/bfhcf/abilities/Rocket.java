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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class Rocket implements Listener {
	
	public static Map<String, Long> cooldown;
	public static Map<String, Long> fall;
	public static String name;
	public static String soundString;
  
	public Rocket() {
		Rocket.cooldown = new HashMap<>();
		Rocket.fall = new HashMap<>();
	}
  
	static {
		Rocket.cooldown = new HashMap<>();
		Rocket.fall = new HashMap<>();
		Rocket.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.ROCKET.NAME"));
		Rocket.soundString = AbilitysFile.getConfig().getString("ABILITIES.ROCKET.SOUND").toUpperCase();
	}
  
	public static boolean isOnCooldown(final Player player) {
		return Rocket.cooldown.containsKey(player.getName())
				&& Rocket.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (Rocket.cooldown.containsKey(player.getName())
				&& Rocket.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}
  
	public static String getCooldown(final Player player) {
		final long millisLeft = Rocket.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	public static boolean isOnCooldownFall(final Player player) {
		return Rocket.fall.containsKey(player.getName())
				&& Rocket.fall.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldownFall(final Player player) {
		if (Rocket.fall.containsKey(player.getName())
				&& Rocket.fall.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}
  
	public static String getCooldownFall(final Player player) {
		final long millisLeft = Rocket.fall.get(player.getName()) - System.currentTimeMillis();
		final double value = millisLeft / 1000.0;
		final double sec = Math.round(10.0 * value) / 10.0;
		return CC.translate("&f" + sec + "s");
	}
	
	@SuppressWarnings("deprecation")
	public static void getRocket(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.ROCKET.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.ROCKET.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Rocket.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.ROCKET.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.ROCKET.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.ROCKET.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.ROCKET.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Rocket.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Rocket.name + " &ahas been added to your inventory."));
			sender.sendMessage(
					CC.translate(Rocket.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
		if (inHand.getItemMeta().getDisplayName().equals(Rocket.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (Rocket.cooldown.containsKey(event.getPlayer().getName()) && Rocket.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = Rocket.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.ROCKET.COOLDOWN-MESSAGE"))
						.replace("<ROCKET>", Rocket.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
			if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
						.replace("<ABILITY>", Rocket.name));
				player.updateInventory();
                return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
			Rocket.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ROCKET.COOLDOWN") * 1000));
			Rocket.fall.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
			player.setVelocity(new Vector(0.1D, 2.0D, 0.0D));
			player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
			decrementItemInHand(player);
			return;
		}
	}
	
	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause().equals(DamageCause.FALL)) {
				if (Rocket.isOnCooldownFall(player)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
  
	private void decrementItemInHand(Player player) {
		ItemStack itemStack = player.getItemInHand();
		if (itemStack.getAmount() <= 1) {
			player.setItemInHand(new ItemStack(Material.AIR, 1));
		} else {
			itemStack.setAmount(itemStack.getAmount() - 1);
		}
	}
}