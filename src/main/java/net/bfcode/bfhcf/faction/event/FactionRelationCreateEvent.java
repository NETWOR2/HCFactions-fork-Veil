package net.bfcode.bfhcf.faction.event;

import org.bukkit.event.HandlerList;

import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class FactionRelationCreateEvent extends Event implements Cancellable
{
    private static HandlerList handlers;
    private PlayerFaction senderFaction;
    private PlayerFaction targetFaction;
    private Relation relation;
    private boolean cancelled;
    
    public FactionRelationCreateEvent(PlayerFaction senderFaction, PlayerFaction targetFaction, Relation relation) {
        this.senderFaction = senderFaction;
        this.targetFaction = targetFaction;
        this.relation = relation;
    }
    
    public static HandlerList getHandlerList() {
        return FactionRelationCreateEvent.handlers;
    }
    
    public PlayerFaction getSenderFaction() {
        return this.senderFaction;
    }
    
    public PlayerFaction getTargetFaction() {
        return this.targetFaction;
    }
    
    public Relation getRelation() {
        return this.relation;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    public HandlerList getHandlers() {
        return FactionRelationCreateEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
