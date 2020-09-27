package net.bfcode.bfhcf.faction.claim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.bfcode.bfbase.drops.CaseInsensitiveMap;
import net.bfcode.bfbase.util.GenericUtils;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfbase.util.cuboid.NamedCuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;

@SuppressWarnings("rawtypes")
public class Claim extends NamedCuboid implements Cloneable, ConfigurationSerializable {
	
    private static Random RANDOM;
    private UUID claimUniqueID;
    private UUID factionUUID;
	private Map subclaims;
    private Faction faction;
    private boolean loaded;
    
    @SuppressWarnings("unchecked")
	public Claim(Map map) {
        super(map);
        this.subclaims = (Map)new CaseInsensitiveMap();
        this.loaded = false;
        this.name = (String) map.get("name");
        this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
        this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
        for (Subclaim subclaim : GenericUtils.createList(map.get("subclaims"), Subclaim.class)) {
            this.subclaims.put(subclaim.getName(), subclaim);
        }
    }
    
    public Claim(Faction faction, Location location) {
        super(location, location);
        this.subclaims = (Map)new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    public Claim(Faction faction, Location location1, Location location2) {
        super(location1, location2);
        this.subclaims = (Map)new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    public Claim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        super(world, x1, y1, z1, x2, y2, z2);
        this.subclaims = (Map)new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    public Claim(Faction faction, Cuboid cuboid) {
        super(cuboid);
        this.subclaims = (Map)new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    @SuppressWarnings("unchecked")
	public Map<String, Object> serialize() {
        Map map = super.serialize();
        map.put("name", this.name);
        map.put("claimUUID", this.claimUniqueID.toString());
        map.put("factionUUID", this.factionUUID.toString());
        map.put("subclaims", new ArrayList(this.subclaims.values()));
        return (Map<String, Object>)map;
    }
    
    private String generateName() {
        return String.valueOf(Claim.RANDOM.nextInt(899) + 100);
    }
    
    public UUID getClaimUniqueID() {
        return this.claimUniqueID;
    }
    
    public ClaimableFaction getFaction() {
        if (!this.loaded && this.faction == null) {
            this.faction = HCFaction.getPlugin().getFactionManager().getFaction(this.factionUUID);
            this.loaded = true;
        }
        return (this.faction instanceof ClaimableFaction) ? ((ClaimableFaction)this.faction) : null;
    }
    
    @SuppressWarnings("unchecked")
	public Collection<Subclaim> getSubclaims() {
        return this.subclaims.values();
    }
    
    public Subclaim getSubclaim(String name) {
        return (Subclaim) this.subclaims.get(name);
    }
    
    public String getFormattedName() {
        return this.getName() + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - (" + this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
    }
    
    public Claim clone() {
        return (Claim)super.clone();
    }
    
    static {
        RANDOM = new Random();
    }
}
