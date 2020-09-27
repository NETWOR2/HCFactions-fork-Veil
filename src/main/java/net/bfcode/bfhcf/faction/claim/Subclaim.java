package net.bfcode.bfhcf.faction.claim;

import java.util.stream.Collectors;

import org.bukkit.World;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.bfcode.bfbase.util.GenericUtils;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.faction.type.Faction;

public class Subclaim extends Claim implements Cloneable, ConfigurationSerializable
{
    private Set<UUID> accessibleMembers;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Subclaim(Map<String, Object> map) {
        super(map);
        (this.accessibleMembers = new HashSet<UUID>()).addAll(GenericUtils.createList(map.get("accessibleMembers"), (Class)String.class));
    }
    
    public Subclaim(Faction faction, Location location) {
        super(faction, location, location);
        this.accessibleMembers = new HashSet<UUID>();
    }
    
    public Subclaim(Faction faction, Location location1, Location location2) {
        super(faction, location1, location2);
        this.accessibleMembers = new HashSet<UUID>();
    }
    
    public Subclaim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        super(faction, world, x1, y1, z1, x2, y2, z2);
        this.accessibleMembers = new HashSet<UUID>();
    }
    
    public Subclaim(Faction faction, Cuboid cuboid) {
        this(faction, cuboid.getWorld(), cuboid.getX1(), cuboid.getY1(), cuboid.getZ1(), cuboid.getX2(), cuboid.getY2(), cuboid.getZ2());
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.remove("subclaims");
        map.put("accessibleMembers", this.accessibleMembers.stream().map(UUID::toString).collect(Collectors.toList()));
        return map;
    }
    
    public Set<UUID> getAccessibleMembers() {
        return this.accessibleMembers;
    }
    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subclaim)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Subclaim blocks = (Subclaim)o;
        return (this.accessibleMembers != null) ? this.accessibleMembers.equals(blocks.accessibleMembers) : (blocks.accessibleMembers == null);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.accessibleMembers != null) ? this.accessibleMembers.hashCode() : 0);
        return result;
    }
    
    @Override
    public Subclaim clone() {
        return (Subclaim)super.clone();
    }
}
