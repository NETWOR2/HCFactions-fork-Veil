package net.bfcode.bfhcf.visualise;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

abstract class BlockFiller
{
    abstract VisualBlockData generate(Player p0, Location p1);
    
    ArrayList<VisualBlockData> bulkGenerate(Player player, Iterable<Location> locations) {
        @SuppressWarnings("rawtypes")
		ArrayList<VisualBlockData> data = new ArrayList<VisualBlockData>(Iterables.size((Iterable)locations));
        for (Location location : locations) {
            data.add(this.generate(player, location));
        }
        return data;
    }
}
