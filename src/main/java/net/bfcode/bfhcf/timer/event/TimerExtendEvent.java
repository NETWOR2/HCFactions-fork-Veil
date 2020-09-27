package net.bfcode.bfhcf.timer.event;

import javax.annotation.Nullable;

import java.util.UUID;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.GlobalTimer;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TimerExtendEvent extends Event implements Cancellable
{
    private static HandlerList handlers;
    private Optional<Player> player;
    private Optional<UUID> userUUID;
    private Timer timer;
    private long previousDuration;
    private boolean cancelled;
    private long newDuration;
    
    public TimerExtendEvent(GlobalTimer timer, long previousDuration, long newDuration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }
    
    public TimerExtendEvent(@Nullable Player player, UUID uniqueId, PlayerTimer timer, long previousDuration, long newDuration) {
        this.player = (Optional<Player>)Optional.fromNullable(player);
        this.userUUID = (Optional<UUID>)Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }
    
    public static HandlerList getHandlerList() {
        return TimerExtendEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        return this.player;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public long getPreviousDuration() {
        return this.previousDuration;
    }
    
    public long getNewDuration() {
        return this.newDuration;
    }
    
    public void setNewDuration(long newDuration) {
        this.newDuration = newDuration;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return TimerExtendEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
