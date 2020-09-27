package net.bfcode.bfhcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

public class KeyAllTimer {
	
    private KeyAllRunnable keyallRunnable;
    
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
            (this.keyallRunnable = new KeyAllRunnable(this, millis)).runTaskLater(HCFaction.getPlugin(), millis / 50L);
        }
    }
    
    public KeyAllRunnable getKeyAllRunnable() {
        return this.keyallRunnable;
    }
    
    static /* synthetic */ void access$0(KeyAllTimer keyallTimer, KeyAllRunnable keyallRunnable) {
        keyallTimer.keyallRunnable = keyallRunnable;
    }
    
    public static class KeyAllRunnable extends BukkitRunnable
    {
        private KeyAllTimer keyallTimer;
        private long startMillis;
        private long endMillis;
        
        public KeyAllRunnable(KeyAllTimer keyallTimer, long duration) {
            this.keyallTimer = keyallTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }
        
        public long getRemaining() {
            return this.endMillis - System.currentTimeMillis();
        }
        
        public void run() {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The key-all has ended.");
            this.cancel();
            KeyAllTimer.access$0(this.keyallTimer, null);
        }
    }
}
