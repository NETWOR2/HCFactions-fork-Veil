package net.bfcode.bfhcf.killstreaks;

import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;

public class KillStreak {
	
	public static int getKillStreak(Player player) {
		if(hasKillStreak(player)) {
			FactionUser killstreaks = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId());
			return killstreaks.getKillStreaks();
		}
		return 0;
	}
	
	public static boolean hasKillStreak(Player player) {
		FactionUser killstreaks = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId());
		if(killstreaks.getKillStreaks() >= 1) {
			return true;
		}else{
			return false;
		}
	}
	
	public static void incrementKillStreak(Player player) {
		Integer killsOnStreak = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).getKillStreaks();
		if(hasKillStreak(player)) {
			HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).setKillStreaks(killsOnStreak + 1);
		} else {
			HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).setKillStreaks(1);
		}
	}
	
	public static void removeKillStreak(Player player) {
		HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).setKillStreaks(0);
	}
}
