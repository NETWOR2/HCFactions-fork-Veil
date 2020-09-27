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

public class Murasame implements Listener {

	public static Map<String, Long> cooldown;
	public static String victim_inv_full;
	public static String name;
	public static String soundString;

	static {
		Murasame.cooldown = new HashMap<String, Long>();
		Murasame.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.MURASAME.NAME"));
		Murasame.soundString = AbilitysFile.getConfig().getString("ABILITIES.MURASAME.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return Murasame.cooldown.containsKey(player.getName())
				&& Murasame.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public Murasame() {
		Murasame.cooldown = new HashMap<String, Long>();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = Murasame.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static Boolean hasCooldown(final Player player) {
		if (Murasame.cooldown.containsKey(player.getName())
				&& Murasame.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static double getSeconds(final Player player) {
		if (Murasame.cooldown.containsKey(player.getName())
				&& Murasame.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = Murasame.cooldown.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	@SuppressWarnings("deprecation")
	public static void getMurasame(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Murasame.name));
		lore = (AbilitysFile.getConfig().getStringList("ABILITIES.MURASAME.LORE"));
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.MURASAME.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.MURASAME.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Murasame.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Murasame.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(Murasame.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();
			ItemStack inHand = damager.getItemInHand();
			Sound sound = Sound.valueOf(Sound.class, soundString);
			if ((inHand == null) || (inHand.getType().equals(Material.AIR)) || (inHand.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (inHand.getItemMeta().getDisplayName().equals(Murasame.name) && inHand.getItemMeta().hasLore()) {
				if (Murasame.isOnCooldown(damager)) {
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damager.getLocation());
				Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(victim.getLocation());
				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", Murasame.name));
					damager.updateInventory();
	                return;
				}
				if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt2 instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", Murasame.name));
					damager.updateInventory();
	                return;
				}
            	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId()) != null) {
            		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damager.getUniqueId());
            		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(victim.getUniqueId());
            		if(playerFaction.equals(victimFaction)) {
            			damager.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
            			Murasame.cooldown.remove(damager.getName());
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
				Murasame.cooldown.put(damager.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.COOLDOWN") * 1000));
				damager.playSound(damager.getLocation(), sound, 1, 1);
				victim.playSound(damager.getLocation(), sound, 1, 1);
				damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.MURASAME.PLAYER-MESSAGE"))
						.replace("<MURASAME>", Murasame.name)
						.replace("<TARGET>", victim.getName()));
				victim.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.MURASAME.TARGET-MESSAGE"))
						.replace("<MURASAME>", Murasame.name)
						.replace("<PLAYER>", damager.getName()));
				giveEffect(victim);
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
		if (inHand.getItemMeta().getDisplayName().equals(Murasame.name) && inHand.getItemMeta().hasLore()) {
			if (Murasame.cooldown.containsKey(player.getName()) && Murasame.cooldown.get(player.getName()) > System.currentTimeMillis()) {
				long millisLeft = Murasame.cooldown.get(player.getName()) - System.currentTimeMillis();
				event.setCancelled(true);
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.MURASAME.COOLDOWN-MESSAGE"))
						.replace("<MURASAME>", Murasame.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
		}
	}

	private void giveEffect(Player victim) {
		int duration = AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.POISON.DURATION");
		int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.POISON.AMPLIFIER");
		victim.removePotionEffect(PotionEffectType.POISON);
		victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration * 20, amplifier - 1));
	}
}
