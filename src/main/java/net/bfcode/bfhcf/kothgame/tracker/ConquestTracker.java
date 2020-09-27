package net.bfcode.bfhcf.kothgame.tracker;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.FactionRemoveEvent;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.EventType;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;

public class ConquestTracker implements EventTracker, Listener
{
    public static long DEFAULT_CAP_MILLIS;
    private static long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;
    private Map<PlayerFaction, Integer> factionPointsMap;
    private HCFaction plugin;
    private int conquestPoints;
    private int conquestLossPoints;
    
    public ConquestTracker(HCFaction ins) {
        this.factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<PlayerFaction, Integer>());
        this.plugin = ins;
    	this.setConquestPoints(ConfigurationService.KIT_MAP ? 150 : 250);
    	this.setConquestLossPoints(ConfigurationService.KIT_MAP ? 20 : 25);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            synchronized (this.factionPointsMap) {
                this.factionPointsMap.remove(faction);
            }
        }
    }
    
    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return (Map<PlayerFaction, Integer>)ImmutableMap.copyOf(this.factionPointsMap);
    }
    
    public int getPoints(PlayerFaction faction) {
        synchronized (this.factionPointsMap) {
            return ObjectUtils.firstNonNull(this.factionPointsMap.get(faction), 0);
        }
    }
    
    public int setPoints(PlayerFaction faction, int amount) {
        if (amount < 0) {
            return amount;
        }
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.put(faction, amount);
            List<Map.Entry<PlayerFaction, Integer>> entries = Ordering.from(ConquestTracker.POINTS_COMPARATOR).sortedCopy(this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for (Map.Entry<PlayerFaction, Integer> entry : entries) {
                this.factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return amount;
    }
    
    public int takePoints(PlayerFaction faction, int amount) {
        return this.setPoints(faction, this.getPoints(faction) - amount);
    }
    
    public int addPoints(PlayerFaction faction, int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }
    
    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
        List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) {
                continue;
            }
            long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                UUID uuid = cappingPlayer.getUniqueId();
                PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    int newPoints = this.addPoints(playerFaction, 1);
                    if (newPoints >= conquestPoints) {
                        synchronized (this.factionPointsMap) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinnerConquest(cappingPlayer);
                        return;
                    }
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + newPoints + '/' + conquestPoints + ')');
                }
                return;
            }
            int remainingSeconds = (int)Math.round(remainingMillis / 1000.0);
            if (remainingSeconds % 15 != 0) {
                continue;
            }
            UUID uuid2 = cappingPlayer.getUniqueId();
            PlayerFaction playerFaction2 = this.plugin.getFactionManager().getPlayerFaction(uuid2);
            playerFaction2.broadcast(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + cappingPlayer.getName() + "'s §eattempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)");
            cappingPlayer.sendMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.YELLOW + "Attempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)");
        }
    }
    
    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + eventFaction.getName() + " §ecan now be contested.");
    }
    
    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > ConquestTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + player.getName() + " §ewas knocked off " + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.clear();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Faction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            Player player = event.getEntity();
            PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            if (playerFaction != null) {
                int oldPoints = this.getPoints(playerFaction);
                if (oldPoints == 0) {
                    return;
                }
                if (this.getPoints(playerFaction) <= this.conquestLossPoints) {
                    this.setPoints(playerFaction, 0);
                }
                else {
                    this.takePoints(playerFaction, this.conquestLossPoints);
                }
                event.setDeathMessage(ChatColor.YELLOW + "§8[§6§l" + currentEventFac.getName() + "§8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " lost " + ChatColor.RED + Math.min(this.conquestLossPoints, oldPoints) + ChatColor.YELLOW + " points because " + player.getName() + " died." + ChatColor.RED + " (" + this.getPoints(playerFaction) + '/' + conquestPoints + ')' + ChatColor.YELLOW + '.');
            }
        }
    }
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        POINTS_COMPARATOR = ((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    }
    
    public int getConquestPoints() {
		return conquestPoints;
	}
    
    public void setConquestPoints(int conquestPoints) {
		this.conquestPoints = conquestPoints;
	}
    
    public int getConquestLossPoints() {
    	return conquestLossPoints;
    }
    
    public int setConquestLossPoints(int conquestLossPoints) {
    	return this.conquestLossPoints = conquestLossPoints;
    }
}
