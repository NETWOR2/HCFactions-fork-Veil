package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Minecart;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.Listener;

public class MinecartElevatorListener implements Listener
{
    @EventHandler
    public void onMinecart(VehicleEnterEvent event) {
        if (!(event.getVehicle() instanceof Minecart) || !(event.getEntered() instanceof Player)) {
            return;
        }
        Player p = (Player)event.getEntered();
        Location l = event.getVehicle().getLocation();
        Location loc = new Location(p.getWorld(), (double)l.getBlockX(), (double)l.getBlockY(), (double)l.getBlockZ());
        Material m = loc.getBlock().getType();
        if (m.equals((Object)Material.FENCE_GATE) || m.equals((Object)Material.SIGN_POST)) {
            event.setCancelled(true);
            p.teleport(this.teleportSpot(loc, loc.getBlockY(), 254));
        }
    }
    
    public Location teleportSpot(Location loc, int min, int max) {
        for (int k = min; k < max; ++k) {
            Material m1 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ()).getBlock().getType();
            Material m2 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)(k + 1), (double)loc.getBlockZ()).getBlock().getType();
            if (m1.equals((Object)Material.AIR) && m2.equals((Object)Material.AIR)) {
                return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ());
            }
        }
        return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()), (double)loc.getBlockZ());
    }
}
