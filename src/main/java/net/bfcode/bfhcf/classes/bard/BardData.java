package net.bfcode.bfhcf.classes.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData
{
    public static double MIN_ENERGY = 0.0;
    public static double MAX_ENERGY = 100.0;
    public static long MAX_ENERGY_MILLIS = 100000L;
    @SuppressWarnings("unused")
	private static double ENERGY_PER_MILLISECOND = 1.25;
    public long buffCooldown;
    public BukkitTask heldTask;
    private long energyStart;
    
    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }
    
    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }
    
    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(120000L, (long)(1.25 * (System.currentTimeMillis() - this.energyStart)));
    }
    
    public double getEnergy() {
        double value = this.getEnergyMillis() / 1000.0;
        return Math.round(value * 10.0) / 10.0;
    }
    
    public void setEnergy(double energy) {
        Preconditions.checkArgument(energy >= 0.0, (Object)"Energy cannot be less than 0.0");
        Preconditions.checkArgument(energy <= 120.0, (Object)"Energy cannot be more than 120.0");
        this.energyStart = (long)(System.currentTimeMillis() - 1000.0 * energy);
    }
}
