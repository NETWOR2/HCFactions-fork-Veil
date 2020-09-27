package net.bfcode.bfhcf.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.bfcode.bfhcf.HCFaction;

public abstract class PvpClass
{
    public static long DEFAULT_MAX_DURATION;
    protected Set<PotionEffect> passiveEffects;
    protected String name;
    protected long warmupDelay;
    
    public PvpClass(String name, long warmupDelay) {
        this.passiveEffects = new HashSet<PotionEffect>();
        this.name = name;
        this.warmupDelay = warmupDelay;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getWarmupDelay() {
        return this.warmupDelay;
    }
    
    public boolean onEquip(Player p) {
    	if(!HCFaction.getPlugin().getTournamentManager().isInTournament(p)) {
	    	for (PotionEffect effect : this.passiveEffects) {
	            p.addPotionEffect(effect, true);
	        }
	    	if(name != "Chestplate") {
		        p.sendMessage(ChatColor.GOLD + this.name + " Class " + ChatColor.YELLOW + "has been equipped.");	
	    	}
    	}
        return true;
    }
    
    public void onUnequip(Player player) {
        for (PotionEffect effect : this.passiveEffects) {
            for (PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > PvpClass.DEFAULT_MAX_DURATION && active.getType().equals(effect.getType())) {
                    if (active.getAmplifier() != effect.getAmplifier()) {
                        continue;
                    }
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
        if(name != "Chestplate") {
            player.sendMessage(ChatColor.GOLD + this.name + " Class " + ChatColor.YELLOW + "has been un-equipped.");
        }
    }
    
    public abstract boolean isApplicableFor(Player p0);
    
    static {
        DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
    }
}
