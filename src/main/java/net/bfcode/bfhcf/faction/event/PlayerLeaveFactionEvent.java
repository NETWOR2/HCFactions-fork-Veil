package net.bfcode.bfhcf.faction.event;

import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.faction.event.cause.FactionLeaveCause;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class PlayerLeaveFactionEvent extends FactionEvent implements Cancellable
{
    private static HandlerList handlers;
    private UUID uniqueID;
    private FactionLeaveCause cause;
    private boolean cancelled;
    private Optional<Player> player;
    
    public PlayerLeaveFactionEvent(Player player, PlayerFaction playerFaction, FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull(player, "Player cannot be null");
        Preconditions.checkNotNull(playerFaction, "Player faction cannot be null");
        Preconditions.checkNotNull("Leave cause cannot be null");
        this.player = (Optional<Player>)Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeaveFactionEvent(UUID playerUUID, PlayerFaction playerFaction, FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull(playerUUID, "Player UUID cannot be null");
        Preconditions.checkNotNull(playerFaction, "Player faction cannot be null");
        Preconditions.checkNotNull("Leave cause cannot be null");
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = (Optional<Player>)Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public FactionLeaveCause getLeaveCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    static {
        handlers = new HandlerList();
    }
}
