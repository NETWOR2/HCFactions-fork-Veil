package net.bfcode.bfhcf.kothgame.faction;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import com.google.common.collect.ImmutableList;
import java.util.List;
import com.google.common.collect.ImmutableSet;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventType;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;
import java.util.EnumMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ConquestFaction extends CapturableFaction implements ConfigurationSerializable
{
    private EnumMap<ConquestZone, CaptureZone> captureZones;
    
    public ConquestFaction(String name) {
        super(name);
        this.captureZones = new EnumMap<ConquestZone, CaptureZone>(ConquestZone.class);
    }
    
    public ConquestFaction(Map<String, Object> map) {
        super(map);
        this.captureZones = new EnumMap<ConquestZone, CaptureZone>(ConquestZone.class);
        Object object;
        if ((object = map.get("red")) instanceof CaptureZone) {
            this.captureZones.put(ConquestZone.RED, (CaptureZone)object);
        }
        if ((object = map.get("green")) instanceof CaptureZone) {
            this.captureZones.put(ConquestZone.GREEN, (CaptureZone)object);
        }
        if ((object = map.get("blue")) instanceof CaptureZone) {
            this.captureZones.put(ConquestZone.BLUE, (CaptureZone)object);
        }
        if ((object = map.get("yellow")) instanceof CaptureZone) {
            this.captureZones.put(ConquestZone.YELLOW, (CaptureZone)object);
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        for (Map.Entry<ConquestZone, CaptureZone> entry : this.captureZones.entrySet()) {
            map.put(entry.getKey().name().toLowerCase(), entry.getValue());
        }
        return map;
    }
    
    public EventType getEventType() {
        return EventType.CONQUEST;
    }
    
    public ConquestZone getZone(CaptureZone captureZone) {
        for (Map.Entry<ConquestZone, CaptureZone> captureZoneEntry : this.captureZones.entrySet()) {
            if (captureZoneEntry.getValue() == captureZone) {
                return captureZoneEntry.getKey();
            }
        }
        return null;
    }
    
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender));
        for (Claim claim : this.claims) {
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' + (String)ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
        }
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public void setZone(ConquestZone conquestZone, CaptureZone captureZone) {
        switch (conquestZone) {
            case RED: {
                this.captureZones.put(ConquestZone.RED, captureZone);
                break;
            }
            case BLUE: {
                this.captureZones.put(ConquestZone.BLUE, captureZone);
                break;
            }
            case GREEN: {
                this.captureZones.put(ConquestZone.GREEN, captureZone);
                break;
            }
            case YELLOW: {
                this.captureZones.put(ConquestZone.YELLOW, captureZone);
                break;
            }
            default: {
                throw new AssertionError("Unsupported operation");
            }
        }
    }
    
    public CaptureZone getRed() {
        return this.captureZones.get(ConquestZone.RED);
    }
    
    public CaptureZone getGreen() {
        return this.captureZones.get(ConquestZone.GREEN);
    }
    
    public CaptureZone getBlue() {
        return this.captureZones.get(ConquestZone.BLUE);
    }
    
    public CaptureZone getYellow() {
        return this.captureZones.get(ConquestZone.YELLOW);
    }
    
    public Collection<ConquestZone> getConquestZones() {
        return (Collection<ConquestZone>)ImmutableSet.copyOf((Collection)this.captureZones.keySet());
    }
    
    public List<CaptureZone> getCaptureZones() {
        return (List<CaptureZone>)ImmutableList.copyOf((Collection)this.captureZones.values());
    }
    
    public enum ConquestZone
    {
        RED(ChatColor.RED, "Red"), 
        BLUE(ChatColor.AQUA, "Blue"), 
        YELLOW(ChatColor.YELLOW, "Yellow"), 
        GREEN(ChatColor.GREEN, "Green");
        
        private static Map<String, ConquestZone> BY_NAME;
        private String name;
        private ChatColor color;
        
        private ConquestZone(ChatColor color, String name) {
            this.color = color;
            this.name = name;
        }
        
        public static ConquestZone getByName(String name) {
            return ConquestZone.BY_NAME.get(name.toUpperCase());
        }
        
        public static Collection<String> getNames() {
            return new ArrayList<String>(ConquestZone.BY_NAME.keySet());
        }
        
        public ChatColor getColor() {
            return this.color;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getDisplayName() {
            return this.color.toString() + this.name;
        }
        
        static {
            ImmutableMap.Builder<String, ConquestZone> builder = ImmutableMap.builder();
            for (ConquestZone zone : values()) {
                builder.put(zone.name().toUpperCase(), zone);
            }
            BY_NAME = (Map)builder.build();
        }
    }
}
