package net.bfcode.bfhcf.scoreboard.seventab.runnable;

import lombok.AllArgsConstructor;
import net.bfcode.bfhcf.scoreboard.seventab.adapter.TabAdapter;
import net.bfcode.bfhcf.scoreboard.seventab.entry.TabEntry;
import net.bfcode.bfhcf.scoreboard.seventab.layout.TabLayout;
import net.bfcode.bfhcf.scoreboard.seventab.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class TabRunnable implements Runnable {

	private TabAdapter adapter;
	
	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			TabLayout layout = TabLayout.getLayoutMapping().get(player.getUniqueId());
			
			for(TabEntry entry : adapter.getLines(player)) {
				layout.update(entry.getColumn(), entry.getRow(), entry.getText(), entry.getPing(), entry.getSkin());
			}
			
			for (int row = 0; row < 20; row++) {
				for (int column = 0; column < 3; column++) {
					if(layout.getByLocation(adapter.getLines(player), column, row) == null) {
						layout.update(column, row, "", 0, Skin.DEFAULT_SKIN);
					}
				}
			}
		}
	}
}
