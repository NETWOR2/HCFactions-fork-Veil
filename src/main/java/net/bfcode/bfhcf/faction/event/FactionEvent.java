package net.bfcode.bfhcf.faction.event;

import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.faction.type.Faction;

import org.bukkit.event.Event;

public abstract class FactionEvent extends Event
{
    protected Faction faction;
    
    public FactionEvent(Faction faction) {
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }
    
    FactionEvent(Faction faction, boolean async) {
        super(async);
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }
    
    public Faction getFaction() {
        return this.faction;
    }
}
