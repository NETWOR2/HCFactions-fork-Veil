package net.bfcode.bfhcf.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.concurrent.TimeUnit;

public class SALETimer extends GlobalTimer
{
    public SALETimer() {
        super(ConfigurationService.SALE_TIMER, 0L);
    }
    
    public void run() {
        if (this.getRemaining() % 30L == 0L) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The sale will start in " + ChatColor.GREEN.toString() + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(), true));
        }
    }
    
    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.SALE_COLOUR;
    }
}
