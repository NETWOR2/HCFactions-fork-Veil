package net.bfcode.bfhcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

public class SaleTimer {
	
    private SaleRunnable saleRunnable;
    
    public boolean cancel() {
        if (this.saleRunnable != null) {
            this.saleRunnable.cancel();
            this.saleRunnable = null;
            return true;
        }
        return false;
    }
    
    public void start(long millis) {
        if (this.saleRunnable == null) {
            (this.saleRunnable = new SaleRunnable(this, millis)).runTaskLater(HCFaction.getPlugin(), millis / 50L);
        }
    }
    
    public SaleRunnable getSaleRunnable() {
        return this.saleRunnable;
    }
    
    static /* synthetic */ void access$0(SaleTimer saleTimer, SaleRunnable saleRunnable) {
        saleTimer.saleRunnable = saleRunnable;
    }
    
    public static class SaleRunnable extends BukkitRunnable
    {
        private SaleTimer saleTimer;
        private long startMillis;
        private long endMillis;
        
        public SaleRunnable(SaleTimer saleTimer, long duration) {
            this.saleTimer = saleTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }
        
        public long getRemaining() {
            return this.endMillis - System.currentTimeMillis();
        }
        
        public void run() {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The key sale has ended.");
            this.cancel();
            SaleTimer.access$0(this.saleTimer, null);
        }
    }
}
