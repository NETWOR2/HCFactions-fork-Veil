package net.bfcode.bfhcf.faction.type;

import java.util.Map;
import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.bfcode.bfhcf.utils.ConfigurationService;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for (World world : Bukkit.getWorlds()) {
            World.Environment environment = world.getEnvironment();
            if (environment == World.Environment.THE_END) {
                continue;
            }
            ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment());
        }
    }
    
    public SpawnFaction(Map<String, Object> map) {
        super(map);
    }
    
    public boolean isDeathban() {
        return false;
    }
}
