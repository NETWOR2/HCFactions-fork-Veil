package net.bfcode.bfhcf.faction.event;

import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.entity.Player;
import com.google.common.base.Optional;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class PlayerJoinFactionEvent extends FactionEvent implements Cancellable
{
    private static HandlerList handlers;
    private UUID uniqueID;
    private boolean cancelled;
    private Optional<Player> player;
    
    public PlayerJoinFactionEvent(Player player, PlayerFaction playerFaction) {
        super(playerFaction);
        Preconditions.checkNotNull(player, "Player cannot be null");
        this.player = (Optional<Player>)Optional.of(player);
        this.uniqueID = player.getUniqueId();
    }
    
    public PlayerJoinFactionEvent(UUID playerUUID, PlayerFaction playerFaction) {
        super(playerFaction);
        Preconditions.checkNotNull(playerUUID, "Player UUID cannot be null");
        this.uniqueID = playerUUID;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerJoinFactionEvent.handlers;
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
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerJoinFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
