package net.bfcode.bfhcf.faction.type;

import org.bukkit.command.CommandSender;

import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.Map;

public class WildernessFaction extends Faction
{
    public WildernessFaction() {
        super("Wilderness");
    }
    
    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(CommandSender sender) {
        return ConfigurationService.WILDERNESS_COLOUR + this.getName();
    }
}
