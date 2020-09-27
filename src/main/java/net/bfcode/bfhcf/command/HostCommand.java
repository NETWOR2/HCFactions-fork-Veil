package net.bfcode.bfhcf.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfbase.listener.VanishListener;
import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.InventoryMaker;
import net.bfcode.bfhcf.utils.ItemMaker;
import net.bfcode.bfhcf.utils.Messager;

public class HostCommand implements CommandExecutor, Listener {
	
	private static String lastLineSumo;
    private static String lastLineDiamond;
    private static String lastLineBard;
    private static String lastLineAssassin;
    private static String lastLineBomber;
    private static String lastLineAxe;
    private static String lastLineArcher;
    private static String lastLineTNTTag;
    private static String lastLineSpleef;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou do not have to execute this command in Console."));
            return true;
        }
        Player player = (Player)sender;
        if(StaffModeCommand.isMod(player) || VanishListener.isVanished(player)) {
        	Messager.player(player, "&cYou not a create event with StaffMode.");
        }
        if (!HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
			Messager.player(player, "&cYou can only create event in Spawn Zone!");
			return true;
		}
        if(!ConfigurationService.KIT_MAP) {
        	Messager.player(player, "&cThe events its only create in KitMap!");
        	return true;
        }
        if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in SOTW"));
        	return true;
        }
    	HostGUI(player);
        return true;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(event.getInventory().getTitle().equals("Host's")) {
        	if(event.getClickedInventory() == null || event.getInventory() != event.getClickedInventory()) {
        		return;
        	}
        	if(event.getRawSlot() == 10 && player.hasPermission("tournament.sumo")) {
        		player.performCommand("tournament create 24 Sumo");
        	}
        	else if(event.getRawSlot() == 11 && player.hasPermission("tournament.ffa")) {
        		openFFAs(player);
        	}
        	else if(event.getRawSlot() == 12 && player.hasPermission("tournament.axe")) {
        		player.performCommand("tournament create 24 Axe");
        	}
        	else if(event.getRawSlot() == 14 && player.hasPermission("tournament.archer")) {
        		player.performCommand("tournament create 24 Srcher");
        	}
        	else if(event.getRawSlot() == 15 && player.hasPermission("tournament.spleef")) {
        		player.performCommand("tournament create 24 Spleef");
        	}
        	else if(event.getRawSlot() == 16 && player.hasPermission("tournament.tnttag")) {
        		player.performCommand("tournament create 24 TNTTag");
        	}
            event.setCancelled(true);	
        }
        if(event.getInventory().getTitle().equals("FFA´s")) {
        	if(event.getClickedInventory() == null || event.getInventory() != event.getClickedInventory()) {
        		return;
        	}
        	if(event.getRawSlot() == 10 && player.hasPermission("tournament.diamond")) {
        		player.performCommand("tournament create 24 Diamond");
        	}
        	else if(event.getRawSlot() == 12 && player.hasPermission("tournament.bard")) {
        		player.performCommand("tournament create 24 Bard");
        	}
        	else if(event.getRawSlot() == 14 && player.hasPermission("tournament.assassin")) {
        		player.performCommand("tournament create 24 Assassin");
        	}
        	else if(event.getRawSlot() == 16 && player.hasPermission("tournament.bomber")) {
        		player.performCommand("tournament create 24 Bomber");
        	}
        	event.setCancelled(true);
        }
    }
    
    public void HostGUI(Player player) {
	    if(player.hasPermission("tournament.sumo")) {
	   		 lastLineSumo = CC.translate("&aYou can create this event!");
	   	} else {
	   		 lastLineSumo = CC.translate("&cYou do not have permissions to create this Event!");
	   	}
		if(player.hasPermission("tournament.diamond")) {
			 lastLineDiamond = CC.translate("&aYou can create this event!");
		} else {
			 lastLineDiamond = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.bard")) {
			 lastLineBard = CC.translate("&aYou can create this event!");
		} else {
			 lastLineBard = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.assassin")) {
			 lastLineAssassin = CC.translate("&aYou can create this event!");
		} else {
			 lastLineAssassin = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.bomber")) {
			 lastLineBomber = CC.translate("&aYou can create this event!");
		} else {
			 lastLineBomber = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.axe")) {
			 lastLineAxe = CC.translate("&aYou can create this event!");
		} else {
			 lastLineAxe = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.archer")) {
			 lastLineArcher = CC.translate("&aYou do not have permissions to create this Event!");
		} else {
			 lastLineArcher = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.tnttag")) {
			 lastLineTNTTag = CC.translate("&aYou can create this event!");
		} else {
			lastLineTNTTag = CC.translate("&cYou do not have permissions to create this Event!");
		}
		if(player.hasPermission("tournament.spleef")) {
			 lastLineSpleef = CC.translate("&aYou can create this event!");
		} else {
			 lastLineSpleef = CC.translate("&cYou do not have permissions to create this Event!");
		}
		ItemStack NONE = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)7).displayName(" ").build();
		player.openInventory(new InventoryMaker(null, 3, "Host's")
				.setItem(0, NONE)
				.setItem(1, NONE)
				.setItem(2, NONE)
				.setItem(3, NONE)
				.setItem(4, NONE)
				.setItem(5, NONE)
				.setItem(6, NONE)
				.setItem(7, NONE)
				.setItem(8, NONE)
				.setItem(9, NONE)
        		.setItem(10, new ItemMaker(Material.RAW_FISH).displayName("&3&lSumo").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You mustn't be pulled off the platform!",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2Sumo",
                		"&7&m-----------------------------------",
                		lastLineSumo).create())
        		.setItem(11, new ItemMaker(Material.DIAMOND_SWORD).displayName("&3&lFFA").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7Click to open the Menu!",
                		"&7&m-----------------------------------").create())
        		.setItem(12, new ItemMaker(Material.DIAMOND_AXE).displayName("&3&lAxe").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2Axe",
                		"&7&m-----------------------------------",
                		lastLineAxe).create())
        		.setItem(13, NONE)
        		.setItem(14, new ItemMaker(Material.BOW).displayName("&3&lArcher").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2Archer",
                		"&7&m-----------------------------------",
                		lastLineArcher).create())
        		.setItem(15, new ItemMaker(Material.DIAMOND_SPADE).displayName("&3&lSpleef").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to break the ground for your enemies!",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2Spleef",
                		"&7&m-----------------------------------",
                		lastLineSpleef).create())
        		.setItem(16, new ItemMaker(Material.TNT).displayName("&3&lTNT-Tag &c(Mantenimiento)").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to pass the TNT to your enemies!",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2TNT-Tag",
                		"&7&m-----------------------------------",
                		lastLineTNTTag).create())
        		.setItem(17, NONE)
        		.setItem(18, NONE)
        		.setItem(19, NONE)
        		.setItem(20, NONE)
        		.setItem(21, NONE)
        		.setItem(22, NONE)
        		.setItem(23, NONE)
        		.setItem(24, NONE)
        		.setItem(25, NONE)
        		.setItem(26, NONE)
        		.create());
    }
    
    public void openFFAs(Player player) {
		ItemStack NONE = new ItemMaker(Material.STAINED_GLASS_PANE).data((short)7).displayName(" ").create();
    	player.openInventory(new InventoryMaker(null, 3, "FFA´s")
    			.setItem(0, NONE)
    			.setItem(1, NONE)
    			.setItem(2, NONE)
    			.setItem(3, NONE)
    			.setItem(4, NONE)
    			.setItem(5, NONE)
    			.setItem(6, NONE)
    			.setItem(7, NONE)
    			.setItem(8, NONE)
    			.setItem(9, NONE)
    			.setItem(10, new ItemMaker(Material.DIAMOND_AXE).displayName("&3&lFFA Diamond").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7With a full Diamond kit",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2FFA Diamond",
                		"&7&m-----------------------------------",
                		lastLineDiamond).create())
    			.setItem(11, NONE)
    			.setItem(12, new ItemMaker(Material.GOLD_AXE).displayName("&3&lFFA Bard").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7With a full Bard kit",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2FFA Bard",
                		"&7&m-----------------------------------",
                		lastLineBard).create())
    			.setItem(13, NONE)
    			.setItem(14, new ItemMaker(Material.WOOD_AXE).displayName("&3&lFFA Assassin").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7With a full Assassin kit",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2FFA Assassin",
                		"&7&m-----------------------------------",
                		lastLineAssassin).create())
    			.setItem(15, NONE)
    			.setItem(16, new ItemMaker(Material.TNT).displayName("&3&lFFA Bomber").lore(
        				"&7&m-----------------------------------",
                		"&bDescription for this event&7:",
                		" &8* &7You have to fight against other enemies!",
                		" &8* &7With a full Bomber kit",
                		" &8* &7The last one left standing wins!",
                		"",
                		"&bAwars to this event&7:",
                		" &8+&f2 &7Event Key",
                		"",
                		"&bClick to host the event &2FFA Bomber",
                		"&7&m-----------------------------------",
                		lastLineBomber).create())
    			.setItem(17, NONE)
    			.setItem(18, NONE)
    			.setItem(19, NONE)
    			.setItem(20, NONE)
    			.setItem(21, NONE)
    			.setItem(22, NONE)
    			.setItem(23, NONE)
    			.setItem(24, NONE)
    			.setItem(25, NONE)
    			.setItem(26, NONE)
    			.create());
    }
}
