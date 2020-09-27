package net.bfcode.bfhcf.faction.event;

import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.faction.CapturableFaction;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class CaptureZoneEnterEvent extends FactionEvent implements Cancellable
{
    private static HandlerList handlers;
    private CaptureZone captureZone;
    private Player player;
    private boolean cancelled;
    
    public CaptureZoneEnterEvent(Player player, CapturableFaction capturableFaction, CaptureZone captureZone) {
        super(capturableFaction);
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        Preconditions.checkNotNull((Object)captureZone, (Object)"Capture zone cannot be null");
        this.captureZone = captureZone;
        this.player = player;
    }
    
    public static HandlerList getHandlerList() {
        return CaptureZoneEnterEvent.handlers;
    }
    
    @Override
    public CapturableFaction getFaction() {
        return (CapturableFaction)super.getFaction();
    }
    
    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return CaptureZoneEnterEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
