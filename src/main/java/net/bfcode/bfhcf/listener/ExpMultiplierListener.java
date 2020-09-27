package net.bfcode.bfhcf.listener;

import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Listener;

public class ExpMultiplierListener implements Listener
{
    public static double DEFAULT_MULTIPLER = 0;
	public static double LOOTING_MULTIPLIER = 0;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        double amount = event.getDroppedExp();
        Player killer = event.getEntity().getKiller();
        ItemStack stack;
        int enchantmentLevel;
        if (killer != null && amount > 0.0 && (stack = killer.getItemInHand()) != null && stack.getType() != Material.AIR && (enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) > 0L) {
            double multiplier = enchantmentLevel * 3.0;
            int result = (int)Math.ceil(amount * multiplier);
            event.setDroppedExp(result);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        double amount = event.getExpToDrop();
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        int enchantmentLevel;
        if (stack != null && stack.getType() != Material.AIR && amount > 0.0 && (enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)) > 0) {
            double multiplier = enchantmentLevel * 4.5;
            int result = (int)Math.ceil(amount * multiplier);
            event.setExpToDrop(result);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupExp(PlayerExpChangeEvent event) {
        double amount = event.getAmount();
        if (amount > 0.0) {
            int result = (int)Math.ceil(amount * 3.0);
            event.setAmount(result);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFish(PlayerFishEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0.0) {
            amount = Math.ceil(amount * 2.0);
            ProjectileSource projectileSource = event.getHook().getShooter();
            ItemStack stack;
            int enchantmentLevel;
            if (projectileSource instanceof Player && (enchantmentLevel = (stack = ((Player)projectileSource).getItemInHand()).getEnchantmentLevel(Enchantment.LUCK)) > 0L) {
                amount = Math.ceil(amount * (enchantmentLevel * 1.5));
            }
            event.setExpToDrop((int)amount);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0.0) {
            double multiplier = 2.0;
            int result = (int)Math.ceil(amount * 5.0);
            event.setExpToDrop(result);
        }
    }
}
