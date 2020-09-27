package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import net.bfcode.bfhcf.HCFaction;
import org.bukkit.event.Listener;

public class SotwListener implements Listener
{
    private HCFaction plugin;
    
    public SotwListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && this.plugin.getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player && HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
			if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) event.getDamager()) && HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) event.getEntity())) {
				return;
			}
			event.setCancelled(true);
		}
	}
    
/*    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnter(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	todos.add(player);
    	for(Player players : todos) {
    		player.hidePlayer(players);
    	}
    	PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    	if(playerFaction != null) {
    		player.showPlayer(arg0);
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeave(PlayerKickEvent event) {
    	Player player = event.getPlayer();
    	todos.remove(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnter(PlayerJoinFactionEvent event) {
    	Player player = (Player) event.getPlayer();
    	todos.add(player);
    }*/
}
