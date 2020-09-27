package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.Listener;

public class BookQuillFixListener implements Listener
{
    @EventHandler
    public void craftBookEvent(PrepareItemCraftEvent e) {
        Material itemType = e.getRecipe().getResult().getType();
        if (itemType == Material.BOOK_AND_QUILL) {
            e.getInventory().setResult(new ItemStack(Material.AIR));
            for (HumanEntity he : e.getViewers()) {
                if (he instanceof Player) {
                    ((Player)he).sendMessage(ChatColor.RED + "This item is disabled.");
                }
            }
        }
    }
}
