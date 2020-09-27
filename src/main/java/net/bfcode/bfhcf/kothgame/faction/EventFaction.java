package net.bfcode.bfhcf.kothgame.faction;

import java.util.List;
import org.bukkit.Location;

import java.util.Collection;

import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventType;

import org.bukkit.ChatColor;

import java.util.Map;

public abstract class EventFaction extends ClaimableFaction
{
    public EventFaction(String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public EventFaction(Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
    }
    
    @Override
    public String getDisplayName(Faction faction) {
        if (this.getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + this.getName() + " KOTH";
        }
        return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + this.getEventType().getDisplayName();
    }
    
    @Override
    public String getDisplayName(CommandSender sender) {
        if (this.getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + this.getName() + " KOTH";
        }
        return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + this.getEventType().getDisplayName();
    }
    
    public String getDisplayName1(Faction faction) {
        if (this.getEventType() == EventType.CITADEL) {
            return ChatColor.LIGHT_PURPLE.toString() + this.getName() + ' ' + this.getEventType().getDisplayName();
        }
        return ChatColor.DARK_PURPLE + this.getEventType().getDisplayName();
    }
    
    public void setClaim(Cuboid cuboid, CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        Location min = cuboid.getMinimumPoint();
        min.setY(0.0);
        Location max = cuboid.getMaximumPoint();
        max.setY(256.0);
        this.addClaim(new Claim(this, min, max), sender);
    }
    
    public abstract EventType getEventType();
    
    public abstract List<CaptureZone> getCaptureZones();
}
