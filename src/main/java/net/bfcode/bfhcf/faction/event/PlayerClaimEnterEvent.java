package net.bfcode.bfhcf.faction.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.bfcode.bfhcf.faction.type.Faction;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerClaimEnterEvent extends Event implements Cancellable
{
    private static HandlerList handlers;
    private Player player;
    private Faction fromFaction;
    private Faction toFaction;
    private Location from;
    private Location to;
    private EnterCause enterCause;
    private boolean cancelled;
    
    public PlayerClaimEnterEvent(Player player, Location from, Location to, Faction fromFaction, Faction toFaction, EnterCause enterCause) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.fromFaction = fromFaction;
        this.toFaction = toFaction;
        this.enterCause = enterCause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerClaimEnterEvent.handlers;
    }
    
    public Faction getFromFaction() {
        return this.fromFaction;
    }
    
    public Faction getToFaction() {
        return this.toFaction;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Location getFrom() {
        return this.from;
    }
    
    public Location getTo() {
        return this.to;
    }
    
    public EnterCause getEnterCause() {
        return this.enterCause;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerClaimEnterEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
    
    public enum EnterCause
    {
        TELEPORT, 
        MOVEMENT;
    }
}
