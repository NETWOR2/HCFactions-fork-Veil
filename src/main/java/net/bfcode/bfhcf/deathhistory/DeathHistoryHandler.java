package net.bfcode.bfhcf.deathhistory;

import org.bukkit.entity.Player;

public class DeathHistoryHandler {
	
public static DeathHistoryFile death_file;
	
	public static void createDeathHistory(Player player) {
		if ((!player.hasPlayedBefore()) || (DeathHistoryHandler.death_file.get(player.getName()) == null)) {
			DeathHistoryHandler.death_file.set(player.getName(), null);
			saveDeathHistory();
    	}
	}
	
	public static void saveDeathHistory() {
        DeathHistoryHandler.death_file.save();
        DeathHistoryHandler.death_file.reload();
    }
	
	static {
        DeathHistoryHandler.death_file = DeathHistoryFile.getConfig();
    }
}
