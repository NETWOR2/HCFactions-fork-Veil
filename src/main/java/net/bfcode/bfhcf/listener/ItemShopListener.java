package net.bfcode.bfhcf.listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.utils.InventoryMaker;
import net.bfcode.bfhcf.utils.ItemMaker;

import org.bukkit.event.Listener;


public class ItemShopListener implements Listener {
	
	private int chestplate_price = 10000;
	private int boots_price = 10000;
	private int sword_price = 10000;
	private int axe_price = 30000;
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
        if(factionAt instanceof SpawnFaction) {
	        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {
	            Sign sign = (Sign)event.getClickedBlock().getState();
	            if (sign.getLine(1).contains("Items")) {
	            	ItemStack CHESTPLATE = new ItemMaker(Material.DIAMOND_CHESTPLATE).displayName("&f&k: &6&lLegendary Chestplate &f&k:").lore("&cFireResistance I").create();
	            	ItemMeta chestplate = CHESTPLATE.getItemMeta();
	            	chestplate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
	            	chestplate.addEnchant(Enchantment.DURABILITY, 5, true);
	            	CHESTPLATE.setItemMeta(chestplate);
	            	
	            	ItemStack BOOTS = new ItemMaker(Material.DIAMOND_BOOTS).displayName("&f&k: &6&lLegendary Boots &f&k:").lore("&cSpeed II").create();
	            	ItemMeta boots = BOOTS.getItemMeta();
	            	boots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
	            	boots.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
	            	boots.addEnchant(Enchantment.DURABILITY, 5, true);
	            	BOOTS.setItemMeta(boots);
	            	
	            	ItemStack SWORD = new ItemMaker(Material.DIAMOND_SWORD).displayName("&f&k: &6&lLegendary Sword &f&k:").create();
	            	ItemMeta sword = SWORD.getItemMeta();
	            	sword.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
	            	sword.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
	            	sword.addEnchant(Enchantment.DURABILITY, 5, true);
	            	SWORD.setItemMeta(sword);
	            	
	            	ItemStack AXE = new ItemMaker(Material.DIAMOND_AXE).displayName("&f&k: &6&lLegendary Axe &f&k:").create();
	            	ItemMeta axe = AXE.getItemMeta();
	            	axe.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
	            	axe.addEnchant(Enchantment.DURABILITY, 5, true);
	            	AXE.setItemMeta(axe);
	            	
	            	ItemStack PANEL = new ItemMaker(Material.STAINED_GLASS_PANE).data((short)7).displayName(" ").create();
	                player.openInventory(new InventoryMaker(null, 3, "Items")
	            		.setItem(0, PANEL)
	            		.setItem(1, PANEL)
	            		.setItem(2, PANEL)
	            		.setItem(3, PANEL)
	            		.setItem(4, PANEL)
	            		.setItem(5, PANEL)
	            		.setItem(6, PANEL)
	            		.setItem(7, PANEL)
	            		.setItem(8, PANEL)
	            		.setItem(9, PANEL)
	            		.setItem(10, PANEL)
	            		.setItem(11, CHESTPLATE)
	            		.setItem(12, BOOTS)
	            		.setItem(13, PANEL)
	            		.setItem(14, SWORD)
	            		.setItem(15, AXE)
	            		.setItem(16, PANEL)
	            		.setItem(17, PANEL)
	            		.setItem(18, PANEL)
	            		.setItem(19, PANEL)
	            		.setItem(20, new ItemMaker(Material.EMERALD).displayName("&a$10.000").lore("&7&oRight Click to purchase!").create())
	            		.setItem(21, new ItemMaker(Material.EMERALD).displayName("&a$10.000").lore("&7&oRight Click to purchase!").create())
	            		.setItem(22, PANEL)
	            		.setItem(23, new ItemMaker(Material.EMERALD).displayName("&a$10.000").lore("&7&oRight Click to purchase!").create())
	            		.setItem(24, new ItemMaker(Material.EMERALD).displayName("&a$30.000").lore("&7&oRight Click to purchase!").create())
	            		.setItem(25, PANEL)
	            		.setItem(26, PANEL).create());
	            }
	        }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        int balance = HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId());
        String message = CC.translate("&cYou don't have enough money to buy this.");
        String inventoryFull = CC.translate("&cYour inventory is full.");
        if (event.getInventory().getName().equals("Items")) {
        	if(event.getClickedInventory() == null || event.getInventory() != event.getClickedInventory()) {
        		return;
        	}
        	event.setCancelled(true);
        	switch (slot) {
        		case 20:
        			if (balance < chestplate_price) {
                        player.sendMessage(message);
                        return;
                    }
                    if (player.getInventory().firstEmpty() < 0) {
                        player.closeInventory();
                        player.sendMessage(CC.translate(inventoryFull));
                        return;
                    }
                    ItemStack CHESTPLATE = new ItemMaker(Material.DIAMOND_CHESTPLATE).displayName("&f&k: &6&lLegendary Chestplate &f&k:").lore("&cFireResistance I").create();
                	ItemMeta chestplate = CHESTPLATE.getItemMeta();
                	chestplate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
                	chestplate.addEnchant(Enchantment.DURABILITY, 5, true);
                	CHESTPLATE.setItemMeta(chestplate);
                    HCFaction.getPlugin().getEconomyManager().setBalance(player.getUniqueId(), balance - chestplate_price);
                    player.getInventory().addItem(new ItemStack[] { CHESTPLATE });
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    return;
        		case 21:
        			if (balance < boots_price) {
                        player.sendMessage(message);
                        return;
                    }
                    if (player.getInventory().firstEmpty() < 0) {
                        player.closeInventory();
                        player.sendMessage(CC.translate(inventoryFull));
                        return;
                    }
                    ItemStack BOOTS = new ItemMaker(Material.DIAMOND_BOOTS).displayName("&f&k: &6&lLegendary Boots &f&k:").lore("&cSpeed II").create();
                	ItemMeta boots = BOOTS.getItemMeta();
                	boots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
                	boots.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
                	boots.addEnchant(Enchantment.DURABILITY, 5, true);
                	BOOTS.setItemMeta(boots);
                    HCFaction.getPlugin().getEconomyManager().setBalance(player.getUniqueId(), balance - boots_price);
                    player.getInventory().addItem(new ItemStack[] { BOOTS });
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    return;
        		case 23:
        			if (balance < sword_price) {
                        player.sendMessage(message);
                        return;
                    }
                    if (player.getInventory().firstEmpty() < 0) {
                        player.closeInventory();
                        player.sendMessage(CC.translate(inventoryFull));
                        return;
                    }
                    ItemStack SWORD = new ItemMaker(Material.DIAMOND_SWORD).displayName("&f&k: &6&lLegendary Sword &f&k:").create();
                	ItemMeta sword = SWORD.getItemMeta();
                	sword.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
                	sword.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
                	sword.addEnchant(Enchantment.DURABILITY, 5, true);
                	SWORD.setItemMeta(sword);
                    HCFaction.getPlugin().getEconomyManager().setBalance(player.getUniqueId(), balance - sword_price);
                    player.getInventory().addItem(new ItemStack[] { SWORD });
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    return;
        		case 24:
        			if (balance < axe_price) {
                        player.sendMessage(message);
                        return;
                    }
                    if (player.getInventory().firstEmpty() < 0) {
                        player.closeInventory();
                        player.sendMessage(CC.translate(inventoryFull));
                        return;
                    }
                    ItemStack AXE = new ItemMaker(Material.DIAMOND_AXE).displayName("&f&k: &6&lLegendary Axe &f&k:").create();
                	ItemMeta axe = AXE.getItemMeta();
                	axe.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
                	axe.addEnchant(Enchantment.DURABILITY, 5, true);
                	AXE.setItemMeta(axe);
                    HCFaction.getPlugin().getEconomyManager().setBalance(player.getUniqueId(), balance - axe_price);
                    player.getInventory().addItem(new ItemStack[] { AXE });
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    return;
        	}
        }
    }
}
