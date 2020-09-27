package net.bfcode.bfhcf.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.bfcode.bfbase.util.Config;
import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;

public class UserManager implements Listener
{
    private HCFaction plugin;
    private Map<UUID, FactionUser> users;
    private Config userConfig;
    
    public UserManager(HCFaction plugin) {
        this.users = new HashMap<UUID, FactionUser>();
        this.plugin = plugin;
        this.reloadUserData();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.users.putIfAbsent(uuid, new FactionUser(uuid));
    }
    
    public Map<UUID, FactionUser> getUsers() {
        return this.users;
    }
    
    public FactionUser getUserAsync(UUID uuid) {
        synchronized (this.users) {
            FactionUser revert = new FactionUser(uuid);
            FactionUser user = this.users.putIfAbsent(uuid, revert);
            return ObjectUtils.firstNonNull(user, revert);
        }
    }
    
    public FactionUser getUser(UUID uuid) {
        FactionUser revert = new FactionUser(uuid);
        FactionUser user = this.users.putIfAbsent(uuid, revert);
        return ObjectUtils.firstNonNull(user, revert);
    }
    
    public void reloadUserData() {
        this.userConfig = new Config((JavaPlugin)this.plugin, "faction-users");
        Object object = this.userConfig.get("users");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            Collection<String> keys = (Collection<String>)section.getKeys(false);
            for (String id : keys) {
                this.users.put(UUID.fromString(id), (FactionUser)this.userConfig.get(section.getCurrentPath() + '.' + id));
            }
        }
    }
    
    public void saveUserData() {
        Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
        LinkedHashMap<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
        for (Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("users", (Object)saveMap);
        this.userConfig.save();
    }
}
