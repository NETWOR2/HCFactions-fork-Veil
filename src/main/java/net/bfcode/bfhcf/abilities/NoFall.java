package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;

public class NoFall implements Listener {

	public static String name;

	static {
		NoFall.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.NO-FALL.NAME"));
	}
	
	@SuppressWarnings("deprecation")
	public static void getNoFall(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.NO-FALL.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.NO-FALL.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(NoFall.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.NO-FALL.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.NO-FALL.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.NO-FALL.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(NoFall.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(NoFall.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(NoFall.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			ItemStack item = player.getInventory().getBoots();
			if (event.getCause().equals(DamageCause.FALL)) {
				if (item == null) {
					return;
				}
				if (item.getItemMeta().getDisplayName() == null) {
					return;
				}
				if (item.getItemMeta().getDisplayName().equals(NoFall.name) && item.getItemMeta().hasLore()) {
					Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
					if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", NoFall.name));
						player.updateInventory();
		                return;
					}
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
