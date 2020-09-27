package net.bfcode.bfhcf.kothgame;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelTracker;
import net.bfcode.bfhcf.kothgame.tracker.ConquestTracker;
import net.bfcode.bfhcf.kothgame.tracker.EventTracker;
import net.bfcode.bfhcf.kothgame.tracker.KothTracker;

@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
public enum EventType {
	
    CONQUEST("Conquest", new ConquestTracker(HCFaction.getPlugin())), 
    CITADEL("Citadel", new CitadelTracker(HCFaction.getPlugin())), 
    KOTH("Koth", new KothTracker(HCFaction.getPlugin()));
    
    private static ImmutableMap<String, EventType> byDisplayName;
	private EventTracker eventTracker;
    private String displayName;
    
    private EventType(String displayName, EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }
    
    @Deprecated
    public static EventType getByDisplayName(String name) {
        return (EventType)EventType.byDisplayName.get(name.toLowerCase());
    }
    
    public EventTracker getEventTracker() {
        return this.eventTracker;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    static {
        ImmutableBiMap.Builder builder = new ImmutableBiMap.Builder();
        for (EventType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }
}
