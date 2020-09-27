package net.bfcode.bfhcf.mountain;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;

public class DestroyTheCoreFaction extends MountainFaction {

    public DestroyTheCoreFaction(HCFaction plugin) {
        super("DTC", ChatColor.AQUA + "Destroy The Core");
    }

    public DestroyTheCoreFaction(Map<String, Object> map) {
        super(map);
    }

    public boolean allowed(Material material) {
        return material == Material.OBSIDIAN;
    }

	@Override
	public Cuboid getCuboid() {
		// TODO Auto-generated method stub
		return null;
	}
}
