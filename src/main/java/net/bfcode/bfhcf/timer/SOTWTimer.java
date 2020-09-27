package net.bfcode.bfhcf.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.concurrent.TimeUnit;

public class SOTWTimer extends GlobalTimer
{
    public SOTWTimer() {
        super(ConfigurationService.SOTW_TIMER, TimeUnit.MINUTES.toMillis(30L));
    }
    
    public void run() {
        if (this.getRemaining() % 30L == 0L) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "SOTW will start in " + ChatColor.RED + HCFaction.getRemaining(this.getRemaining(), true));
        }
    }
    
    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.SOTW_COLOUR;
    }
}
