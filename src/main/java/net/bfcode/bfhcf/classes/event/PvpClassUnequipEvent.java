package net.bfcode.bfhcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.bfcode.bfhcf.classes.PvpClass;

public class PvpClassUnequipEvent extends PlayerEvent
{
    private static HandlerList handlers;
    private PvpClass pvpClass;
    
    public PvpClassUnequipEvent(Player player, PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvpClassUnequipEvent.handlers;
    }
    
    public PvpClass getPvpClass() {
        return this.pvpClass;
    }
    
    public HandlerList getHandlers() {
        return PvpClassUnequipEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
