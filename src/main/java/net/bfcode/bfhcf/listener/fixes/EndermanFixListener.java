package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public class EndermanFixListener implements Listener
{
    @EventHandler
    public void onEnderDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause().equals((Object)EntityType.ENDERMAN)) {
            e.setCancelled(true);
        }
    }
}
