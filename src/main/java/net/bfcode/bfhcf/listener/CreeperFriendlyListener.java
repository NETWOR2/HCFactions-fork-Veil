package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Creeper;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.Listener;

public class CreeperFriendlyListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onTargetEvent(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }
}
