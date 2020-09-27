package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.block.Furnace;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class FurnaceSmeltSpeederListener implements Listener
{
    private HCFaction plugin;
    
    public FurnaceSmeltSpeederListener(HCFaction plugin) {
        this.plugin = plugin;
        ShapedRecipe cmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
        cmelon.shape(new String[] { "AAA", "CBA", "AAA" }).setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
        Bukkit.getServer().addRecipe((Recipe)cmelon);
    }
    
    private void startUpdate(Furnace tile, int increase) {
        new BukkitRunnable() {
            public void run() {
                if (tile.getCookTime() > 0 || tile.getBurnTime() > 0) {
                    tile.setCookTime((short)(tile.getCookTime() + increase));
                    tile.update();
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimer(HCFaction.getPlugin(), 1L, 10L);
    }
    
    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        this.startUpdate((Furnace)event.getBlock().getState(), 10);
    }
}
