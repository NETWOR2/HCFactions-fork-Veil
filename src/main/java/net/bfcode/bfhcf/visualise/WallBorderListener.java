package net.bfcode.bfhcf.visualise;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import org.bukkit.util.Vector;

import java.util.HashSet;
import com.google.common.base.Predicate;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.RoadFaction;

import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashMap;

import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;
import org.bukkit.event.Listener;

public class WallBorderListener implements Listener
{
    private HCFaction plugin;
    
    public WallBorderListener(HCFaction plugin) {
        new HashMap<UUID, BukkitTask>();
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getClass();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getClass();
        Location now = player.getLocation();
        new BukkitRunnable() {
            public void run() {
                Location location = player.getLocation();
                if (now.equals((Object)location)) {
                    WallBorderListener.this.handlePositionChanged(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                }
            }
        }.runTaskLater((Plugin)this.plugin, 4L);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.getClass();
        Location to = event.getTo();
        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();
        Location from = event.getFrom();
        if (from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
            this.handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove((PlayerMoveEvent)event);
    }
    
    private void handlePositionChanged(Player player, World toWorld, int toX, int toY, int toZ) {
        VisualType visualType;
        if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
            visualType = VisualType.SPAWN_BORDER;
        }
        else {
            if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player) <= 0L) {
                return;
            }
            visualType = VisualType.CLAIM_BORDER;
        }
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, (Predicate<VisualBlock>)(visualBlock -> {
            Location other = visualBlock.getLocation();
            return other.getWorld().equals(toWorld) && (Math.abs(toX - other.getBlockX()) > 7 || Math.abs(toY - other.getBlockY()) > 4 || Math.abs(toZ - other.getBlockZ()) > 7);
        }));
        int minHeight = toY - 3;
        int maxHeight = toY + 4;
        int minX = toX - 7;
        int maxX = toX + 7;
        int minZ = toZ - 7;
        int maxZ = toZ + 7;
        Collection<Claim> added = new HashSet<Claim>();
        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                Faction faction = this.plugin.getFactionManager().getFactionAt(toWorld, x, z);
                if (faction instanceof ClaimableFaction) {
                    if (visualType == VisualType.SPAWN_BORDER) {
                        if (!faction.isSafezone()) {
                            continue;
                        }
                    }
                    else if (visualType == VisualType.CLAIM_BORDER) {
                        if (faction instanceof RoadFaction) {
                            continue;
                        }
                        if (faction.isSafezone()) {
                            continue;
                        }
                    }
                    Collection<Claim> claims = ((ClaimableFaction)faction).getClaims();
                    for (Claim claim : claims) {
                        if (toWorld.equals(claim.getWorld())) {
                            added.add(claim);
                        }
                    }
                }
            }
        }
        if (!added.isEmpty()) {
            Iterator<Claim> iterator = added.iterator();
            while (iterator.hasNext()) {
                Claim claim2 = iterator.next();
                List<Vector> edges = (List<Vector>)claim2.edges();
                for (Vector edge : edges) {
                    if (Math.abs(edge.getBlockX() - toX) > 7) {
                        continue;
                    }
                    if (Math.abs(edge.getBlockZ() - toZ) > 7) {
                        continue;
                    }
                    Location location = edge.toLocation(toWorld);
                    if (location == null) {
                        continue;
                    }
                    Location first = location.clone();
                    first.setY((double)minHeight);
                    Location second = location.clone();
                    second.setY((double)maxHeight);
                    this.plugin.getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false).size();
                }
                iterator.remove();
            }
        }
    }
    
    @SuppressWarnings("unused")
	private static class WarpTimerRunnable extends BukkitRunnable
    {
        private WallBorderListener listener;
        private Player player;
        private double lastX;
        private double lastY;
        private double lastZ;
        
        public WarpTimerRunnable(WallBorderListener listener, Player player) {
            this.lastX = Double.MAX_VALUE;
            this.lastY = Double.MAX_VALUE;
            this.lastZ = Double.MAX_VALUE;
            this.listener = listener;
            this.player = player;
        }
        
        public void run() {
            Location location = this.player.getLocation();
            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();
            if (this.lastX == x && this.lastY == y && this.lastZ == z) {
                return;
            }
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.listener.handlePositionChanged(this.player, this.player.getWorld(), (int)x, (int)y, (int)z);
        }
        
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.listener = null;
            this.player = null;
        }
    }
}
