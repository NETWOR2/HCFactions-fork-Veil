package net.bfcode.bfhcf.visualise;

import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.Material;
import java.util.Iterator;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.common.collect.Maps;
import com.google.common.base.Predicate;
import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import com.google.common.collect.HashBasedTable;
import org.bukkit.Location;
import java.util.UUID;
import com.google.common.collect.Table;

import net.bfcode.bfbase.util.cuboid.Cuboid;

public class VisualiseHandler
{
    private Table<UUID, Location, VisualBlock> storedVisualises;
    
    public VisualiseHandler() {
        this.storedVisualises = HashBasedTable.create();
    }
    
    public Table<UUID, Location, VisualBlock> getStoredVisualises() {
        return this.storedVisualises;
    }
    
    @Deprecated
    public VisualBlock getVisualBlockAt(Player player, int x, int y, int z) throws NullPointerException {
        return this.getVisualBlockAt(player, new Location(player.getWorld(), (double)x, (double)y, (double)z));
    }
    
    public VisualBlock getVisualBlockAt(Player player, Location location) throws NullPointerException {
        Preconditions.checkNotNull(player, "Player cannot be null");
        Preconditions.checkNotNull(location, "Location cannot be null");
        Table<UUID, Location, VisualBlock> table = this.storedVisualises;
        synchronized (table) {
            return (VisualBlock)this.storedVisualises.get(player.getUniqueId(), location);
        }
    }
    
    public Map<Location, VisualBlock> getVisualBlocks(Player player) {
        Table<UUID, Location, VisualBlock> table = this.storedVisualises;
        synchronized (table) {
            return new HashMap<Location, VisualBlock>(this.storedVisualises.row(player.getUniqueId()));
        }
    }
    
    public Map<Location, VisualBlock> getVisualBlocks(Player player, VisualType visualType) {
        return (Map<Location, VisualBlock>)Maps.filterValues((Map)this.getVisualBlocks(player), (Predicate)new Predicate<VisualBlock>() {
            public boolean apply(VisualBlock visualBlock) {
                return visualType == visualBlock.getVisualType();
            }
        });
    }
    
    public LinkedHashMap<Location, VisualBlockData> generate(Player player, Cuboid cuboid, VisualType visualType, boolean canOverwrite) {
        HashSet<Location> locations = new HashSet<Location>(cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ());
        for (Block block : cuboid) {
            locations.add(block.getLocation());
        }
        return this.generate(player, locations, visualType, canOverwrite);
    }
    
    public LinkedHashMap<Location, VisualBlockData> generate(Player player, Iterable<Location> locations, VisualType visualType, boolean canOverwrite) {
        Table<UUID, Location, VisualBlock> table = this.storedVisualises;
        synchronized (table) {
            LinkedHashMap<Location, VisualBlockData> results = new LinkedHashMap<Location, VisualBlockData>();
            ArrayList<VisualBlockData> filled = visualType.blockFiller().bulkGenerate(player, locations);
            if (filled != null) {
                int count = 0;
                for (Location location : locations) {
                    Material previousType;
                    if ((canOverwrite || !this.storedVisualises.contains(player.getUniqueId(), location)) && !(previousType = location.getBlock().getType()).isSolid()) {
                        if (previousType != Material.AIR) {
                            continue;
                        }
                        VisualBlockData visualBlockData = filled.get(count++);
                        results.put(location, visualBlockData);
                        player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
                        this.storedVisualises.put(player.getUniqueId(), location, new VisualBlock(visualType, visualBlockData, location));
                    }
                }
            }
            return results;
        }
    }
    
    public boolean clearVisualBlock(Player player, Location location) {
        return this.clearVisualBlock(player, location, true);
    }
    
    public boolean clearVisualBlock(Player player, Location location, boolean sendRemovalPacket) {
        Table<UUID, Location, VisualBlock> table = this.storedVisualises;
        synchronized (table) {
            VisualBlock visualBlock = (VisualBlock)this.storedVisualises.remove(player.getUniqueId(), location);
            if (sendRemovalPacket && visualBlock != null) {
                Block block = location.getBlock();
                VisualBlockData visualBlockData = visualBlock.getBlockData();
                if (visualBlockData.getBlockType() != block.getType() || visualBlockData.getData() != block.getData()) {
                    player.sendBlockChange(location, block.getType(), block.getData());
                }
                return true;
            }
        }
        return false;
    }
    
    public Map<Location, VisualBlock> clearVisualBlocks(Player player) {
        return this.clearVisualBlocks(player, null, null);
    }
    
    public Map<Location, VisualBlock> clearVisualBlocks(Player player, VisualType visualType, Predicate<VisualBlock> predicate) {
        return this.clearVisualBlocks(player, visualType, predicate, true);
    }
    
    @Deprecated
    public Map<Location, VisualBlock> clearVisualBlocks(Player player, VisualType visualType, Predicate<VisualBlock> predicate, boolean sendRemovalPackets) {
        synchronized (this.storedVisualises) {
            if (!this.storedVisualises.containsRow(player.getUniqueId())) {
                return Collections.emptyMap();
            }
            Map<Location, VisualBlock> results = new HashMap<Location, VisualBlock>(this.storedVisualises.row(player.getUniqueId()));
            Map<Location, VisualBlock> removed = new HashMap<Location, VisualBlock>();
            for (Map.Entry<Location, VisualBlock> entry : results.entrySet()) {
                VisualBlock visualBlock = entry.getValue();
                if ((predicate == null || predicate.apply(visualBlock)) && (visualType == null || visualBlock.getVisualType() == visualType)) {
                    Location location = entry.getKey();
                    if (removed.put(location, visualBlock) != null) {
                        continue;
                    }
                    this.clearVisualBlock(player, location, sendRemovalPackets);
                }
            }
            return removed;
        }
    }
}
