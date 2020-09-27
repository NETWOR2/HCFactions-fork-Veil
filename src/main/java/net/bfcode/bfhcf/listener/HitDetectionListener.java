package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class HitDetectionListener implements Listener
{
    public void onEnable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(19);
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setMaximumNoDamageTicks(19);
    }
}
