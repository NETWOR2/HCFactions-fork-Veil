package net.bfcode.bfhcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

public class OPKeyAllTimer {
	
    private OPKeyAllRunnable keyallRunnable;
    
    public boolean cancel() {
        if (this.keyallRunnable != null) {
            this.keyallRunnable.cancel();
            this.keyallRunnable = null;
            return true;
        }
        return false;
    }
    
    public void start(long millis) {
        if (this.keyallRunnable == null) {
            (this.keyallRunnable = new OPKeyAllRunnable(this, millis)).runTaskLater(HCFaction.getPlugin(), millis / 50L);
        }
    }
    
    public OPKeyAllRunnable getOpKeyAllRunnable() {
        return this.keyallRunnable;
    }
    
    static /* synthetic */ void access$0(OPKeyAllTimer keyallTimer, OPKeyAllRunnable keyallRunnable) {
        keyallTimer.keyallRunnable = keyallRunnable;
    }
    
    public static class OPKeyAllRunnable extends BukkitRunnable
    {
        private OPKeyAllTimer keyallTimer;
        private long startMillis;
        private long endMillis;
        
        public OPKeyAllRunnable(OPKeyAllTimer keyallTimer, long duration) {
            this.keyallTimer = keyallTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }
        
        public long getRemaining() {
            return this.endMillis - System.currentTimeMillis();
        }
        
        public void run() {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The opkey-all has ended.");
            this.cancel();
            OPKeyAllTimer.access$0(this.keyallTimer, null);
        }
    }
}
