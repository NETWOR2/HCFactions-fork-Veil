package net.bfcode.bfhcf.timer.event;

import java.util.UUID;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerExpireEvent extends Event
{
    private static HandlerList handlers;
    private Optional<UUID> userUUID;
    private Timer timer;
    
    public TimerExpireEvent(Timer timer) {
        this.userUUID = Optional.absent();
        this.timer = timer;
    }
    
    public TimerExpireEvent(UUID userUUID, Timer timer) {
        this.userUUID = (Optional<UUID>)Optional.fromNullable(userUUID);
        this.timer = timer;
    }
    
    public static HandlerList getHandlerList() {
        return TimerExpireEvent.handlers;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public HandlerList getHandlers() {
        return TimerExpireEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
