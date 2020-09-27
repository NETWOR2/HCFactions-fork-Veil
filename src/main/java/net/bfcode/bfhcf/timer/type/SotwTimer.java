package net.bfcode.bfhcf.timer.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.scheduler.BukkitRunnable;

public class SotwTimer {
	
    private SotwRunnable sotwRunnable;
	public List<UUID> enabledSotw = new ArrayList<>();
    
    public boolean cancel() {
        if (this.sotwRunnable != null) {
            this.sotwRunnable.cancel();
            this.sotwRunnable = null;
            return true;
        }
        return false;
    }
    
    public void start(long millis) {
        if (this.sotwRunnable == null) {
            (this.sotwRunnable = new SotwRunnable(this, millis)).runTaskLater(HCFaction.getPlugin(), millis / 50L);
        }
    }
    
    public SotwRunnable getSotwRunnable() {
        return this.sotwRunnable;
    }
    
    public static class SotwRunnable extends BukkitRunnable {
    	
    	private SotwTimer sotwTimer;
        private long startMillis;
        private long endMillis;
        
        public SotwRunnable(SotwTimer sotwTimer, long duration) {
            this.sotwTimer = sotwTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }
        
        public long getRemaining() {
            return this.endMillis - System.currentTimeMillis();
        }
        
		public boolean isSotwEnabled(Player player) {
			if (HCFaction.getPlugin().getSotwTimer().enabledSotw.contains(player.getUniqueId())) {
				return true;
			} else {
				return false;
			}
		}
        
        public void run() {
            Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
            Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
            Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
            Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
            Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
            Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588");
            Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588");
            Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
            Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
            Bukkit.broadcastMessage(ChatColor.RESET + "");
            Bukkit.broadcastMessage(ChatColor.RED + "You are no longer Invincible");
            this.cancel();
            this.sotwTimer.sotwRunnable = null;
        }
    }
}
