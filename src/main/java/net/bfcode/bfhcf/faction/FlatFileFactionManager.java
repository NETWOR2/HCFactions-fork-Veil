package net.bfcode.bfhcf.faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

import net.bfcode.bfbase.drops.CaseInsensitiveMap;
import net.bfcode.bfbase.util.Config;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.cuboid.CoordinatePair;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.event.FactionClaimChangedEvent;
import net.bfcode.bfhcf.faction.event.FactionCreateEvent;
import net.bfcode.bfhcf.faction.event.FactionRelationRemoveEvent;
import net.bfcode.bfhcf.faction.event.FactionRemoveEvent;
import net.bfcode.bfhcf.faction.event.FactionRenameEvent;
import net.bfcode.bfhcf.faction.event.PlayerJoinedFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeftFactionEvent;
import net.bfcode.bfhcf.faction.event.cause.ClaimChangeCause;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.EndPortalFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.faction.type.WarzoneFaction;
import net.bfcode.bfhcf.faction.type.WildernessFaction;
import net.bfcode.bfhcf.mountain.DestroyTheCoreFaction;
import net.bfcode.bfhcf.mountain.GlowstoneFaction;
import net.bfcode.bfhcf.mountain.OreFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class FlatFileFactionManager implements Listener, FactionManager
{
    private WarzoneFaction warzone;
    private WildernessFaction wilderness;
    private Map<CoordinatePair, Claim> claimPositionMap;
    private Map<UUID, UUID> factionPlayerUuidMap;
    private Map<UUID, Faction> factionUUIDMap;
    private Map<String, UUID> factionNameMap;
    private HCFaction plugin;
    private Config config;
    
    public FlatFileFactionManager(HCFaction plugin) {
        this.claimPositionMap = new HashMap<CoordinatePair, Claim>();
        this.factionPlayerUuidMap = new ConcurrentHashMap<UUID, UUID>();
        this.factionUUIDMap = new HashMap<UUID, Faction>();
        this.factionNameMap = (Map<String, UUID>)new CaseInsensitiveMap();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.warzone = new WarzoneFaction();
        this.wilderness = new WildernessFaction();
        this.reloadFactionData();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        this.factionPlayerUuidMap.put(event.getUniqueID(), event.getFaction().getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        this.factionPlayerUuidMap.remove(event.getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        this.factionNameMap.remove(event.getOriginalName());
        this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(FactionClaimChangedEvent event) {
        for (Claim claim : event.getAffectedClaims()) {
            this.cacheClaim(claim, event.getCause());
        }
    }
    
    @Deprecated
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }
    
    public List<Faction> getFactions() {
        List<Faction> asd = new ArrayList<Faction>();
        for (Faction fac : this.factionUUIDMap.values()) {
            asd.add(fac);
        }
        return asd;
    }
    
    public Claim getClaimAt(World world, int x, int z) {
        return this.claimPositionMap.get(new CoordinatePair(world, x, z));
    }
    
    public Claim getClaimAt(Location location) {
        return this.getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }
    
    public Faction getFactionAt(World world, int x, int z) {
        World.Environment environment = world.getEnvironment();
        Claim claim = this.getClaimAt(world, x, z);
        if (claim != null) {
            Faction faction = claim.getFaction();
            if (faction != null) {
                return faction;
            }
        }
        if (environment == World.Environment.THE_END) {
            return this.warzone;
        }
        int warzoneRadius = ConfigurationService.WARZONE_RADIUS * (ConfigurationService.BORDER_SIZES.get(environment) / ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL));
        return (Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius) ? this.wilderness : this.warzone;
    }
    
    public Faction getFactionAt(Location location) {
        return this.getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }
    
    public Faction getFactionAt(Block block) {
        return this.getFactionAt(block.getLocation());
    }
    
    public Faction getFaction(String factionName) {
        UUID uuid = this.factionNameMap.get(factionName);
        return (uuid == null) ? null : this.factionUUIDMap.get(uuid);
    }
    
    public Faction getFaction(UUID factionUUID) {
        return this.factionUUIDMap.get(factionUUID);
    }
    
    public PlayerFaction getPlayerFaction(UUID playerUUID) {
        UUID uuid = this.factionPlayerUuidMap.get(playerUUID);
        Faction faction = (uuid == null) ? null : this.factionUUIDMap.get(uuid);
        return (faction instanceof PlayerFaction) ? ((PlayerFaction)faction) : null;
    }
    
    public PlayerFaction getPlayerFaction(Player player) {
        return this.getPlayerFaction(player.getUniqueId());
    }
    
    public PlayerFaction getContainingPlayerFaction(String search) {
        OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search)) : Bukkit.getOfflinePlayer(search);
        return (target.hasPlayedBefore() || target.isOnline()) ? this.getPlayerFaction(target.getUniqueId()) : null;
    }
    
    public Faction getContainingFaction(String search) {
        Faction faction = this.getFaction(search);
        if (faction != null) {
            return faction;
        }
        UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId();
        if (playerUUID != null) {
            return this.getPlayerFaction(playerUUID);
        }
        return null;
    }
    
    public boolean containsFaction(Faction faction) {
        return this.factionNameMap.containsKey(faction.getName());
    }
    
    public boolean createFaction(Faction faction) {
        return this.createFaction(faction, (CommandSender)Bukkit.getConsoleSender());
    }
    
    public boolean createFaction(Faction faction, CommandSender sender) {
        if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
            return false;
        }
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        if (faction instanceof PlayerFaction && sender instanceof Player) {
            Player player = (Player)sender;
            PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
                return false;
            }
        }
        FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
        Bukkit.getPluginManager().callEvent((Event)createEvent);
        return !createEvent.isCancelled();
    }
    
    public boolean removeFaction(Faction faction, CommandSender sender) {
        if (this.factionUUIDMap.remove(faction.getUniqueID()) == null) {
            return false;
        }
        this.factionNameMap.remove(faction.getName());
        FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent((Event)removeEvent);
        if (removeEvent.isCancelled()) {
            return false;
        }
        if (faction instanceof ClaimableFaction) {
            Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, ((ClaimableFaction)faction).getClaims()));
        }
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)faction;
            for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
                Bukkit.getPluginManager().callEvent((Event)new FactionRelationRemoveEvent(playerFaction, ally, Relation.ENEMY));
                ally.getRelations().remove(faction.getUniqueID());
            }
        }
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)faction;
            for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
                ally.getRelations().remove(faction.getUniqueID());
            }
            for (UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.setMember(uuid, null, true);
            }
        }
        return true;
    }
    
    private void cacheClaim(Claim claim, ClaimChangeCause cause) {
        Preconditions.checkNotNull(claim, "Claim cannot be null");
        Preconditions.checkNotNull(cause, "Cause cannot be null");
        Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE, "Cannot cache claims of resize type");
        World world = claim.getWorld();
        if (world == null) {
            return;
        }
        int minX = Math.min(claim.getX1(), claim.getX2());
        int maxX = Math.max(claim.getX1(), claim.getX2());
        int minZ = Math.min(claim.getZ1(), claim.getZ2());
        int maxZ = Math.max(claim.getZ1(), claim.getZ2());
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                CoordinatePair coordinatePair = new CoordinatePair(world, x, z);
                if (cause == ClaimChangeCause.CLAIM) {
                    this.claimPositionMap.put(coordinatePair, claim);
                }
                else if (cause == ClaimChangeCause.UNCLAIM) {
                    this.claimPositionMap.remove(coordinatePair);
                }
            }
        }
    }
    
    private void cacheFaction(Faction faction) {
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        this.factionUUIDMap.put(faction.getUniqueID(), faction);
        if (faction instanceof ClaimableFaction) {
            ClaimableFaction claimableFaction = (ClaimableFaction)faction;
            for (Claim claim : claimableFaction.getClaims()) {
                this.cacheClaim(claim, ClaimChangeCause.CLAIM);
            }
        }
        if (faction instanceof PlayerFaction) {
            for (FactionMember factionMember : ((PlayerFaction)faction).getMembers().values()) {
                this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
            }
        }
    }
    
    public void reloadFactionData() {
        this.factionNameMap.clear();
        this.config = new Config((JavaPlugin)this.plugin, "factions");
        Object object = this.config.get("factions");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            for (String factionName : section.getKeys(false)) {
                Object next = this.config.get(section.getCurrentPath() + '.' + factionName);
                if (next instanceof Faction) {
                    this.cacheFaction((Faction)next);
                }
            }
        }
        else if (object instanceof List) {
            List list = (List)object;
            for (Object next2 : list) {
                if (next2 instanceof Faction) {
                    this.cacheFaction((Faction)next2);
                }
            }
        }
        Set<Faction> adding = new HashSet<Faction>();
        if (!this.factionNameMap.containsKey("Warzone")) {
            adding.add(new WarzoneFaction());
        }
        if (!this.factionNameMap.containsKey("Spawn")) {
            adding.add(new SpawnFaction());
        }
        if (!this.factionNameMap.containsKey("NorthRoad")) {
            adding.add(new RoadFaction.NorthRoadFaction());
        }
        if (!this.factionNameMap.containsKey("EastRoad")) {
            adding.add(new RoadFaction.EastRoadFaction());
        }
        if (!this.factionNameMap.containsKey("WestRoad")) {
            adding.add(new RoadFaction.WestRoadFaction());
        }
        if (!this.factionNameMap.containsKey("SouthRoad")) {
            adding.add(new RoadFaction.SouthRoadFaction());
        }
        if (!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction.EndPortalFaction1());
        }
        if (!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction.EndPortalFaction2());
        }
        if (!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction.EndPortalFaction3());
        }
        if (!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction.EndPortalFaction4());
        }
        if(!this.factionNameMap.containsKey("Glowstone")) {
        	adding.add(new GlowstoneFaction(plugin));
        }
        if(!this.factionNameMap.containsKey("OreFaction")) {
        	adding.add(new OreFaction(plugin));
        }
        if (!this.factionNameMap.containsKey("Wilderness")) {
            adding.add(new WildernessFaction());
        }
        if (!this.factionNameMap.containsKey("DTC")) {
            adding.add(new DestroyTheCoreFaction(plugin));
        }
        for (Faction added : adding) {
            this.cacheFaction(added);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Faction " + added.getName() + " not found, created.");
        }
    }
    
    public void saveFactionData() {
        this.config.set("factions", new ArrayList(this.factionUUIDMap.values()));
        this.config.save();
    }
}
