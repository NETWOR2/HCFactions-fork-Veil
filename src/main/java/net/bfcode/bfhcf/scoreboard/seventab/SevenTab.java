package net.bfcode.bfhcf.scoreboard.seventab;

import lombok.Getter;
import net.bfcode.bfhcf.scoreboard.seventab.adapter.TabAdapter;
import net.bfcode.bfhcf.scoreboard.seventab.listener.TabListener;
import net.bfcode.bfhcf.scoreboard.seventab.packet.TabPacket;
import net.bfcode.bfhcf.scoreboard.seventab.runnable.TabRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SevenTab {

	@Getter
	private static SevenTab instance;
	
	private final TabAdapter adapter;
	
	public SevenTab(JavaPlugin plugin, TabAdapter adapter) {
		instance = this;
		this.adapter = adapter;
		
		new TabPacket(plugin);
		
		Bukkit.getServer().getPluginManager().registerEvents(new TabListener(this), plugin);
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TabRunnable(adapter), 10L, 10L); //TODO: async to run 1 millis
	}
}