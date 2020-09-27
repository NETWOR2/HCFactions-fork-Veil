package net.bfcode.bfhcf.kothgame.eotw;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.listener.BorderListener;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class EOTWHandler
{
    public static int BORDER_DECREASE_MINIMUM = 1000;
    public static int BORDER_DECREASE_AMOUNT = 200;
    public static long BORDER_DECREASE_TIME_MILLIS;
    public static int BORDER_DECREASE_TIME_SECONDS;
    public static String BORDER_DECREASE_TIME_WORDS;
    public static String BORDER_DECREASE_TIME_ALERT_WORDS;
    public static long EOTW_WARMUP_WAIT_MILLIS;
    public static int EOTW_WARMUP_WAIT_SECONDS;
    private static long EOTW_CAPPABLE_WAIT;
    private HCFaction plugin;
    private EotwRunnable runnable;
    
    public EOTWHandler(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public EotwRunnable getRunnable() {
        return this.runnable;
    }
    
    public boolean isEndOfTheWorld() {
        return this.isEndOfTheWorld(true);
    }
    
    public void setEndOfTheWorld(boolean yes) {
        if (yes == this.isEndOfTheWorld(false)) {
            return;
        }
        if (yes) {
            (this.runnable = new EotwRunnable(this.plugin.getServerHandler().getWorldBorder())).runTaskTimer((Plugin)this.plugin, 1L, 100L);
        }
        else if (this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
    }
    
    public boolean isEndOfTheWorld(boolean ignoreWarmup) {
        return this.runnable != null && (!ignoreWarmup || this.runnable.getElapsedMilliseconds() > 0L);
    }
    
    static {
        BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5L);
        BORDER_DECREASE_TIME_SECONDS = (int)(EOTWHandler.BORDER_DECREASE_TIME_MILLIS / 1000L);
        BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(EOTWHandler.BORDER_DECREASE_TIME_MILLIS, true, true);
        BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(EOTWHandler.BORDER_DECREASE_TIME_MILLIS / 2L, true, true);
        EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(5L);
        EOTW_WARMUP_WAIT_SECONDS = (int)(EOTWHandler.EOTW_WARMUP_WAIT_MILLIS / 6000L);
        EOTW_CAPPABLE_WAIT = TimeUnit.MINUTES.toMillis(5L);
    }
    
    public static class EotwRunnable extends BukkitRunnable
    {
        private static PotionEffect WITHER;
        private boolean hasInformedStarted;
        private long startStamp;
        private int borderSize;
        
        public EotwRunnable(int borderSize) {
            this.hasInformedStarted = false;
            this.borderSize = borderSize;
            this.startStamp = System.currentTimeMillis() + EOTWHandler.EOTW_WARMUP_WAIT_MILLIS;
        }
        
        public long getTimeUntilStarting() {
            long difference = System.currentTimeMillis() - this.startStamp;
            return (difference > 0L) ? 0L : Math.abs(difference);
        }
        
        public long getTimeUntilCappable() {
            return EOTWHandler.EOTW_CAPPABLE_WAIT - this.getElapsedMilliseconds();
        }
        
        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - this.startStamp;
        }
        
        public void run() {
            long elapsedMillis = this.getElapsedMilliseconds();
            int elapsedSeconds = (int)Math.round(elapsedMillis / 1000.0);
            if (!this.hasInformedStarted && elapsedSeconds >= 0) {
                for (Faction faction : HCFaction.getPlugin().getFactionManager().getFactions()) {
                    if (!(faction instanceof ClaimableFaction)) {
                        continue;
                    }
                    ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                    for (Claim claims : claimableFaction.getClaims()) {
                        claimableFaction.removeClaim(claims, (CommandSender)Bukkit.getConsoleSender());
                    }
                    claimableFaction.getClaims().clear();
                }
                this.hasInformedStarted = true;
                Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "              End Of The World");
                Bukkit.broadcastMessage(ChatColor.RED + "                      has begun!");
                Bukkit.broadcastMessage(ChatColor.GRAY + "");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "              All Faction claims have");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "                  been unclaimed!");
                Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                return;
            }
            if (elapsedMillis < 0L && elapsedMillis >= -EOTWHandler.EOTW_WARMUP_WAIT_MILLIS) {
                Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "End Of The World" + ChatColor.RED + " will start in " + HCFaction.getRemaining(Math.abs(elapsedMillis), true, false) + '.');
                return;
            }
            for (Player on : Bukkit.getOnlinePlayers()) {
                if (BorderListener.isWithinBorder(on.getLocation())) {
                    continue;
                }
                on.sendMessage(ChatColor.RED + "EOTW is active and your outside of the border. You will get wither.");
                on.addPotionEffect(EotwRunnable.WITHER, true);
            }
            if (HCFaction.getPlugin().getServerHandler().getWorldBorder() <= 1000) {
                return;
            }
            int newBorderSize = this.borderSize - 200;
            if (newBorderSize <= 1000) {
                return;
            }
            if (elapsedSeconds % EOTWHandler.BORDER_DECREASE_TIME_SECONDS == 0) {
                World.Environment normal = World.Environment.NORMAL;
                int borderSize = this.borderSize = newBorderSize;
                HCFaction.getPlugin().getServerHandler().setServerBorder(normal, borderSize);
                Bukkit.broadcastMessage(ChatColor.RED + "Border has been decreased to " + ChatColor.RED + newBorderSize + ChatColor.RED + " blocks.");
            }
            else if (elapsedSeconds % TimeUnit.MINUTES.toSeconds(5L) == 0L) {
                Bukkit.broadcastMessage(ChatColor.RED + "Border decreasing to " + ChatColor.RED + newBorderSize + ChatColor.RED + " blocks in " + ChatColor.RED + EOTWHandler.BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
            }
        }
        
        static {
            WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);
        }
    }
}
