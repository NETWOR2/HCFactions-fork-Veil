package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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

public class AntiTrapper implements Listener {

	public static Map<String, Long> cooldowndam;
	public static Map<String, Long> cooldownvic;
	public static String name;
	public static String soundString;
	public int count;

	public AntiTrapper() {
		AntiTrapper.cooldowndam = new HashMap<String, Long>();
		AntiTrapper.cooldownvic = new HashMap<String, Long>();
		this.count = 0;
	}

	static {
		AntiTrapper.cooldowndam = new HashMap<String, Long>();
		AntiTrapper.cooldownvic = new HashMap<String, Long>();
		AntiTrapper.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.NAME"));
		AntiTrapper.soundString = AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.SOUND").toUpperCase();
	}

	public static boolean isOnCooldownDam(final Player player) {
		return AntiTrapper.cooldowndam.containsKey(player.getName())
				&& AntiTrapper.cooldowndam.get(player.getName()) > System.currentTimeMillis();
	}

	public static boolean isOnCooldownVic(final Player player) {
		return AntiTrapper.cooldownvic.containsKey(player.getName())
				&& AntiTrapper.cooldownvic.get(player.getName()) > System.currentTimeMillis();
	}

	public static Boolean hasCooldownDam(final Player player) {
		if (AntiTrapper.cooldowndam.containsKey(player.getName())
				&& AntiTrapper.cooldowndam.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static Boolean hasCooldownVic(final Player player) {
		if (AntiTrapper.cooldownvic.containsKey(player.getName())
				&& AntiTrapper.cooldownvic.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static String getCooldownDam(final Player player) {
		final long millisLeft = AntiTrapper.cooldowndam.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static String getCooldownVic(final Player player) {
		final long millisLeft = AntiTrapper.cooldownvic.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getAntiTrapper(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(AntiTrapper.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.ANTI-TRAPPER.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.ANTI-TRAPPER.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.ANTI-TRAPPER.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(item);
		if (target == sender) {
			target.sendMessage(CC.translate(AntiTrapper.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(AntiTrapper.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(AntiTrapper.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
			if (inHand.getItemMeta().getDisplayName().equals(AntiTrapper.name) && inHand.getItemMeta().hasLore()) {
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
            	if(FFACommand.mode == true) {
            		return;
            	}
				if (AntiTrapper.isOnCooldownDam(damager)) {
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damager.getLocation());
				Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(victim.getLocation());
				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", AntiTrapper.name));
                    damager.updateInventory();
                    return;
				} 
				if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", AntiTrapper.name));
                    damager.updateInventory();
                    return;
				}
				count = count + 1;
				if (count >= 3) {
					count = 0;
					AntiTrapper.cooldowndam.put(damager.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.COOLDOWN") * 1000));
					AntiTrapper.cooldownvic.put(victim.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.TARGET-COOLDOWN") * 1000));
					damager.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.PLAYER-MESSAGE"))
							.replace("<ANTI-TRAPPER>", AntiTrapper.name)
							.replace("<TARGET>", victim.getName()));
					damager.playSound(damager.getLocation(), sound, 1, 1);
					victim.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.TARGET-MESSAGE"))
							.replace("<ANTI-TRAPPER>", AntiTrapper.name)
							.replace("<PLAYER>", damager.getName()));
					victim.playSound(damager.getLocation(), sound, 1, 1);
					this.decrementItemInHand(damager);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack is = event.getItem();
		Action action = event.getAction();
		if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
			if ((is == null) || (is.getType().equals(Material.AIR)) || (is.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (is.getItemMeta().getDisplayName().equals(AntiTrapper.name) && is.getItemMeta().hasLore()) {
				if (AntiTrapper.cooldowndam.containsKey(event.getPlayer().getName()) && AntiTrapper.cooldowndam.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
					long millisLeft = AntiTrapper.cooldowndam.get(event.getPlayer().getName()) - System.currentTimeMillis();
					event.setCancelled(true);
					player.sendMessage(CC
							.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.COOLDOWN-MESSAGE"))
							.replace("<ANTI-TRAPPER>", AntiTrapper.name)
							.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
					player.updateInventory();
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (AntiTrapper.isOnCooldownVic(player)) {
			long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
			event.setCancelled(true);
			player.sendMessage(CC
					.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.TARGET-PLACE-BLOCK"))
					.replace("<ANTI-TRAPPER>", AntiTrapper.name)
					.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (AntiTrapper.isOnCooldownVic(player)) {
			long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
			event.setCancelled(true);
			player.sendMessage(CC
					.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.TARGET-BREAK-BLOCK"))
					.replace("<ANTI-TRAPPER>", AntiTrapper.name)
					.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
			return;
		}
	}
	
	@EventHandler
	public void onFenceInteract(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		Block block = (Block) event.getClickedBlock();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (block.getType().equals(Material.FENCE_GATE) || block.getType().equals(Material.CHEST)) {
				if (AntiTrapper.isOnCooldownVic(player)) {
					long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
					event.setCancelled(true);
					player.sendMessage(CC
							.translate(AbilitysFile.getConfig().getString("ABILITIES.ANTI-TRAPPER.TARGET-BLOCK-INTERACT"))
							.replace("<ANTI-TRAPPER>", AntiTrapper.name)
							.replace("<SECONDS>", Utils.formatLongMin(millisLeft))
							.replace("<BLOCK>", String.valueOf(block.getType())));
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
