package net.bfcode.bfhcf.killstreaks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.Messager;

public class KillStreaks {

	public static void x3(Player player) {
		Messager.broadcast(streakMessage(player, "x5 Gapples", 3));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5));
	}
	
	public static void x6(Player player) {
		Messager.broadcast(streakMessage(player, "Fire Resistance (8m)", 6));
		Integer time = 60;
		Integer seconds = time * 20;
		Integer minutes = seconds * 8;
		Integer totalTime = minutes;
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, totalTime, 0), true);
	}
	
	public static void x10(Player player) {
		Messager.broadcast(streakMessage(player, "Invisibility (8m)", 10));
		Integer time = 60;
		Integer seconds = time * 20;
		Integer minutes = seconds * 8;
		Integer totalTime = minutes;
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, totalTime, 0), true);
	}
	
	public static void x15(Player player) {
		Messager.broadcast(streakMessage(player, "x8 Gapples", 15));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 8));
	}
	
	public static void x20(Player player) {
		Messager.broadcast(streakMessage(player, "Damage Resistance (8m)", 20));
		Integer time = 60;
		Integer seconds = time * 20;
		Integer minutes = seconds * 8;
		Integer totalTime = minutes;
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, totalTime, 0), true);
	}
	
	public static void x25(Player player) {
		Messager.broadcast(streakMessage(player, "Strenght (3m)", 25));
		Integer time = 60;
		Integer seconds = time * 20;
		Integer minutes = seconds * 3;
		Integer totalTime = minutes;
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, totalTime, 0), true);
	}
	
	public static void x30(Player player) {
		Messager.broadcast(streakMessage(player, "x1 Gopple", 30));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
	}
	
	public static void x45(Player player) {
		Messager.broadcast(streakMessage(player, "x16 Snowball &7and &c&lx16 Egg", 45));
		player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hca give " + player.getName() + " 16 Switcher");
	}
	
	public static void x60(Player player) {
		Messager.broadcast(streakMessage(player, "x5 Gopples&7, &c&lx32 Gapples &7and &c&lx1 Grapple Hook", 60));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5, (short) 1));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 32));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hca give " + player.getName() + " 16 grappling");
	}
	
	private static String streakMessage(Player player, String string, Integer killStreak) {
		
		return HCFaction.getPlugin().getConfig().getString("messages.killstreak-message").replace("%player%", player.getName()).replace("%itemmessage%", string).replace("%killstreaks%", killStreak.toString());//"&7[&6&lKillStreaks&7] &e&l" + player.getName() + " &7received &e&l" + string + "&7 from their killstreak of &e&l" + killStreak + "&7.";
	}
}
