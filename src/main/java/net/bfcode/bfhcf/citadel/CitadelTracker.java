package net.bfcode.bfhcf.citadel;

import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.EventType;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.tracker.EventTracker;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.DateTimeFormats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@SuppressWarnings("deprecation")
public class CitadelTracker implements EventTracker {
	
    public static long DEFAULT_CAP_MILLIS1;
    private static long MINIMUM_CONTROL_TIME_ANNOUNCE1;
    private HCFaction plugin;
    
    public CitadelTracker(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CITADEL;
    }
    
    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        CaptureZone captureZone = ((CitadelFaction)eventFaction).getCaptureZone();
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinnerCitadel(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        int remainingSeconds = (int)(remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.PALACE_FORMAT.format(remainingMillis) + ')');
        }
    }
    
    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.PALACE_FORMAT.format(eventTimer.getRemaining1()) + ')');
    }
    
    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        return true;
    }
    
    @Override
    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > CitadelTracker.MINIMUM_CONTROL_TIME_ANNOUNCE1) {
            Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.PALACE_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
    }
    
    static {
        DEFAULT_CAP_MILLIS1 = TimeUnit.MINUTES.toMillis(20L);
        MINIMUM_CONTROL_TIME_ANNOUNCE1 = TimeUnit.SECONDS.toMillis(25L);
    }
}
