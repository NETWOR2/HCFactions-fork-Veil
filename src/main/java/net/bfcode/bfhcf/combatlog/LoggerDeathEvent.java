package net.bfcode.bfhcf.combatlog;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class LoggerDeathEvent extends Event
{
    private static HandlerList handlers;
    private LoggerEntity loggerEntity;
    
    public LoggerDeathEvent(LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }
    
    public static HandlerList getHandlerList() {
        return LoggerDeathEvent.handlers;
    }
    
    public LoggerEntity getLoggerEntity() {
        return this.loggerEntity;
    }
    
    public HandlerList getHandlers() {
        return LoggerDeathEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
