package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
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

public class ThunderAxe implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;

	static {
		ThunderAxe.cooldown = new HashMap<String, Long>();
		ThunderAxe.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.THUNDER-AXE.NAME"));
		ThunderAxe.soundString = AbilitysFile.getConfig().getString("ABILITIES.THUNDER-AXE.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return ThunderAxe.cooldown.containsKey(player.getName())
				&& ThunderAxe.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public ThunderAxe() {
		ThunderAxe.cooldown = new HashMap<String, Long>();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = ThunderAxe.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static Boolean hasCooldown(final Player player) {
		if (ThunderAxe.cooldown.containsKey(player.getName())
				&& ThunderAxe.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static void getThunderAxe(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.THUNDER-AXE.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.THUNDER-AXE.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(ThunderAxe.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.THUNDER-AXE.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.THUNDER-AXE.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.THUNDER-AXE.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.THUNDER-AXE.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(ThunderAxe.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(ThunderAxe.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(ThunderAxe.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();
			World world = victim.getWorld();
			Location loc = victim.getLocation();
			ItemStack inHand = damager.getItemInHand();
			double health = victim.getHealthScale();
			double newHealth = health - 6.5;
			Sound sound = Sound.valueOf(Sound.class, soundString);
			if ((inHand == null) || (inHand.getType().equals(Material.AIR)) || (inHand.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (inHand.getItemMeta().getDisplayName().equals(ThunderAxe.name) && inHand.getItemMeta().hasLore()) {
				if (ThunderAxe.isOnCooldown(damager)) {
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damager.getLocation());
				Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(victim.getLocation());
				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", ThunderAxe.name));
					damager.updateInventory();
	                return;
				}
				if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt2 instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", ThunderAxe.name));
					damager.updateInventory();
	                return;
				}
            	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId()) != null) {
            		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId());
            		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(victim.getUniqueId());
            		if(playerFaction != null && playerFaction.equals(victimFaction)) {
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
            	if(FFACommand.mode == true) {
            		return;
            	}
				ThunderAxe.cooldown.put(damager.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.THUNDER-AXE.COOLDOWN") * 1000));
				damager.playSound(damager.getLocation(), sound, 1F, 1F);
				victim.playSound(victim.getLocation(), sound, 1F, 1F);
				world.strikeLightningEffect(loc);
				victim.setHealth(newHealth);
				damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.THUNDER-AXE.PLAYER-MESSAGE"))
						.replace("<THUNDER-AXE>", ThunderAxe.name)
						.replace("<TARGET>", victim.getName()));
				victim.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.THUNDER-AXE.TARGET-MESSAGE"))
						.replace("<THUNDER-AXE>", ThunderAxe.name)
						.replace("<PLAYER>", damager.getName()));
				return;
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
		if (inHand.getItemMeta().getDisplayName().equals(ThunderAxe.name) && inHand.getItemMeta().hasLore()) {
			if (ThunderAxe.cooldown.containsKey(player.getName()) && ThunderAxe.cooldown.get(player.getName()) > System.currentTimeMillis()) {
				long millisLeft = ThunderAxe.cooldown.get(player.getName()) - System.currentTimeMillis();
				event.setCancelled(true);
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.THUNDER-AXE.COOLDOWN-MESSAGE"))
						.replace("<THUNDER-AXE>", ThunderAxe.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
		}
	}
}
