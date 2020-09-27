package net.bfcode.bfhcf.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

public class BrewingSpeedListener implements Listener {
	
    private Map<Location, BrewingStand> activeStands;
    
    public BrewingSpeedListener(HCFaction plugin) {
        this.activeStands = new HashMap<Location, BrewingStand>();
        new BrewingUpdateTask().runTaskTimer(plugin, 20L, 20L);
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Action action = event.getAction();
    	
        if(action == Action.RIGHT_CLICK_BLOCK) {
            BlockState state = event.getClickedBlock().getState();
            
            if(state instanceof BrewingStand) {
				BrewingStand brewingStand = (BrewingStand) state;
                
                this.activeStands.put(brewingStand.getLocation(), brewingStand);
            }
        }
    }
    
    public class BrewingUpdateTask extends BukkitRunnable {
        public void run() {
            if(BrewingSpeedListener.this.activeStands.isEmpty()) return;

            Iterator<Entry<Location, BrewingStand>> standLoc = BrewingSpeedListener.this.activeStands.entrySet().iterator();
            
            while(standLoc.hasNext()) {
              BrewingStand stand = standLoc.next().getValue();
              
              if(!stand.getChunk().isLoaded()) {
            	  standLoc.remove();
              } else if(stand.getBrewingTime() > 1) {
            	  stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - HCFaction.getPlugin().getConfig().getInt("Brewer-Speed")));
              }
            }
        }
	}
}
