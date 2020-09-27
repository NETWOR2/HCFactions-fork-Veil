package net.bfcode.bfhcf.faction.type;

import org.bukkit.World;
import org.bukkit.Bukkit;
import java.util.List;
import org.bukkit.ChatColor;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable {
	
	public static int ROAD_EDGE_DIFF = 1000;
	public static double ROAD_WIDTH_LEFT = 8.0;
	public static double ROAD_WIDTH_RIGHT = 8.0;
	public static int ROAD_MIN_HEIGHT = 0;
	public static int ROAD_MAX_HEIGHT = 256;

	public RoadFaction(String name) {
		super(name);
	}

	public RoadFaction(Map<String, Object> map) {
		super(map);
	}

	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.ROAD_COLOUR + this.getName().replace("st", "st ").replace("th", "th ");
	}

	@Override
	public void printDetails(CommandSender sender) {
		List<String> toSend = new ArrayList<String>();
		for (String string : HCFaction.getPlugin().getConfig().getStringList("faction-settings.show.road-faction")) {
			string = string.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "");
			string = string.replace("%FACTION%", this.getDisplayName(sender));
			toSend.add(string);
		}
		for (String message : toSend) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}

	public static class NorthRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public NorthRoadFaction() {
			super("NorthRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
				}
			}
		}

		public NorthRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class EastRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public EastRoadFaction() {
			super("EastRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
				}
			}
		}

		public EastRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class SouthRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public SouthRoadFaction() {
			super("SouthRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
				}
			}
		}

		public SouthRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class WestRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public WestRoadFaction() {
			super("WestRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
				}
			}
		}

		public WestRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}
}
