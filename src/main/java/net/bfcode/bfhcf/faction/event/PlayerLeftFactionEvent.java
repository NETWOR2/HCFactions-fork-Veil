package net.bfcode.bfhcf.faction.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.faction.event.cause.FactionLeaveCause;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public class PlayerLeftFactionEvent extends FactionEvent
{
    private static HandlerList handlers;
    private UUID uniqueID;
    private FactionLeaveCause cause;
    private Optional<Player> player;
    
    public PlayerLeftFactionEvent(Player player, PlayerFaction playerFaction, FactionLeaveCause cause) {
        super(playerFaction);
        this.player = (Optional<Player>)Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeftFactionEvent(UUID playerUUID, PlayerFaction playerFaction, FactionLeaveCause cause) {
        super(playerFaction);
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeftFactionEvent.handlers;
    }
    
    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)super.getFaction();
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
    
    public FactionLeaveCause getCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeftFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
