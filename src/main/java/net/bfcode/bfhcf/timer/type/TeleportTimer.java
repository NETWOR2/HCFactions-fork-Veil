package net.bfcode.bfhcf.timer.type;

import org.bukkit.Effect;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Location;
import java.util.Iterator;
import java.util.Collection;

import org.bukkit.entity.Entity;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionManager;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.TimerRunnable;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.event.Listener;

public class TeleportTimer extends PlayerTimer implements Listener
{
    private ConcurrentMap<Object, Object> destinationMap;
    private HCFaction plugin;
    
    public TeleportTimer(HCFaction plugin) {
        super(ConfigurationService.TELEPORT_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.teleport-time")), false);
        this.plugin = plugin;
        this.destinationMap = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(60000L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    public Object getDestination(Player player) {
        return this.destinationMap.get(player.getUniqueId());
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.TELEPORT_COLOUR;
    }
    
    @Override
    public TimerRunnable clearCooldown(UUID uuid) {
        TimerRunnable runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            this.destinationMap.remove(uuid);
            return runnable;
        }
        return null;
    }
    
    public int getNearbyEnemies(Player player, int distance) {
        FactionManager factionManager = this.plugin.getFactionManager();
        Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        int count = 0;
        Collection<Entity> nearby = (Collection<Entity>)player.getNearbyEntities((double)distance, (double)distance, (double)distance);
        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                Player target = (Player)entity;
                if (!target.canSee(player)) {
                    continue;
                }
                if (!player.canSee(target)) {
                    continue;
                }
                Faction targetFaction;
                if (playerFaction != null && (targetFaction = factionManager.getPlayerFaction(target)) != null && targetFaction.equals(playerFaction)) {
                    continue;
                }
                ++count;
            }
        }
        return count;
    }
    
    public boolean teleport(Player player, Location location, long millis, String warmupMessage, PlayerTeleportEvent.TeleportCause cause) {
        this.cancelTeleport(player, null);
        boolean result;
        if (millis <= 0L) {
            result = player.teleport(location, cause);
            this.clearCooldown(player.getUniqueId());
        }
        else {
            UUID uuid = player.getUniqueId();
            player.sendMessage(warmupMessage);
            this.destinationMap.put(uuid, location.clone());
            this.setCooldown(player, uuid, millis, true);
            result = true;
        }
        return result;
    }
    
    public void cancelTeleport(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            this.clearCooldown(uuid);
            if (reason != null && !reason.isEmpty()) {
                player.sendMessage(reason);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        this.cancelTeleport(event.getPlayer(), ChatColor.YELLOW + "You moved a block, therefore cancelling your teleport.");
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            this.cancelTeleport((Player)entity, ChatColor.YELLOW + "You took damage, therefore cancelling your teleport.");
        }
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        Location destination = (Location) this.destinationMap.remove(userUUID);
        if (destination != null) {
            destination.getChunk();
            player.playEffect(player.getLocation().clone().add(0.5, 1.0, 0.5), Effect.ENDER_SIGNAL, 3);
            player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }
}
