package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.bfcode.bfbase.util.CC;

public class CustomEnchantCommand implements CommandExecutor {
	
	public static String speed = CC.translate("&eSpeed");
	public static String fire = CC.translate("&eFire Resistance");
	public static String invis = CC.translate("&eInvisibility");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CC.translate("&cThis command only execute by players."));
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("hcf.command.customenchant")) {
			player.sendMessage(CC.translate("&cYou don't have permission to execute this command."));
			return true;
		}
		ItemStack item = player.getInventory().getItemInHand();
		if (item.getType() == Material.AIR) {
			player.sendMessage(CC.translate("&cYou need a object in your hand."));
			return true;
		}
		if (args.length < 1) {
			getUsage(player);
			return true;
		}
		if (args[0].equalsIgnoreCase("speed")) {
			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if(itemMeta.hasLore()) {
				for(String listLore : itemMeta.getLore()) {
					lore.add(listLore);
				}	
			}
			lore.add(speed);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			player.sendMessage(CC.translate("&a'" + speed + "' added inventory!"));
			return true;
		} else if (args[0].equalsIgnoreCase("fire")) {
			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if(itemMeta.hasLore()) {
				for(String listLore : itemMeta.getLore()) {
					lore.add(listLore);
				}	
			}
			lore.add(fire);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			player.sendMessage(CC.translate("&a'" + fire + "' added inventory!"));
			return true;
		} else if (args[0].equalsIgnoreCase("invis")) {
			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if(itemMeta.hasLore()) {
				for(String listLore : itemMeta.getLore()) {
					lore.add(listLore);
				}	
			}
			lore.add(invis);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			player.sendMessage(CC.translate("&a'" + fire + "' added inventory!"));
			return true;
		} else {
			player.sendMessage(CC.translate("&cThis effect doesn´t exist!"));
			return true;
		}
	}
	
	public void getUsage(Player player) {
		player.sendMessage("");
		player.sendMessage(CC.translate("&3&lCustom Enchants"));
		player.sendMessage(CC.translate(" &f» &bSpeed"));
		player.sendMessage(CC.translate(" &f» &bFire"));
		player.sendMessage(CC.translate("&cUsa: /ce <efecto>"));
		player.sendMessage("");
	}
}
