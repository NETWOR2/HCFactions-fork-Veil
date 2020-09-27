package net.bfcode.bfhcf.listener.fixes;

import com.google.common.collect.Sets;

import java.util.UUID;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.concurrent.TimeUnit;

import net.bfcode.bfbase.util.BukkitUtils;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Material;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.Listener;

public class BlockHitFixListener implements Listener
{
    private static long THRESHOLD = 850L;
    private static ImmutableSet<Material> NON_TRANSPARENT_ATTACK_BREAK_TYPES;
    private static ImmutableSet<Material> NON_TRANSPARENT_ATTACK_INTERACT_TYPES;
    private ConcurrentMap<Object, Object> lastInteractTimes;
    
    public BlockHitFixListener() {
        this.lastInteractTimes = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(850L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getAction() == Action.PHYSICAL || BlockHitFixListener.NON_TRANSPARENT_ATTACK_INTERACT_TYPES.contains((Object)event.getClickedBlock().getType())) {}
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() || BlockHitFixListener.NON_TRANSPARENT_ATTACK_BREAK_TYPES.contains((Object)event.getBlock().getType())) {}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        Player attacker = BukkitUtils.getFinalAttacker(event, true);
        if (attacker != null) {
            Long lastInteractTime = (Long) this.lastInteractTimes.get(attacker.getUniqueId());
            if (lastInteractTime == null || lastInteractTime - System.currentTimeMillis() > 0L) {}
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(PlayerQuitEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    public void cancelAttackingMillis(UUID uuid, long delay) {
        this.lastInteractTimes.put(uuid, System.currentTimeMillis() + delay);
    }
    
    static {
        NON_TRANSPARENT_ATTACK_BREAK_TYPES = Sets.immutableEnumSet(Material.GLASS, new Material[] { Material.STAINED_GLASS, Material.STAINED_GLASS_PANE });
        NON_TRANSPARENT_ATTACK_INTERACT_TYPES = Sets.immutableEnumSet(Material.IRON_DOOR_BLOCK, new Material[] { Material.IRON_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR, Material.FENCE_GATE });
    }
}
