package net.bfcode.bfhcf.timer;

import java.util.Set;
import java.util.LinkedHashMap;
import org.bukkit.configuration.MemorySection;

import javax.annotation.Nullable;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.event.TimerClearEvent;
import net.bfcode.bfhcf.timer.event.TimerExpireEvent;
import net.bfcode.bfhcf.timer.event.TimerExtendEvent;
import net.bfcode.bfhcf.timer.event.TimerPauseEvent;
import net.bfcode.bfhcf.timer.event.TimerStartEvent;
import net.bfcode.bfhcf.user.Config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Map;

public abstract class PlayerTimer extends Timer
{
    private static String COOLDOWN_PATH = "timer-cooldowns";
    protected boolean persistable;
    protected Map<UUID, TimerRunnable> cooldowns;
    
    public PlayerTimer(String name, long defaultCooldown) {
        this(name, defaultCooldown, true);
    }
    
    public PlayerTimer(String name, long defaultCooldown, boolean persistable) {
        super(name, defaultCooldown);
        this.cooldowns = new ConcurrentHashMap<UUID, TimerRunnable>();
        this.persistable = persistable;
    }
    
    public void onExpire(UUID userUUID) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerExpireLoadReduce(TimerExpireEvent event) {
        Optional<UUID> optionalUserUUID;
        if (event.getTimer().equals(this) && (optionalUserUUID = event.getUserUUID()).isPresent()) {
            UUID userUUID = (UUID)optionalUserUUID.get();
            this.onExpire(userUUID);
            this.clearCooldown(userUUID);
        }
    }
    
    public void clearCooldown(Player player) {
        this.clearCooldown(player.getUniqueId());
    }
    
    public TimerRunnable clearCooldown(UUID playerUUID) {
        TimerRunnable runnable = this.cooldowns.remove(playerUUID);
        if (runnable != null) {
            runnable.cancel();
            Bukkit.getPluginManager().callEvent((Event)new TimerClearEvent(playerUUID, this));
            return runnable;
        }
        return null;
    }
    
    public void clearCooldowns() {
        for (UUID uuid : this.cooldowns.keySet()) {
            this.clearCooldown(uuid);
        }
    }
    
    public boolean isPaused(Player player) {
        return this.isPaused(player.getUniqueId());
    }
    
    public boolean isPaused(UUID playerUUID) {
        TimerRunnable runnable = this.cooldowns.get(playerUUID);
        return runnable != null && runnable.isPaused();
    }
    
    public void setPaused(@Nullable Player player, UUID playerUUID, boolean paused) {
        TimerRunnable runnable = this.cooldowns.get(playerUUID);
        if (runnable != null && runnable.isPaused() != paused) {
            TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                runnable.setPaused(paused);
            }
        }
    }
    
    public boolean hasCooldown(Player player) {
        return this.getRemaining(player) > 0L;
    }
    
    public long getRemaining(Player player) {
        return this.getRemaining(player.getUniqueId());
    }
    
    public long getRemaining(UUID playerUUID) {
        TimerRunnable runnable = this.cooldowns.get(playerUUID);
        return (runnable == null) ? 0L : runnable.getRemaining();
    }
    
    public boolean setCooldown(@Nullable Player player, UUID playerUUID) {
        return this.setCooldown(player, playerUUID, this.defaultCooldown, false);
    }
    
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite) {
        TimerRunnable runnable = (duration <= 0L) ? this.clearCooldown(playerUUID) : this.cooldowns.get(playerUUID);
        if (runnable != null) {
            long remaining = runnable.getRemaining();
            if (!overwrite && remaining > 0L && duration > remaining) {
                return false;
            }
            TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            runnable.setRemaining(duration);
        }
        else {
            Bukkit.getPluginManager().callEvent((Event)new TimerStartEvent(player, playerUUID, this, duration));
            runnable = new TimerRunnable(playerUUID, this, duration);
        }
        this.cooldowns.put(playerUUID, runnable);
        return true;
    }
    
    @Override
    public void load(Config config) {
        if (!this.persistable) {
            return;
        }
        String path = "timer-cooldowns." + this.name;
        Object object = config.get(path);
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            long millis = System.currentTimeMillis();
            for (String id : section.getKeys(false)) {
                long remaining = config.getLong(section.getCurrentPath() + '.' + id) - millis;
                if (remaining <= 0L) {
                    continue;
                }
                this.setCooldown(null, UUID.fromString(id), remaining, true);
            }
        }
        if ((object = config.get(path = "timer-cooldowns." + this.name)) instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            for (String id2 : section.getKeys(false)) {
                TimerRunnable timerRunnable = this.cooldowns.get(UUID.fromString(id2));
                if (timerRunnable == null) {
                    continue;
                }
                timerRunnable.setPauseMillis(config.getLong(path + '.' + id2));
            }
        }
    }
    
    @Override
    public void onDisable(Config config) {
        if (this.persistable) {
            Set<Map.Entry<UUID, TimerRunnable>> entrySet = this.cooldowns.entrySet();
            LinkedHashMap<String, Long> pauseSavemap = new LinkedHashMap<String, Long>(entrySet.size());
            LinkedHashMap<String, Long> cooldownSavemap = new LinkedHashMap<String, Long>(entrySet.size());
            for (Map.Entry<UUID, TimerRunnable> entry : entrySet) {
                String id = entry.getKey().toString();
                TimerRunnable runnable = entry.getValue();
                pauseSavemap.put(id, runnable.getPauseMillis());
                cooldownSavemap.put(id, runnable.getExpiryMillis());
            }
            config.set("timer-pauses." + this.name, pauseSavemap);
            config.set("timer-cooldowns." + this.name, cooldownSavemap);
        }
    }
}
