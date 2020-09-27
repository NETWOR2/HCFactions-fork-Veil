package net.bfcode.bfhcf.combatlog;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class LoggerSpawnEvent extends Event
{
    private static HandlerList handlers;
    private LoggerEntity loggerEntity;
    
    public LoggerSpawnEvent(LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }
    
    public static HandlerList getHandlerList() {
        return LoggerSpawnEvent.handlers;
    }
    
    public LoggerEntity getLoggerEntity() {
        return this.loggerEntity;
    }
    
    public HandlerList getHandlers() {
        return LoggerSpawnEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
