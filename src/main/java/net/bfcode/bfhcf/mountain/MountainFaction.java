package net.bfcode.bfhcf.mountain;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.utils.ConfigurationService;

public abstract class MountainFaction extends ClaimableFaction {
	
    private String colorname;

    public MountainFaction(String name, String colorname) {
        super(name);
        this.colorname = colorname;
    }

    public MountainFaction(Map<String, Object> map) {
        super(map);
        colorname = (String) map.get("colorname");
    }

    public abstract Cuboid getCuboid();

    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("colorname", colorname);
        return map;
    }

    public void reload() {
        removeClaims(getClaims(), Bukkit.getConsoleSender());
        if (ConfigurationService.KIT_MAP) {
            return;
        }
        Cuboid cuboid = getCuboid();
        if (cuboid != null) {
            addClaim(new Claim(this, cuboid), Bukkit.getConsoleSender());
        }
    }

    public abstract boolean allowed(Material material);

    public boolean isSafezone() {
        return false;
    }

    public boolean isDeathban() {
        return true;
    }

    public String getDisplayName(Faction other) {
        return colorname;
    }

    public String getDisplayName(CommandSender sender) {
        return colorname;
    }

    public void printDetails(CommandSender sender) {
        Bukkit.dispatchCommand(sender, name);
    }
}
