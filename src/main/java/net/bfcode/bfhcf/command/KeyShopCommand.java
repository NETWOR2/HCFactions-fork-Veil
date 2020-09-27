package net.bfcode.bfhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.item.ItemMaker;

public class KeyShopCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if(!ConfigurationService.KIT_MAP) {
			sender.sendMessage(CC.translate("&cThis command is only executable in KitMap."));
			return true;
		}
		Player player = (Player) sender;
		Inventory inv = Bukkit.createInventory(null, 27, "Key Shop");
		
		inv.setItem(9, new ItemMaker(Material.REDSTONE).setName(CC.translate("&6Reward Crate Key")).addEnchantment(Enchantment.DURABILITY, 1).build());
		inv.setItem(18, new ItemMaker(Material.PAPER).setName(CC.translate("&e$500")).addLore(CC.translate("&aClick here for Buy")).build());
		
		inv.setItem(11, new ItemMaker(Material.DOUBLE_PLANT).setName(CC.translate("&eAbilities Crate Key")).addEnchantment(Enchantment.DURABILITY, 1).build());
		inv.setItem(20, new ItemMaker(Material.PAPER).setName(CC.translate("&e$1500")).addLore(CC.translate("&aClick here for Buy")).build());
		
		inv.setItem(13, new ItemMaker(Material.TRIPWIRE_HOOK).setName(CC.translate("&dEvent Key")).addEnchantment(Enchantment.DURABILITY, 1).build());
		inv.setItem(22, new ItemMaker(Material.PAPER).setName(CC.translate("&e$2000")).addLore(CC.translate("&aClick here for Buy")).build());
		
		inv.setItem(15, new ItemMaker(Material.GOLD_NUGGET).setName(CC.translate("&aKoTH Crate Key")).addEnchantment(Enchantment.DURABILITY, 1).build());
		inv.setItem(24, new ItemMaker(Material.PAPER).setName(CC.translate("&e$2500")).addLore(CC.translate("&aClick here for Buy")).build());
		
		inv.setItem(17, new ItemMaker(Material.TRIPWIRE_HOOK).setName(CC.translate("&6Conquest Key")).addEnchantment(Enchantment.DURABILITY, 1).build());
		inv.setItem(26, new ItemMaker(Material.PAPER).setName(CC.translate("&e$4500")).addLore(CC.translate("&aClick here for Buy")).build());
		
		player.openInventory(inv);
		return true;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(!ConfigurationService.KIT_MAP) {
			return;
		}
		if(event.getInventory().getTitle().contains("Key Shop")) {
			switch(event.getRawSlot()) {
			case 18:
				if(HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >=  500) {
					player.sendMessage(CC.translate("&aSuccessfully bought Reward!"));
					HCFaction.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 500);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Reward 1");
				} else {
					player.sendMessage(CC.translate("&cYou need a more balance for buy this!"));
				}
				break;
			case 20:
				if(HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >=  1500) {
					player.sendMessage(CC.translate("&aSuccessfully bought Abilities!"));
					HCFaction.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 1500);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Abilities 1");
				} else {
					player.sendMessage(CC.translate("&cYou need a more balance for buy this!"));
				}
				break;
			case 22:
				if(HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >=  2000) {
					player.sendMessage(CC.translate("&aSuccessfully bought Event!"));
					HCFaction.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 2000);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Event 1");
				} else {
					player.sendMessage(CC.translate("&cYou need a more balance for buy this!"));
				}
				break;
			case 24:
				if(HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >=  2500) {
					player.sendMessage(CC.translate("&aSuccessfully bought KoTH!"));
					HCFaction.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 2500);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " KoTH 1");
				} else {
					player.sendMessage(CC.translate("&cYou need a more balance for buy this!"));
				}
				break;
			case 26:
				if(HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >=  4500) {
					player.sendMessage(CC.translate("&aSuccessfully bought Conquest!"));
					HCFaction.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 4500);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Conquest 1");
				} else {
					player.sendMessage(CC.translate("&cYou need a more balance for buy this!"));
				}
				break;
			}
			event.setCancelled(true);
		}
	}

}
