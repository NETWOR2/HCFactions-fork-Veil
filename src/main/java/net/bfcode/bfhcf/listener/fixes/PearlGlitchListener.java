package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import com.google.common.collect.Sets;

import net.bfcode.bfhcf.HCFaction;
import org.bukkit.Material;
import com.google.common.collect.ImmutableSet;

import org.bukkit.event.Listener;

public class PearlGlitchListener implements Listener
{
    private HCFaction plugin;
    private ImmutableSet<Material> blockedPearlTypes;
    
    public PearlGlitchListener(HCFaction plugin) {
        this.blockedPearlTypes = Sets.immutableEnumSet(Material.THIN_GLASS, new Material[] { Material.STAINED_GLASS_PANE });
        this.plugin = plugin;
    }
    
/*    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.ENDER_PEARL) {
            Block block = event.getClickedBlock();
            if (block.getType().isSolid() && !(block.getState() instanceof InventoryHolder)) {
                Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(block.getLocation());
                if (factionAt instanceof ClaimableFaction) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    player.setItemInHand(event.getItem());
                }
            }
        }
    }*/
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPearlClip(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (this.blockedPearlTypes.contains(to.getBlock().getType())) {
                Player player = event.getPlayer();
                player.sendMessage(ChatColor.YELLOW + "Pearl glitching detected, enderpearl refunded.");
                this.plugin.getTimerManager().enderPearlTimer.refund(player);
                event.setCancelled(true);
                return;
            }
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);
            event.setTo(to);
        }
    }
}
