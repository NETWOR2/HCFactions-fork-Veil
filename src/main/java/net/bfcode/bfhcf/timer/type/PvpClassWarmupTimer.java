package net.bfcode.bfhcf.timer.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.TimerRunnable;
import net.bfcode.bfhcf.user.Config;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class PvpClassWarmupTimer extends PlayerTimer implements Listener {
	
    protected Map<UUID, PvpClass> classWarmups;
    
    private HCFaction plugin;
    
    public PvpClassWarmupTimer(HCFaction plugin) {
        super(ConfigurationService.PVP_CLASS_WARMUP_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.pvp-class-warmup-time")), false);
        this.classWarmups = new HashMap<UUID, PvpClass>();
        this.plugin = plugin;
        this.classupdate();
    }
    
    @Deprecated
    public void classupdate() {
    	new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player players : Bukkit.getOnlinePlayers()) {
					PvpClassWarmupTimer.this.attemptEquip(players);
				}
			}
		}.runTaskLater(this.plugin, 10L);
    }
    
    @Override
    public void onDisable(Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.PVP_CLASS_WARMUP_COLOUR;
    }
    
    @Override
    public TimerRunnable clearCooldown(UUID playerUUID) {
        TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        PvpClass hCFClass = this.classWarmups.remove(userUUID);
        Preconditions.checkNotNull(hCFClass, "Attempted to equip a class for %s, but nothing was added", new Object[] { player.getName() });
        this.plugin.getPvpClassManager().setEquippedClass(player, hCFClass);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(EquipmentSetEvent event) {
        HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player)humanEntity);
        }
    }
    
    private void attemptEquip(Player player) {
    	PvpClass current = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        }
        else if ((current = this.classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.clearCooldown(player.getUniqueId());
        }
        Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
        for (PvpClass hCFClass : pvpClasses) {
            if (hCFClass.isApplicableFor(player)) {
                this.classWarmups.put(player.getUniqueId(), hCFClass);
                this.setCooldown(player, player.getUniqueId(), hCFClass.getWarmupDelay(), false);
                break;
            }
        }
    }
}
