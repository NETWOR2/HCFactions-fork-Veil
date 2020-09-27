package net.bfcode.bfhcf.killstreaks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.bfcode.bfhcf.HCFaction;

public class KillStreakListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		if(!HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
			if(killer instanceof Player) {
			KillStreak.incrementKillStreak(killer);	
			if(KillStreak.hasKillStreak(killer)) {
				if(KillStreak.getKillStreak(killer) == 3) {
						KillStreaks.x3(killer);
					} else if(KillStreak.getKillStreak(killer) == 6) {
						KillStreaks.x6(killer);
					} else if(KillStreak.getKillStreak(killer) == 10) {
						KillStreaks.x10(killer);
					} else if(KillStreak.getKillStreak(killer) == 15) {
						KillStreaks.x15(killer);
					} else if(KillStreak.getKillStreak(killer) == 20) {
						KillStreaks.x20(killer);
					} else if(KillStreak.getKillStreak(killer) == 25) {
						KillStreaks.x25(killer);
					} else if(KillStreak.getKillStreak(killer) == 30) {
						KillStreaks.x30(killer);
					} else if(KillStreak.getKillStreak(killer) == 45) {
						KillStreaks.x45(killer);
					} else if(KillStreak.getKillStreak(killer) == 60) {
						KillStreaks.x60(killer);
					}
				}
			}
			if(KillStreak.hasKillStreak(player)) {
				KillStreak.removeKillStreak(player);
			}
		}
	}
}
