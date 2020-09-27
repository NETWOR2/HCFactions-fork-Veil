package net.bfcode.bfhcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

import java.util.UUID;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.combatlog.CombatLogListener;
import net.bfcode.bfhcf.faction.LandMap;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.TimerRunnable;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.event.Listener;

public class StuckTimer extends PlayerTimer implements Listener
{
    private ConcurrentMap<Object, Object> startedLocations;
    
    public StuckTimer() {
        super(ConfigurationService.STUCK_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.stuck-time")), false);
        this.startedLocations = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.STUCK_COLOUR;
    }
    
    @Override
    public TimerRunnable clearCooldown(UUID uuid) {
        TimerRunnable runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }
        return null;
    }
    
    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long millis, boolean force) {
        if (player != null && super.setCooldown(player, playerUUID, millis, force)) {
            this.startedLocations.put(playerUUID, player.getLocation());
            return true;
        }
        return false;
    }
    
    private void checkMovement(Player player, Location from, Location to) {
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            if (from == null) {
                this.clearCooldown(uuid);
                return;
            }
            int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
            if (xDiff > 5 || yDiff > 5 || zDiff > 5) {
                this.clearCooldown(uuid);
                player.sendMessage(ChatColor.RED + "You moved more than " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. " + this.getDisplayName() + ChatColor.RED + " timer ended.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Player player;
        if (entity instanceof Player && this.getRemaining(player = (Player)entity) > 0L) {
            player.sendMessage(ChatColor.RED + "You were damaged, " + this.getDisplayName() + ChatColor.RED + " timer ended.");
            this.clearCooldown(player);
        }
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 124);
        if (nearest == null) {
            CombatLogListener.safelyDisconnect(player, ChatColor.RED + "Unable to find a safe location, you have been safely logged out.");
            player.sendMessage(ChatColor.RED + "No safe-location found.");
            return;
        }
        if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage(ChatColor.YELLOW + this.getDisplayName() + ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
        }
    }
    
    public void run(Player player) {
        long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(this.getDisplayName() + ChatColor.BLUE + " timer is teleporting you in " + ChatColor.BOLD + HCFaction.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}
