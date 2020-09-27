package net.bfcode.bfhcf.scoreboard.seventab.listener;

import lombok.AllArgsConstructor;
import net.bfcode.bfhcf.scoreboard.seventab.SevenTab;
import net.bfcode.bfhcf.scoreboard.seventab.layout.TabLayout;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class TabListener implements Listener {

	private SevenTab sevenTab;
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		TabLayout layout = new TabLayout(sevenTab, player);
		layout.create();
		layout.setHeaderAndFooter();
		
		TabLayout.getLayoutMapping().put(player.getUniqueId(), layout);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		TabLayout.getLayoutMapping().remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		TabLayout.getLayoutMapping().remove(player.getUniqueId());
	}
}
