package net.bfcode.bfhcf.listener;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class FoundDiamondsListener implements Listener
{
    public static String DIAMOND_ORE_ALERTS;
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.DIAMOND_ORE)) {
            block.setMetadata("FoundDiamond", new FixedMetadataValue(HCFaction.getPlugin(), Boolean.TRUE));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType().equals(Material.DIAMOND_ORE) && !block.hasMetadata("FoundDiamond")) {
            int i = 0;
            for (int j = -5; j < 5; ++j) {
                for (int k = -5; k < 5; ++k) {
                    for (int m = -5; m < 5; ++m) {
                        Block localBlock2 = block.getRelative(j, k, m);
                        if (localBlock2.getType().equals(Material.DIAMOND_ORE) && !localBlock2.hasMetadata("FoundOre")) {
                            ++i;
                            localBlock2.setMetadata("FoundDiamond", new FixedMetadataValue(HCFaction.getPlugin(), Boolean.TRUE));
                        }
                    }
                }
            }
            if (i == 1) {
                Text message = new Text(ChatColor.translateAlternateColorCodes('&', "&7[&bFD&7]&f " + player.getName() + " &bhas found&f " + i + " &bdiamond."));
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.hasMetadata("FD_ALERTS")) {
                        if (other.hasPermission("rank.staff")) {
                            message.setHoverText(ChatColor.GREEN + "Click to teleport to " + player.getName()).setClick(ClickAction.RUN_COMMAND, "/tp " + player.getName());
                            message.send(other);
                        }
                    }
                }
            }
            else {
                Text message2 = new Text(ChatColor.translateAlternateColorCodes('&', "&7[&bFD&7]&f " + player.getName() + " &bhas found&f " + i + " &bdiamond."));
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.hasMetadata("FD_ALERTS")) {
                        if (other.hasPermission("rank.staff")) {
                            message2.setHoverText(ChatColor.GREEN + "Click to teleport to " + player.getName()).setClick(ClickAction.RUN_COMMAND, "/tp " + player.getName());
                            message2.send(other);
                        }
                    }
                }
            }
        }
    }
    
    static {
        FoundDiamondsListener.DIAMOND_ORE_ALERTS = "FD_ALERTS";
    }
}
