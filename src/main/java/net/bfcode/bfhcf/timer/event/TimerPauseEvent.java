package net.bfcode.bfhcf.timer.event;

import java.util.UUID;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TimerPauseEvent extends Event implements Cancellable
{
    private static HandlerList handlers;
    private boolean paused;
    private Optional<UUID> userUUID;
    private Timer timer;
    private boolean cancelled;
    
    public TimerPauseEvent(Timer timer, boolean paused) {
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.paused = paused;
    }
    
    public TimerPauseEvent(UUID userUUID, PlayerTimer timer, boolean paused) {
        this.userUUID = (Optional<UUID>)Optional.fromNullable(userUUID);
        this.timer = timer;
        this.paused = paused;
    }
    
    public static HandlerList getHandlerList() {
        return TimerPauseEvent.handlers;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public HandlerList getHandlers() {
        return TimerPauseEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    static {
        handlers = new HandlerList();
    }
}
