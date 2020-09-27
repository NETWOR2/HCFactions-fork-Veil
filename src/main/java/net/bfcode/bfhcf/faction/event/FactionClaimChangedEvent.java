package net.bfcode.bfhcf.faction.event;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.event.cause.ClaimChangeCause;

import org.bukkit.event.Event;

public class FactionClaimChangedEvent extends Event
{
    private static HandlerList handlers;
    private CommandSender sender;
    private ClaimChangeCause cause;
    private Collection<Claim> affectedClaims;
    
    public FactionClaimChangedEvent(CommandSender sender, ClaimChangeCause cause, Collection<Claim> affectedClaims) {
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = affectedClaims;
    }
    
    public static HandlerList getHandlerList() {
        return FactionClaimChangedEvent.handlers;
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public ClaimChangeCause getCause() {
        return this.cause;
    }
    
    public Collection<Claim> getAffectedClaims() {
        return this.affectedClaims;
    }
    
    public HandlerList getHandlers() {
        return FactionClaimChangedEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
