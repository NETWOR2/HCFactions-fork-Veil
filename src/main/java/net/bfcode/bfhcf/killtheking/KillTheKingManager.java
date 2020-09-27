package net.bfcode.bfhcf.killtheking;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.ItemBuilder;

public class KillTheKingManager {
	
	public static Player king;
	public static String prize;
	public static boolean active;
	
	public KillTheKingManager() {
		KillTheKingManager.king = null;
		KillTheKingManager.prize = null;
		KillTheKingManager.active = false;
	}
	
	public static Player getKing() {
		return king;
	}
	
	public static void setKing(Player king) {
		KillTheKingManager.king = king;
	}
	
	public static String getPrize() {
		return prize.replace("_", " ");
	}
	
	public static void setPrize(String prize) {
		KillTheKingManager.prize = prize;
	}
	
	public static Boolean getActive() {
		return active;
	}
	
	public static void setActive(boolean active) {
		KillTheKingManager.active = active;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static int getPotions(Player player) {
		int potions = 0;
		for(ItemStack items : player.getInventory().getContents()) {
			if(items != null && items.getType().equals(Material.POTION)) {
				potions = potions + items.getAmount();
			}
		}
		return potions;
	}
	
	public static int getHealGapple(Player player) {
		int apples = 0;
		for(ItemStack items : player.getInventory().getContents()) {
			if(items != null && items.getType() == Material.GOLDEN_APPLE && !(items.getDurability() == 1)) {
				apples = items.getAmount();
			}
		}
		return apples;
	}
	
	public static void startMessage(Player king) {
		int x = king.getLocation().getBlockX();
		int y = king.getLocation().getBlockY();
		int z = king.getLocation().getBlockZ();
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588     &eKing: &f" + king.getName()));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588\u2588\u2588&7\u2588\u2588\u2588     &eLocation: &f" + x + ", " + y + ", " + z));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588     &ePrize: &f" + getPrize()));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&a\u2588&7\u2588\u2588&a\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
	}
	
	public static void stopMessage() {
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588\u2588\u2588&7\u2588\u2588\u2588     &c&lEvent has been cancelled!"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
	}
	
	public static void kingDeathMessage(Player king, Player killer) {
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588\u2588\u2588&7\u2588\u2588\u2588     &c&lKing " + king.getName() + " has died."));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588     &a&lWINNER: " + killer.getName()));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588&c\u2588&7\u2588\u2588&c\u2588&7\u2588\u2588"));
		Bukkit.broadcastMessage(CC.translate("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
	}
	
	public static void giveInventory(Player king) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kit apply KillTheKing " + KillTheKingManager.getKing().getName());
	}
	
	public static void clearInventory(Player king) {
		king.getInventory().setArmorContents(null);
		king.getInventory().clear();
	}
}
