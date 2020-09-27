package net.bfcode.bfhcf.faction.struct;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.Locale;

public enum ChatChannel
{
    FACTION("Faction"), 
    ALLIANCE("Alliance"), 
    PUBLIC("Public");
    
    private String name;
    
    private ChatChannel(String name) {
        this.name = name;
    }
    
    public static ChatChannel parse(String id) {
        return parse(id, ChatChannel.PUBLIC);
    }
    
    public static ChatChannel parse(String id, ChatChannel def) {
        String s;
        @SuppressWarnings("unused")
		String lowerCase = s = (id = id.toLowerCase(Locale.ENGLISH));
        switch (s) {
            case "f":
            case "faction":
            case "fc":
            case "fac":
            case "fact": {
                return ChatChannel.FACTION;
            }
            case "a":
            case "alliance":
            case "ally":
            case "ac": {
                return ChatChannel.ALLIANCE;
            }
            case "p":
            case "pc":
            case "g":
            case "gc":
            case "global":
            case "pub":
            case "publi":
            case "public": {
                return ChatChannel.PUBLIC;
            }
            default: {
                return (def == null) ? null : def.getRotation();
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        String prefix = null;
        switch (this) {
            case FACTION: {
                prefix = ConfigurationService.TEAMMATE_COLOUR.toString();
                break;
            }
            case ALLIANCE: {
                prefix = ConfigurationService.ALLY_COLOUR.toString();
                break;
            }
            default: {
                prefix = ConfigurationService.ENEMY_COLOUR.toString();
                break;
            }
        }
        return prefix + this.name;
    }
    
    public String getShortName() {
        switch (this) {
            case FACTION: {
                return "FC";
            }
            case ALLIANCE: {
                return "AC";
            }
            default: {
                return "PC";
            }
        }
    }
    
    public ChatChannel getRotation() {
        switch (this) {
            case FACTION: {
                return ChatChannel.PUBLIC;
            }
            case PUBLIC: {
                return ChatChannel.ALLIANCE;
            }
            case ALLIANCE: {
                return ChatChannel.FACTION;
            }
            default: {
                return ChatChannel.PUBLIC;
            }
        }
    }
    
    public String getRawFormat(Player player) {
        switch (this) {
            case FACTION: {
                return ConfigurationService.TEAMMATE_COLOUR + "(" + this.getDisplayName() + ConfigurationService.TEAMMATE_COLOUR + ") " + player.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + "%2$s";
            }
            case ALLIANCE: {
                return ConfigurationService.ALLY_COLOUR + "(" + this.getDisplayName() + ConfigurationService.ALLY_COLOUR + ") " + player.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + "%2$s";
            }
            default: {
                throw new IllegalArgumentException("Cannot get the raw format for public chat channel");
            }
        }
    }
}
