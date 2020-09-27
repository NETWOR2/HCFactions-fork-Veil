package net.bfcode.bfhcf.timer.event;

import javax.annotation.Nullable;

import java.util.UUID;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerStartEvent extends Event
{
    private static HandlerList handlers;
    private Optional<Player> player;
    private Optional<UUID> userUUID;
    private Timer timer;
    private long duration;
    
    public TimerStartEvent(Timer timer, long duration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.duration = duration;
    }
    
    public TimerStartEvent(@Nullable Player player, UUID uniqueId, PlayerTimer timer, long duration) {
        this.player = (Optional<Player>)Optional.fromNullable(player);
        this.userUUID = (Optional<UUID>)Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }
    
    public static HandlerList getHandlerList() {
        return TimerStartEvent.handlers;
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
    
    public long getDuration() {
        return this.duration;
    }
    
    public HandlerList getHandlers() {
        return TimerStartEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
