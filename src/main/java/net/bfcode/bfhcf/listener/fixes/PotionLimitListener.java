package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.projectiles.ProjectileSource;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.config.PotionLimiterData;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import org.bukkit.event.Listener;

public class PotionLimitListener implements Listener
{
    private static HCFaction plugin;
    private static List<Short> disabledPotions;
    private static PotionLimiterData limiter;
    
    public static void init() {
    }
    
    public static void reload() {
        PotionLimitListener.disabledPotions = (List<Short>)PotionLimiterData.getInstance().getConfig().getShortList("disabled-potions");
        System.out.println(PotionLimitListener.disabledPotions.toString());
    }
    
    public boolean isPotionDisabled(ItemStack item) {
        return item.getType() == Material.POTION && PotionLimitListener.disabledPotions.contains(item.getDurability());
    }
    
    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        if (this.isPotionDisabled(event.getPotion().getItem())) {
            event.setCancelled(true);
            ProjectileSource shooter = event.getEntity().getShooter();
            if (shooter instanceof Player) {
                ((Player)shooter).sendMessage(ChatColor.RED + "You cannot use this potion.");
                ((Player)shooter).getPlayer().setItemInHand((ItemStack)null);
            }
        }
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (this.isPotionDisabled(event.getItem())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this potion.");
            event.getPlayer().setItemInHand((ItemStack)null);
        }
    }
    
    @EventHandler
    public void onBrew(BrewEvent e) {
        BrewerInventory inventory = e.getContents();
        BrewingStand stand = inventory.getHolder();
        stand.setBrewingTime(200);
        if (this.isPotionDisabled(e.getContents().getItem(0))) {
            e.setCancelled(true);
        }
    }
}
