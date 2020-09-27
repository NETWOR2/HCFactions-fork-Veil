package net.bfcode.bfhcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.bfcode.bfhcf.classes.PvpClass;

public class PvpClassEquipEvent extends PlayerEvent {
	
    private static HandlerList handlers;
    
    private PvpClass hCFClass;
    
    static {
        handlers = new HandlerList();
    }
    
    public PvpClassEquipEvent(Player player, PvpClass hCFClass) {
        super(player);
        this.hCFClass = hCFClass;
    }
    
    public PvpClass getPvpClass() {
        return this.hCFClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvpClassEquipEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return PvpClassEquipEvent.handlers;
    }
}
