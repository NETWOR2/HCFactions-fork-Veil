package net.bfcode.bfhcf.mountain;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;

public class OreFaction extends MountainFaction {

    public OreFaction(HCFaction plugin) {
        super("OreFaction", ChatColor.BLUE + "Ore Mountain");
    }

    public OreFaction(Map<String, Object> map) {
        super(map);
    }

    public Cuboid getCuboid() {
        return HCFaction.getPlugin().getOreMountainManager().getCuboid();
    }

    public boolean allowed(Material material) {
        return HCFaction.getPlugin().getOreMountainManager().ALLOWED.contains(material.getId());
    }
}
