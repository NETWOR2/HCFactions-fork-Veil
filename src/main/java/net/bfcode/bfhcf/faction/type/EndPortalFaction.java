package net.bfcode.bfhcf.faction.type;

import org.bukkit.ChatColor;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public EndPortalFaction(String name) {
        super(name);
    }
    
    public EndPortalFaction(Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(CommandSender sender) {
        return ChatColor.DARK_AQUA + "EndPortal";
    }
    
    public static class EndPortalFaction1 extends EndPortalFaction implements ConfigurationSerializable {
        public EndPortalFaction1() {
            super("EndPortal1");
            for (World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction1(Map<String, Object> map) {
            super(map);
        }
    }
    public static class EndPortalFaction2 extends EndPortalFaction implements ConfigurationSerializable {
        public EndPortalFaction2() {
            super("EndPortal2");
            for (World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction2(Map<String, Object> map) {
            super(map);
        }
    }
    public static class EndPortalFaction3 extends EndPortalFaction implements ConfigurationSerializable {
        public EndPortalFaction3() {
            super("EndPortal3");
            for (World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction3(Map<String, Object> map) {
            super(map);
        }
    }
    public static class EndPortalFaction4 extends EndPortalFaction implements ConfigurationSerializable {
        public EndPortalFaction4() {
            super("EndPortal4");
            for (World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction4(Map<String, Object> map) {
            super(map);
        }
    }
    
    public boolean isDeathban() {
        return true;
    }
}
