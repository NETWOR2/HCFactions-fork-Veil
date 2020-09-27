package net.bfcode.bfhcf.timer.event;

import java.util.UUID;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerClearEvent extends Event
{
    private static HandlerList handlers;
    private Optional<UUID> userUUID;
    private Timer timer;
    
    public TimerClearEvent(Timer timer) {
        this.userUUID = Optional.absent();
        this.timer = timer;
    }
    
    public TimerClearEvent(UUID userUUID, PlayerTimer timer) {
        this.userUUID = Optional.of(userUUID);
        this.timer = timer;
    }
    
    public static HandlerList getHandlerList() {
        return TimerClearEvent.handlers;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public HandlerList getHandlers() {
        return TimerClearEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
