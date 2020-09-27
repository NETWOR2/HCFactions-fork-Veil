package net.bfcode.bfhcf.balance;

import java.util.LinkedHashMap;
import java.util.Set;
import org.bukkit.configuration.MemorySection;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.UUID;

import net.bfcode.bfbase.util.Config;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatFileEconomyManager implements EconomyManager
{
    private JavaPlugin plugin;
    private TObjectIntMap<UUID> balanceMap;
    private Config balanceConfig;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public FlatFileEconomyManager(JavaPlugin plugin) {
        this.balanceMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(10, 0.5f, 0);
        this.plugin = plugin;
        this.reloadEconomyData();
    }
    
    @Override
    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }
    
    @Override
    public int getBalance(UUID uuid) {
        return this.balanceMap.get(uuid);
    }
    
    @Override
    public int setBalance(UUID uuid, int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }
    
    @Override
    public int addBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }
    
    @Override
    public int subtractBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }
    
    @Override
    public void reloadEconomyData() {
        this.balanceConfig = new Config(this.plugin, "balances");
        Object object = this.balanceConfig.get("balances");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            Set<String> keys = (Set<String>)section.getKeys(false);
            for (String id : keys) {
                this.balanceMap.put(UUID.fromString(id), this.balanceConfig.getInt("balances." + id));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void saveEconomyData() {
        @SuppressWarnings("rawtypes")
		LinkedHashMap saveMap = new LinkedHashMap(this.balanceMap.size());
        this.balanceMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.balanceConfig.set("balances", saveMap);
        this.balanceConfig.save();
    }
}
