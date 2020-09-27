package net.bfcode.bfhcf.listener.fixes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Projectile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.Listener;

public class InfinityArrowFixListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Arrow arrow;
        if (entity instanceof Arrow && (!((arrow = (Arrow)entity).getShooter() instanceof Player) || ((CraftArrow)arrow).getHandle().fromPlayer == 2)) {
            arrow.remove();
        }
    }
}
