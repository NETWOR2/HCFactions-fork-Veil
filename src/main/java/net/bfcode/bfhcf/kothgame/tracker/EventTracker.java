package net.bfcode.bfhcf.kothgame.tracker;

import org.bukkit.entity.Player;

import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.EventType;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker
{
    EventType getEventType();
    
    void tick(EventTimer p0, EventFaction p1);
    
    void onContest(EventFaction p0, EventTimer p1);
    
    boolean onControlTake(Player p0, CaptureZone p1);
    
    boolean onControlLoss(Player p0, CaptureZone p1, EventFaction p2);
    
    void stopTiming();
}
