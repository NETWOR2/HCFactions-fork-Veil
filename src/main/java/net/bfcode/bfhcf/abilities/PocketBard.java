package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class PocketBard implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;
	public static HashMap<Player, PotionEffect> effects;

	public PocketBard() {
		PocketBard.cooldown = new HashMap<String, Long>();
		effects = new HashMap<Player, PotionEffect>();
	}

	static {
		PocketBard.cooldown = new HashMap<String, Long>();
		PocketBard.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.NAME"));
		PocketBard.soundString = AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return PocketBard.cooldown.containsKey(player.getName())
				&& PocketBard.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldown(final Player player) {
		if (PocketBard.cooldown.containsKey(player.getName())
				&& PocketBard.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = PocketBard.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getPocketBard(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(PocketBard.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.POCKET-BARD.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.POCKET-BARD.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.POCKET-BARD.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(PocketBard.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(PocketBard.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(PocketBard.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
		if (inHand.getItemMeta().getDisplayName().equals(PocketBard.name) && inHand.getItemMeta().hasLore()) {
			event.setCancelled(true);
			if (PocketBard.cooldown.containsKey(event.getPlayer().getName()) && PocketBard.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
				long millisLeft = PocketBard.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.COOLDOWN-MESSAGE"))
						.replace("<POCKET-BARD>", PocketBard.name)
						.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
				player.updateInventory();
				return;
			}
			Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
			if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
				player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
						.replace("<ABILITY>", PocketBard.name));
				player.updateInventory();
                return;
			}
        	if(FFACommand.mode == true) {
        		return;
        	}
			PocketBard.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.COOLDOWN") * 1000));
			player.playSound(player.getLocation(), sound, 1F, 1F);
			giveEffect(player);
			decrementItemInHand(player);
			return;
		}
	}

	@SuppressWarnings("static-access")
	private Random giveEffect(Player player) {
		Random rand = new Random();
		int chance = rand.nextInt(7);
		if (chance == 0) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.SPEED.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.SPEED.AMPLIFIER");
			for(PotionEffect effect : player.getActivePotionEffects()) {
				if(effect.getType().SPEED != null) {
					int duration2 = effect.getDuration();
					int amplifier2 = effect.getAmplifier();
					new BukkitRunnable() {
						@Override
						public void run() {
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration2, amplifier2));
						}
					}.runTaskLater(HCFaction.getPlugin(), duration * 20);
				}	
			}
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.SPEED.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		}
		else if (chance == 1) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.STRENGTH.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.STRENGTH.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.STRENGTH.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		} 
		else if (chance == 2) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.RESISTANCE.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.RESISTANCE.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.RESISTANCE.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		} 
		else if (chance == 3) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.JUMP-BOOST.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.JUMP-BOOST.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.JUMP);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.JUMP-BOOST.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		} 
		else if (chance == 4) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.REGENERATION.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.REGENERATION.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.REGENERATION);
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.REGENERATION.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		} 
		else if (chance == 5) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.FIRE-RESISTANCE.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.FIRE-RESISTANCE.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.FIRE-RESISTANCE.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		} 
		else if (chance == 6) {
			int duration = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.WITHER.DURATION");
			int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.WITHER.AMPLIFIER");
			player.removePotionEffect(PotionEffectType.WITHER);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, amplifier - 1));
			player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.POCKET-BARD.WITHER.MESSAGE"))
					.replace("<AMPLIFIER>", String.valueOf(amplifier)));
		}
		return rand;
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
