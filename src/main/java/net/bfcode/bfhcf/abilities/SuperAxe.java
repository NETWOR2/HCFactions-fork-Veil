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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

public class SuperAxe implements Listener {

	public static Map<String, Long> cooldown;
	public static String victim_inv_full;
	public static String name;
	public static String soundString;

	static {
		SuperAxe.cooldown = new HashMap<String, Long>();
		SuperAxe.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.NAME"));
		SuperAxe.soundString = AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return SuperAxe.cooldown.containsKey(player.getName())
				&& SuperAxe.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public SuperAxe() {
		SuperAxe.cooldown = new HashMap<String, Long>();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = SuperAxe.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static Boolean hasCooldown(final Player player) {
		if (SuperAxe.cooldown.containsKey(player.getName())
				&& SuperAxe.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static void getSuperAxe(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.SUPER-AXE.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.SUPER-AXE.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(SuperAxe.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SUPER-AXE.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SUPER-AXE.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SUPER-AXE.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SUPER-AXE.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(SuperAxe.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(SuperAxe.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(SuperAxe.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();
			ItemStack helmet = victim.getInventory().getHelmet();
			ItemStack air = new ItemStack(Material.AIR);
			ItemStack inHand = damager.getItemInHand();
			Sound sound = Sound.valueOf(Sound.class, soundString);
			if (victim.getInventory().getHelmet() == null) {
				return;
			}
			if ((inHand == null) || (inHand.getType().equals(Material.AIR)) || (inHand.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (inHand.getItemMeta().getDisplayName().equals(SuperAxe.name) && inHand.getItemMeta().hasLore()) {
				if (victim.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET)) {
					if (SuperAxe.isOnCooldown(damager)) {
						return;
					}
					if (victim.getInventory().firstEmpty() < 0) {
						damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.TARGET-INV-FULL"))
								.replace("<TARGET>", victim.getName()));
						return;
					}
					Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damager.getLocation());
					Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(victim.getLocation());
					if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
						damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", SuperAxe.name));
						damager.updateInventory();
		                return;
					}
					if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt2 instanceof ConquestFaction) {
						damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", SuperAxe.name));
						damager.updateInventory();
		                return;
					}
                	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId()) != null) {
                		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId());
                		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(victim.getUniqueId());
                		if(playerFaction.equals(victimFaction)) {
                			damager.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                			return;
                		}
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(damager)) {
                		return;
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(victim)) {
                		return;
                	}
					SuperAxe.cooldown.put(damager.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SUPER-AXE.COOLDOWN") * 1000));
					damager.playSound(damager.getLocation(), sound, 1, 1);
					victim.playSound(damager.getLocation(), sound, 1, 1);
					victim.getInventory().addItem(helmet);
					victim.getInventory().setHelmet(air);
					victim.updateInventory();
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.PLAYER-MESSAGE"))
							.replace("<SUPER-AXE>", SuperAxe.name)
							.replace("<TARGET>", victim.getName()));
					victim.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.TARGET-MESSAGE"))
							.replace("<SUPER-AXE>", SuperAxe.name)
							.replace("<PLAYER>", damager.getName()));
					return;
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack inHand = player.getItemInHand();
		if ((inHand == null) || (inHand.getType().equals(Material.AIR)) || (inHand.getItemMeta().getDisplayName() == null)) {
			return;
		}
		if (inHand.getItemMeta().getDisplayName().equals(SuperAxe.name) && inHand.getItemMeta().hasLore()) {
			if (SuperAxe.cooldown.containsKey(player.getName()) && SuperAxe.cooldown.get(player.getName()) > System.currentTimeMillis()) {
				long millisLeft = SuperAxe.cooldown.get(player.getName()) - System.currentTimeMillis();
				event.setCancelled(true);
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SUPER-AXE.COOLDOWN-MESSAGE"))
						.replace("<SUPER-AXE>", SuperAxe.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
		}
	}
	
	public static String getName() {
		return SuperAxe.name;
	}
}
