package net.bfcode.bfhcf.combatlog;

import org.bukkit.scheduler.BukkitTask;

public class CombatLogEntry
{
    public LoggerEntity loggerEntity;
    public BukkitTask task;
    
    public CombatLogEntry(LoggerEntity loggerEntity, BukkitTask task) {
        this.loggerEntity = loggerEntity;
        this.task = task;
    }
}
