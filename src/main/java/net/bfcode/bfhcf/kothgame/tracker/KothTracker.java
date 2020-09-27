package net.bfcode.bfhcf.kothgame.tracker;

import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.EventType;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.utils.DateTimeFormats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class KothTracker implements EventTracker {
    public static long DEFAULT_CAP_MILLIS;
    private static long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private HCFaction plugin;
    
    public KothTracker(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinnerKoth(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        int remainingSeconds = (int)(remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + "Someone §eis controlling " + ChatColor.GOLD + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }
    
    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }
    
    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        player.sendMessage(ChatColor.YELLOW + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    @Override
    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
    	// TODO Auto-generated method stub
    	
    }
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
    }
}
