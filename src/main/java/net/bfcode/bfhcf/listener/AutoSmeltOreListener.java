package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class AutoSmeltOreListener implements Listener
{
    private static String AUTO_SMELT_ORE_PERMISSION = "hcf.autosmeltore";
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack;
        if (player.getGameMode() != GameMode.CREATIVE && player.hasPermission(AUTO_SMELT_ORE_PERMISSION) && (stack = player.getItemInHand()) != null && stack.getType() != Material.AIR && !stack.containsEnchantment(Enchantment.SILK_TOUCH)) {
            Block block = event.getBlock();
            Material dropType = null;
            switch (block.getType()) {
                case IRON_ORE: {
                    dropType = Material.IRON_INGOT;
                    break;
                }
                case GOLD_ORE: {
                    dropType = Material.GOLD_INGOT;
                    break;
                }
                default: {
                    return;
                }
            }
            Location location = block.getLocation();
            World world = location.getWorld();
            ItemStack drop = new ItemStack(dropType, 1);
            world.dropItemNaturally(location, drop);
            block.setType(Material.AIR);
            block.getState().update();
        }
    }
}
