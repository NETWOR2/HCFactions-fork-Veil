package net.bfcode.bfhcf.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.concurrent.TimeUnit;

public class OPKEYALLTimer extends GlobalTimer
{
    public OPKEYALLTimer() {
        super(ConfigurationService.OPKEYALL_TIMER, TimeUnit.MINUTES.toMillis(30L));
    }
    
    public void run() {
        if (this.getRemaining() % 30L == 0L) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The OPKeyAll will start in " + ChatColor.GREEN.toString() + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(), true));
        }
    }
    
    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.OPKEYALL_COLOUR;
    }
}
