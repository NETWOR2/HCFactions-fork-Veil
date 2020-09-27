package net.bfcode.bfhcf.utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import com.google.common.collect.ImmutableList;

import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

@SuppressWarnings({ "unused" })
public class ConfigurationService {
	public static TimeZone SERVER_TIME_ZONE;
	public static List<String> DISALLOWED_FACTION_NAMES;
	public static Map<Enchantment, Integer> ENCHANTMENT_LIMITS;
	public static Map<PotionType, Integer> POTION_LIMITS;
	public static Map<World.Environment, Double> SPAWN_RADIUS_MAP;
	public static int SPAWNER_PRICE;
	public static String DOUBLEARROW = "»";
	public static String TEAMSPEAK;
	public static String STORE;
	public static String TWITTER;
	public static String DISCORD;
	public static String WEBSITE;
	public static String SERVER_NAME;
	public static String FOOTER;
	public static String END_EXIT;
	public static boolean LUXOR = false;
	public static ChatColor TEAMMATE_COLOUR;
	public static ChatColor ALLY_COLOUR;
	public static ChatColor ENEMY_COLOUR;
	public static ChatColor SAFEZONE_COLOUR;
	public static ChatColor ROAD_COLOUR;
	public static ChatColor TARGET;
	public static ChatColor WARZONE_COLOUR;
	public static ChatColor WILDERNESS_COLOUR;
	public static ChatColor GLOWSTONE_COLOUR;
	public static int WARZONE_RADIUS;
	public static int MAX_PLAYERS_PER_FACTION;
	public static int MAX_ALLIES_PER_FACTION;
	public static long DTR_MILLIS_BETWEEN_UPDATES;
	public static String DTR_WORDS_BETWEEN_UPDATES;
	public static ChatColor BASECOLOUR;
	public static int CONQUEST_REQUIRED_WIN_POINTS;
	public static long DEFAULT_DEATHBAN_DURATION;
	public static Map<World.Environment, Integer> BORDER_SIZES;
	public static boolean CRATE_BROADCASTS;
	public static ChatColor LINE_COLOUR;
	public static String STAFFMODE;
	public static String GAMEMODE;
	public static String VANISH;
	public static String CHAT;
	public static String PLAYERS;
	public static ChatColor PLAYERS_COLOUR;
	public static String TPS;
	public static ChatColor TPS_COLOUR;
	public static String SOTW_TIMER;
	public static ChatColor SOTW_COLOUR;
	public static String MINER_CLASS;
	public static String DIAMONDS;
	public static ChatColor DIAMONDS_COLOUR;
	public static String EMERALDS;
	public static ChatColor EMERALDS_COLOUR;
	public static String BARD_CLASS;
	public static String ENERGY;
	public static ChatColor ENERGY_COLOUR;
	public static String BARD_COOLDOWN;
	public static ChatColor BARD_COOLDOWN_COLOUR;
	public static String STATS_TITLE;
	public static String BALANCE;
	public static ChatColor BALANCE_COLOUR;
	public static String KILLS;
	public static ChatColor KILLS_COLOUR;
	public static String DEATHS;
	public static ChatColor DEATHS_COLOUR;
	public static String KILL_STREAK;
	public static ChatColor KILL_STREAK_COLOR;
	public static String FACTION_TITLE;
	public static String FAC_DTR;
	public static ChatColor FAC_DTR_COLOUR;
	public static String FAC_BALANCE;
	public static ChatColor FAC_BALANCE_COLOUR;
	public static String FAC_ONLINE;
	public static ChatColor FAC_ONLINE_COLOUR;
	public static String FAC_POINTS;
	public static ChatColor FAC_POINTS_COLOUR;
	public static String FAC_REGEN;
	public static ChatColor FAC_REGEN_COLOUR;
	public static String COBBLE_ENABLED;
	public static String COBBLE_DISABLED;
	public static String LOGOUT_ALREADY_STARTED;
	public static String LOGOUT_STARTED;
	public static String LOGOUT_DISCONNECT;
	public static String ENDERPEARL_TIMER;
	public static ChatColor ENDERPEARL_COLOUR;
	public static String ARCHER_TIMER;
	public static ChatColor ARCHER_COLOUR;
	public static String LOGOUT_TIMER;
	public static ChatColor LOGOUT_COLOUR;
	public static String NOTCH_APPLE_TIMER;
	public static ChatColor NOTCH_APPLE_COLOUR;
	public static String APPLE_TIMER;
	public static ChatColor APPLE_COLOUR;
	public static String PVP_CLASS_WARMUP_TIMER;
	public static ChatColor PVP_CLASS_WARMUP_COLOUR;
	public static String PVPTIMER_TIMER;
	public static ChatColor PVPTIMER_COLOUR;
	public static String SPAWNTAG_TIMER;
	public static ChatColor SPAWNTAG_COLOUR;
	public static String STUCK_TIMER;
	public static ChatColor STUCK_COLOUR;
	public static String TELEPORT_TIMER;
	public static ChatColor TELEPORT_COLOUR;
	public static String SALE_TIMER;
	public static ChatColor SALE_COLOUR;
	public static String KEYALL_TIMER;
	public static ChatColor KEYALL_COLOUR;
	public static String OPKEYALL_TIMER;
	public static ChatColor OPKEYALL_COLOUR;
	public static String EVENT_TIMER;
	public static ChatColor EVENT_COLOUR;
	public static String SOTW_STARTED;
	public static String SOTW_NOT_ACTIVE;
	public static String SOTW_CANCELLED;
	public static String SOTW_ENDED_ONE;
	public static String SOTW_ENDED_TWO;
	public static String REVIVE_MESSAGE;
	public static String DEATHBAN_BYPASS;
	public static String DEATHBANNED_EOTW;
	public static String DEATHBANNED_ACTIVE;
	public static String STILL_DEATHBANNED;
	public static String DEATHBANNED_USE_A_LIFE;
	public static String DEATHBANNED_EOTW_ENTIRE;
	public static String END_CANNOT_BUILD;
	public static String WORLD_CANNOT_BUILD;
	public static String FAILED_PEARL;
	public static String TELEPORTED_SPAWN;
	public static String CANNOT_ATTACK;
	public static String IN_FACTION;
	public static String ALLY_FACTION;
	public static String CANNOT_BUILD;
	public static String ENDERPEARL_COOLDOWN_EXPIRED;
	public static String ENDERPEARL_ITEM;
	public static String SPAWN_TAGGED;
	public static String FOOTER_SCOREBOARD;
	public static String TABLIST_COLOR;
	public static boolean KIT_MAP;
	public static boolean DEV;
	public static boolean TAB;
	public static boolean DIAMOND_ORE_ALERTS;
	public static int UNBUILDABLE_RANGE;
	public static boolean ORIGINS = false;

	public static void init(FileConfiguration config) {
		KIT_MAP = config.getBoolean("kit-map");
		DEV = config.getBoolean("dev", false);
		TAB = config.getBoolean("tab");

		// SCOREBOARD TIMERS AND COLORS
		END_EXIT = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.END_EXIT"));
		LINE_COLOUR = ChatColor.getByChar(config.getString("Scoreboard.LINE_COLOUR", "&7").replace("&", "").trim());
		STAFFMODE = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.STAFFMODE"));
		VANISH = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.VANISH"));
		CHAT = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.CHAT"));
		GAMEMODE = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.GAMEMODE"));
		PLAYERS = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.PLAYERS"));
		TPS = ChatColor.translateAlternateColorCodes('&', config.getString("StaffMode.TPS"));
		SOTW_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.SOTW_TIMER"));
		SOTW_COLOUR = ChatColor.getByChar(config.getString("Scoreboard.SOTW_COLOUR", "&f").replace("&", "").trim());
		MINER_CLASS = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.MINER.MINER_CLASS"));
		BARD_CLASS = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.BARD.BARD_CLASS"));
		ENERGY = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.BARD.ENERGY"));
		ENERGY_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.BARD.ENERGY_COLOUR", "&f").replace("&", "").trim());
		BARD_COOLDOWN = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.BARD.BARD_COOLDOWN"));
		BARD_COOLDOWN_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.BARD.BARD_COOLDOWN_COLOUR", "&f").replace("&", "").trim());
		BALANCE = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.BALANCE"));
		BALANCE_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.BALANCE_COLOUR", "&2").replace("&", "").trim());
		KILLS = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.KILLS"));
		KILLS_COLOUR = ChatColor.getByChar(config.getString("Scoreboard.KILLS_COLOUR", "&f").replace("&", "").trim());
		DEATHS = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.DEATHS"));
		DEATHS_COLOUR = ChatColor.getByChar(config.getString("Scoreboard.DEATHS_COLOUR", "&f").replace("&", "").trim());
		KILL_STREAK = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.STREAK"));
		KILL_STREAK_COLOR = ChatColor.getByChar(config.getString("Scoreboard.STREAK_COLOUR").replace("&", "").trim());
		FACTION_TITLE = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FACTION_TITLE"));
		FAC_DTR = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FAC_DTR"));
		FAC_DTR_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.FAC_DTR_COLOUR", "&a").replace("&", "").trim());
		FAC_BALANCE = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FAC_BALANCE"));
		FAC_BALANCE_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.FAC_BALANCE_COLOUR", "&2").replace("&", "").trim());
		FAC_ONLINE = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FAC_ONLINE"));
		FAC_ONLINE_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.FAC_ONLINE_COLOUR", "&f").replace("&", "").trim());
		FAC_POINTS = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FAC_POINTS"));
		FAC_POINTS_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.FAC_POINTS_COLOUR", "&f").replace("&", "").trim());
		FAC_REGEN = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FAC_REGEN"));
		FAC_REGEN_COLOUR = ChatColor
				.getByChar(config.getString("Scoreboard.FAC_REGEN_COLOUR", "&c").replace("&", "").trim());
		TEAMSPEAK = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.TEAMSPEAK"));
		STORE = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.STORE"));
		DISCORD = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.DISCORD"));
		TWITTER = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.TWITTER"));
		WEBSITE = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.WEBSITE"));
		SERVER_NAME = ChatColor.translateAlternateColorCodes('&', config.getString("server-info.SERVER-NAME"));
		FOOTER = ChatColor.translateAlternateColorCodes('&',
				config.getString("Scoreboard.FOOTER").replace("%normalarrow%", "»"));
		WARZONE_RADIUS = config.getInt("WARZONE_RADIUS");
		MAX_PLAYERS_PER_FACTION = config.getInt("faction-settings.MAX-PLAYERS");
		MAX_ALLIES_PER_FACTION = config.getInt("faction-settings.MAX-ALLIES");
		BORDER_SIZES = new EnumMap<World.Environment, Integer>(World.Environment.class);
		PotionType[] array1;
		PotionType[] limitedPotions = array1 = new PotionType[] { PotionType.INSTANT_DAMAGE, PotionType.REGEN,
				PotionType.STRENGTH, PotionType.WEAKNESS, PotionType.SLOWNESS, PotionType.INVISIBILITY,
				PotionType.POISON };
		for (PotionType potion : array1) {
			try {
				POTION_LIMITS.put(potion, config.getInt("potion-limits" + potion.name()));
			} catch (Exception p) {
				System.out.println("Error getting potion limit. Tried getting the limit for " + potion.name());
			}
		}
		Enchantment[] array;
		Enchantment[] limitedEnchants = array = new Enchantment[] { Enchantment.PROTECTION_ENVIRONMENTAL,
				Enchantment.DAMAGE_ALL, Enchantment.ARROW_DAMAGE, Enchantment.KNOCKBACK, Enchantment.ARROW_KNOCKBACK,
				Enchantment.FIRE_ASPECT, Enchantment.THORNS, Enchantment.ARROW_FIRE };
		for (Enchantment enchant : array) {
			try {
				ENCHANTMENT_LIMITS.put(enchant, config.getInt("enchant-limits." + enchant.getName()));
			} catch (Exception e) {
				System.out.println("Error getting enchantment limit. Tried getting the limit for " + enchant.getName());
			}
		}
		BORDER_SIZES.put(World.Environment.NORMAL, 3000);
		BORDER_SIZES.put(World.Environment.NETHER, 1000);
		BORDER_SIZES.put(World.Environment.THE_END, 500);
		SPAWN_RADIUS_MAP.put(World.Environment.NORMAL, 63.0);
		SPAWN_RADIUS_MAP.put(World.Environment.NETHER, 22.5);
		SPAWN_RADIUS_MAP.put(World.Environment.THE_END, 48.5);
		DEFAULT_DEATHBAN_DURATION = TimeUnit.MINUTES.toMillis(config.getInt("deathban-time", 120));
		TEAMMATE_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.teammate", "&2").replace("&", "").trim());
		ALLY_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.ally", "&3").replace("&", "").trim());
		ENEMY_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.enemy", "&c").replace("&", "").trim());
		SAFEZONE_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.safezone", "&b").replace("&", "").trim());
		ROAD_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.road", "&c").replace("&", "").trim());
		TARGET = ChatColor.getByChar(config.getString("faction-settings.colors.target", "&d").replace("&", "").trim());
		WARZONE_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.warzone", "&c").replace("&", "").trim());
		BASECOLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.default", "&7").replace("&", "").trim());
		WILDERNESS_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.wilderness", "&d").replace("&", "").trim());
		GLOWSTONE_COLOUR = ChatColor
				.getByChar(config.getString("faction-settings.colors.glowstone", "&b").replace("&", "").trim());
		DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);
		DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils.formatDurationWords(DTR_MILLIS_BETWEEN_UPDATES, true, true);
		CRATE_BROADCASTS = false;
		COBBLE_ENABLED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cobble-enabled"));
		COBBLE_DISABLED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cobble-disabled"));
		LOGOUT_ALREADY_STARTED = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.messages.logout-already-started"));
		LOGOUT_STARTED = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.messages.logout-started"));
		LOGOUT_DISCONNECT = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.messages.logout-safely"));
		DEATHBAN_BYPASS = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathban-bypass"));
		DEATHBANNED_EOTW = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathbanned-eotw"));
		DEATHBANNED_ACTIVE = ChatColor.translateAlternateColorCodes('&',
				config.getString("messages.deathbanned-active"));
		STILL_DEATHBANNED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.still-deathbanned"));
		DEATHBANNED_EOTW_ENTIRE = ChatColor.translateAlternateColorCodes('&',
				config.getString("messages.deathbanned-eotw-entire"));
		END_CANNOT_BUILD = ChatColor.translateAlternateColorCodes('&', config.getString("messages.end-cannot-build"));
		WORLD_CANNOT_BUILD = ChatColor.translateAlternateColorCodes('&',
				config.getString("messages.world-cannot-build"));
		FAILED_PEARL = ChatColor.translateAlternateColorCodes('&', config.getString("messages.failed-pearl"));
		TELEPORTED_SPAWN = ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-spawn"));
		CANNOT_ATTACK = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cannot-attack"));
		IN_FACTION = ChatColor.translateAlternateColorCodes('&', config.getString("messages.in-faction"));
		ALLY_FACTION = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ally-faction"));
		REVIVE_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.revive-message"));
		LOGOUT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.logout-timer"));
		LOGOUT_COLOUR = ChatColor.getByChar(config.getString("timers.logout-color", "&f").replace("&", "").trim());
		ENDERPEARL_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.enderpearl-timer"));
		ENDERPEARL_COLOUR = ChatColor
				.getByChar(config.getString("timers.enderpearl-color", "&e").replace("&", "").trim());
		ARCHER_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.archer-timer"));
		ARCHER_COLOUR = ChatColor.getByChar(config.getString("timers.archer-color", "&c").replace("&", "").trim());
		NOTCH_APPLE_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.notch-apple-timer"));
		NOTCH_APPLE_COLOUR = ChatColor
				.getByChar(config.getString("timers.notch-apple-color", "&6").replace("&", "").trim());
		APPLE_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.apple-timer"));
		APPLE_COLOUR = ChatColor.getByChar(config.getString("timers.apple-color", "&e").replace("&", "").trim());
		PVP_CLASS_WARMUP_TIMER = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.pvp-class-warmup-timer"));
		PVP_CLASS_WARMUP_COLOUR = ChatColor
				.getByChar(config.getString("timers.pvp-class-warmup-color", "&b").replace("&", "").trim());
		PVPTIMER_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.pvptimer-timer"));
		PVPTIMER_COLOUR = ChatColor
				.getByChar(config.getString("timers.pvptimer-color", "&a").replace("&", "").replace("&l", "").trim());
		SALE_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.sale-timer"));
		SALE_COLOUR = ChatColor.getByChar(config.getString("timers.sale-color", "&e").replace("&", "").trim());
		KEYALL_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.keyall-timer"));
		KEYALL_COLOUR = ChatColor.getByChar(config.getString("timers.keyall-color", "&e").replace("&", "").trim());
		OPKEYALL_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.opkeyall-timer"));
		OPKEYALL_COLOUR = ChatColor.getByChar(config.getString("timers.opkeyall-color", "&e").replace("&", "").trim());
		SPAWNTAG_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.spawntag-timer"));
		SPAWNTAG_COLOUR = ChatColor.getByChar(config.getString("timers.spawntag-color", "&c").replace("&", "").trim());
		STUCK_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.stuck-timer"));
		STUCK_COLOUR = ChatColor.getByChar(config.getString("timers.stuck-color", "&c").replace("&", "").trim());
		TELEPORT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.teleport-timer"));
		TELEPORT_COLOUR = ChatColor.getByChar(config.getString("timers.teleport-color", "&9").replace("&", "").trim());
		EVENT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.event-timer"));
		EVENT_COLOUR = ChatColor.getByChar(config.getString("timers.event-color", "&9&l").replace("&", "").trim());
		ENDERPEARL_COOLDOWN_EXPIRED = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.messages.enderpearl-expired"));
		ENDERPEARL_ITEM = ChatColor.translateAlternateColorCodes('&',
				config.getString("timers.messages.enderpearl-item"));
		SPAWN_TAGGED = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.spawn-tagged"));
		FOOTER_SCOREBOARD = ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.FOOTER"));
		TABLIST_COLOR = ChatColor.translateAlternateColorCodes('&', config.getString("TABLIST.COLOR"));
		UNBUILDABLE_RANGE = config.getInt("unbuildable-range");
		ORIGINS = config.getBoolean("origins", ORIGINS);
	}

	static {
		SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
		DISALLOWED_FACTION_NAMES = HCFaction.getPlugin().getConfig().getStringList("DISABLED_NAME_FACTIONS");
		ENCHANTMENT_LIMITS = new HashMap<Enchantment, Integer>();
		POTION_LIMITS = new EnumMap<PotionType, Integer>(PotionType.class);
		SPAWN_RADIUS_MAP = new EnumMap<World.Environment, Double>(World.Environment.class);
		SPAWNER_PRICE = 10000;
		CONQUEST_REQUIRED_WIN_POINTS = 300;
		DIAMOND_ORE_ALERTS = true;
	}
}
