package net.bfcode.bfhcf.mountain;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;

public class GlowstoneFaction extends MountainFaction {

    public GlowstoneFaction(HCFaction plugin) {
        super("Glowstone", ChatColor.GOLD + "Glowstone Mountain");
    }

    public GlowstoneFaction(Map<String, Object> map) {
        super(map);
    }

    public Cuboid getCuboid() {
        return HCFaction.getPlugin().getGlowstoneMountainManager().getCuboid();
    }

    public boolean allowed(Material material) {
        return material == Material.GLOWSTONE;
    }
}
