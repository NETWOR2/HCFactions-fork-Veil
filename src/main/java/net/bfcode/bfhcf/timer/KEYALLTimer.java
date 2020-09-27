package net.bfcode.bfhcf.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.concurrent.TimeUnit;

public class KEYALLTimer extends GlobalTimer
{
    public KEYALLTimer() {
        super(ConfigurationService.KEYALL_TIMER, TimeUnit.MINUTES.toMillis(30L));
    }
    
    public void run() {
        if (this.getRemaining() % 30L == 0L) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The KeyAll will start in " + ChatColor.GREEN.toString() + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(), true));
        }
    }
    
    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.KEYALL_COLOUR;
    }
}
