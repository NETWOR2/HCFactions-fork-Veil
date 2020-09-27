package net.bfcode.bfhcf.timer;

import java.util.Collection;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import java.util.HashSet;

import org.bukkit.plugin.java.JavaPlugin;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.timer.type.ArcherTimer;
import net.bfcode.bfhcf.timer.type.EnderPearlTimer;
import net.bfcode.bfhcf.timer.type.GoldenAppleTimer;
import net.bfcode.bfhcf.timer.type.LogoutTimer;
import net.bfcode.bfhcf.timer.type.NotchAppleTimer;
import net.bfcode.bfhcf.timer.type.PvpClassWarmupTimer;
import net.bfcode.bfhcf.timer.type.PvpProtectionTimer;
import net.bfcode.bfhcf.timer.type.SpawnTagTimer;
import net.bfcode.bfhcf.timer.type.StuckTimer;
import net.bfcode.bfhcf.timer.type.TeleportTimer;
import net.bfcode.bfhcf.user.Config;

import java.util.Set;

import org.bukkit.event.Listener;

public class TimerManager implements Listener
{
	public LogoutTimer logoutTimer;
    public EnderPearlTimer enderPearlTimer;
    public NotchAppleTimer notchAppleTimer;
    public GoldenAppleTimer goldenAppleTimer;
    public PvpProtectionTimer pvpProtectionTimer;
    public PvpClassWarmupTimer pvpClassWarmupTimer;
    public StuckTimer stuckTimer;
    public SpawnTagTimer spawnTagTimer;
    public TeleportTimer teleportTimer;
    public EventTimer eventTimer;
    public ArcherTimer archerTimer;
    public SOTWTimer sotwTimer;
    private Set<Timer> timers;
    private JavaPlugin plugin;
    
    public TimerManager(HCFaction plugin) {
        this.timers = new HashSet<Timer>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
        this.registerTimer(this.sotwTimer = new SOTWTimer());
        this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
        this.registerTimer(this.logoutTimer = new LogoutTimer());
        this.registerTimer(this.notchAppleTimer = new NotchAppleTimer(plugin));
        this.registerTimer(this.goldenAppleTimer = new GoldenAppleTimer(plugin));
        this.registerTimer(this.stuckTimer = new StuckTimer());
        this.registerTimer(this.pvpProtectionTimer = new PvpProtectionTimer(plugin));
        this.registerTimer(this.spawnTagTimer = new SpawnTagTimer(plugin));
        this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
        this.registerTimer(this.eventTimer = new EventTimer(plugin));
        this.registerTimer(this.pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
    }
    
    public void enable() {
        for (Timer timer : this.timers) {
            timer.load(new Config(HCFaction.getPlugin(), "timer.yml"));
        }
    }
    
    public void disable() {
        for (Timer timer : this.timers) {
            timer.onDisable(new Config(HCFaction.getPlugin(), "timer.yml"));
        }
    }
    
    public Collection<Timer> getTimers() {
        return this.timers;
    }
    
    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener)timer, this.plugin);
        }
    }
    
    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }
    
    public EventTimer getEventTimer() {
        return eventTimer;
    }
}
