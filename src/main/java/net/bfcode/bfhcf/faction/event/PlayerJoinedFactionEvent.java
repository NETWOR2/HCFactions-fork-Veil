package net.bfcode.bfhcf.faction.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import net.bfcode.bfhcf.faction.type.PlayerFaction;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public class PlayerJoinedFactionEvent extends FactionEvent
{
    private static HandlerList handlers;
    private UUID uniqueID;
    private Optional<Player> player;
    
    public PlayerJoinedFactionEvent(Player player, PlayerFaction playerFaction) {
        super(playerFaction);
        this.player = (Optional<Player>)Optional.of(player);
        this.uniqueID = player.getUniqueId();
    }
    
    public PlayerJoinedFactionEvent(UUID playerUUID, PlayerFaction playerFaction) {
        super(playerFaction);
        this.uniqueID = playerUUID;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerJoinedFactionEvent.handlers;
    }
    
    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)this.faction;
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
    
    public HandlerList getHandlers() {
        return PlayerJoinedFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
