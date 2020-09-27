package net.bfcode.bfhcf.user;

import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.bukkit.event.Listener;

public abstract class AbstractUserManager implements Listener
{
    private static Pattern USERNAME_REGEX;
    protected HCFaction plugin;
    protected ConcurrentMap<UUID, FactionUser> inMemoryStorage;
    protected ConcurrentMap<UUID, FactionUser> onlineStorage;
    protected Map<String, UUID> uuidCache;
    
    public AbstractUserManager(HCFaction plugin) {
        this.uuidCache = Collections.synchronizedMap(new TreeMap<String, UUID>(String.CASE_INSENSITIVE_ORDER));
        this.inMemoryStorage = new ConcurrentHashMap<UUID, FactionUser>();
        this.onlineStorage = new ConcurrentHashMap<UUID, FactionUser>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.reloadUserData();
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        FactionUser factionUser = this.inMemoryStorage.get(uuid);
        if (factionUser == null) {
            factionUser = new FactionUser(uuid);
            this.inMemoryStorage.put(uuid, factionUser);
            this.saveUser(factionUser);
        }
        this.onlineStorage.put(uuid, factionUser);
        this.uuidCache.put(player.getName(), uuid);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.onlineStorage.remove(uuid);
    }
    
    public ConcurrentMap<UUID, FactionUser> getUsers() {
        return this.inMemoryStorage;
    }
    
    public FactionUser getUser(UUID uuid) {
        Preconditions.checkNotNull((Object)uuid);
        FactionUser factionUser;
        return ((factionUser = this.inMemoryStorage.get(uuid)) != null) ? factionUser : (((factionUser = this.onlineStorage.get(uuid)) != null) ? factionUser : this.insertAndReturn(uuid, new FactionUser(uuid)));
    }
    
    public FactionUser getIfContainedOffline(UUID uuid) {
        Preconditions.checkNotNull((Object)uuid);
        FactionUser factionUser;
        return ((factionUser = this.onlineStorage.get(uuid)) != null) ? factionUser : this.inMemoryStorage.get(uuid);
    }
    
    public FactionUser insertAndReturn(UUID uuid, FactionUser factionUser) {
        this.inMemoryStorage.put(uuid, factionUser);
        return factionUser;
    }
    
    public FactionUser getIfContains(UUID uuid) {
        return this.onlineStorage.get(uuid);
    }
    
    public UUID fetchUUID(String username) {
        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            return player.getUniqueId();
        }
        if (AbstractUserManager.USERNAME_REGEX.matcher(username).matches()) {
            return this.uuidCache.get(username);
        }
        return null;
    }
    
    public ConcurrentMap<UUID, FactionUser> getOnlineStorage() {
        return this.onlineStorage;
    }
    
    public void saveUser(FactionUser user) {
    }
    
    public abstract void saveUserData();
    
    public abstract void reloadUserData();
    
    static {
        USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
    }
}
