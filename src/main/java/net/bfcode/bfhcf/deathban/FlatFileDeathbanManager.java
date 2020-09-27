package net.bfcode.bfhcf.deathban;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

import net.bfcode.bfbase.util.Config;
import net.bfcode.bfbase.util.PersistableLocation;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public class FlatFileDeathbanManager implements DeathbanManager
{
    @SuppressWarnings("unused")
	private static int MAX_DEATHBAN_MULTIPLIER = 300;
    private HCFaction plugin;
    private TObjectIntMap<UUID> livesMap;
    private Config livesConfig;
    
    public FlatFileDeathbanManager(HCFaction plugin) {
        this.plugin = plugin;
        this.reloadDeathbanData();
    }
    
    @Override
    public TObjectIntMap<UUID> getLivesMap() {
        return this.livesMap;
    }
    
    @Override
    public int getLives(UUID uuid) {
        return this.livesMap.get(uuid);
    }
    
    @Override
    public int setLives(UUID uuid, int lives) {
        this.livesMap.put(uuid, lives);
        return lives;
    }
    
    @Override
    public int addLives(UUID uuid, int amount) {
        return this.livesMap.adjustOrPutValue(uuid, amount, amount);
    }
    
    @Override
    public int takeLives(UUID uuid, int amount) {
        return this.setLives(uuid, this.getLives(uuid) - amount);
    }
    
    @Override
    public long getDeathBanMultiplier(Player player) {
        if (player.hasPermission("hcf.deathban.extra")) {
            for (int i = 5; i < 21600; --i) {
                if (player.hasPermission("hcf.deathban.seconds." + i)) {
                    return i / 1000;
                }
            }
        }
        return ConfigurationService.DEFAULT_DEATHBAN_DURATION;
    }
    
    @Override
    public Deathban applyDeathBan(Player player, String reason) {
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
        if (!factionAt.isDeathban()) {
            duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
        }
        if (player.hasPermission("deathban.insider")) {
            duration = 28800000L;
        }
        if (player.hasPermission("deathban.150m")) {
            duration = 9000000L;
        }
        if (player.hasPermission("deathban.120m")) {
            duration = 7200000L;
        }
        if (player.hasPermission("deathban.90m")) {
            duration = 5400000L;
        }
        if (player.hasPermission("deathban.60m")) {
            duration = 3600000L;
        }
        if (player.hasPermission("deathban.45m")) {
            duration = 2700000L;
        }
        if (player.hasPermission("deathban.40m")) {
            duration = 2700000L;
        }
        if (player.hasPermission("deathban.30m")) {
            duration = 1800000L;
        }
        if (player.hasPermission("deathban.25m")) {
            duration = 1500000L;
        }
        if (player.hasPermission("deathban.20m")) {
            duration = 1200000L;
        }
        if (player.hasPermission("deathban.15m")) {
            duration = 900000L;
        }
        if (player.hasPermission("deathban.10m")) {
            duration = 600000L;
        }
        if (player.hasPermission("deathban.5m")) {
            duration = 300000L;
        }
        if (player.hasPermission("deathban.1m")) {
            duration = 60000L;
        }
        return this.applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(FlatFileDeathbanManager.MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
    }
    
    @Override
    public Deathban applyDeathBan(UUID uuid, Deathban deathban) {
        this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void reloadDeathbanData() {
        this.livesConfig = new Config((JavaPlugin)this.plugin, "lives");
        Object object = this.livesConfig.get("lives");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            Set<String> keys = (Set<String>)section.getKeys(false);
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(keys.size(), 0.5f, 0);
            for (String id : keys) {
                this.livesMap.put(UUID.fromString(id), this.livesConfig.getInt(section.getCurrentPath() + "." + id));
            }
        }
        else {
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(10, 0.5f, 0);
        }
    }
    
    @Override
    public void saveDeathbanData() {
        Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
        this.livesMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.livesConfig.set("lives", saveMap);
        this.livesConfig.save();
    }
}
