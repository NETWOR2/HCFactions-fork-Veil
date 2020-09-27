package net.bfcode.bfhcf.deathhistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class DeathHistoryListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		DeathHistoryHandler.createDeathHistory(player);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		DamageCause damcause = player.getLastDamageCause().getCause();
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		if(player.getKiller() instanceof Player) {
			Player killer = event.getEntity().getKiller();
            ItemStack item = ((Player)killer).getItemInHand();
			if(item != null && item.getType().equals(Material.BOW)) {
				addReason(player, "Killer: " + killer.getName() + " \u2503 Coords: " + 
						Math.round(player.getLocation().getX()) + ", " + 
						Math.round(player.getLocation().getY()) + ", " + 
						Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
			} else {
				addReason(player, "Killer: " + killer.getName() + " \u2503 Coords: " + 
						Math.round(player.getLocation().getX()) + ", " + 
						Math.round(player.getLocation().getY()) + ", " + 
						Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
			}
		}
        else if (damcause == EntityDamageEvent.DamageCause.FALL) {
            addReason(player, "Death: Fall. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.FIRE) {
            addReason(player, "Death: Burned. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.LIGHTNING) {
            addReason(player, "Death: Ray. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.WITHER) {
            addReason(player, "Death: Wither. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.DROWNING) {
            addReason(player, "Death: Drowned. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
            addReason(player, "Death: Suffocated. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.MAGIC) {
            addReason(player, "Death: Magic. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.VOID) {
            addReason(player, "Death: Void. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            addReason(player, "Death: Expotion. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.LAVA) {
            addReason(player, "Death: Lava. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else if (damcause == EntityDamageEvent.DamageCause.STARVATION) {
            addReason(player, "Death: Hungry. \u2503 Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
        else {
            addReason(player, 
            		"Death: Unknown. \u2503 "+ "Coords: " + 
            		Math.round(player.getLocation().getX()) + ", " + 
            		Math.round(player.getLocation().getY()) + ", " + 
            		Math.round(player.getLocation().getZ()) + " \u2503 " + format.format(now));
        }
	}
	
    public void addReason(final Player player, final String arg) {
        List<String> args = DeathHistoryHandler.death_file.getStringList(player.getName());
        args.add(arg);
        DeathHistoryHandler.death_file.set(player.getName(), args);
        DeathHistoryHandler.saveDeathHistory();
    }
	
}
