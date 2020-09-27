package net.bfcode.bfhcf.faction.type;

import org.bukkit.command.CommandSender;

import net.bfcode.bfhcf.utils.ConfigurationService;

import java.util.Map;

public class WarzoneFaction extends Faction
{
    public WarzoneFaction() {
        super("Warzone");
    }
    
    public WarzoneFaction(Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(CommandSender sender) {
        return ConfigurationService.WARZONE_COLOUR + this.getName();
    }
}
