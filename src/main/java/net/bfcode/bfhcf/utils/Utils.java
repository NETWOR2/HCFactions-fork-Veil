package net.bfcode.bfhcf.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.HCFaction;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;

public class Utils {
	
    private static long MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static long HOUR = TimeUnit.HOURS.toMillis(1);
	
    public static String STICK;
	public static String no_faction = CC.translate("&cYou are not in a faction.");
	public static String role = CC.translate("&cYour cannot do this with this role.");
	public static String faction_not_found = CC.translate("&cFaction not found.");
	public static String no_ally = CC.translate("&cAllies are currently disabled this map.");
	
	public static String no_permission = CC.translate("&cNo permissions.");
	public static String no_console = CC.translate("&cNo console.");
	public static String target_offline = CC.translate("&cThat player is currently offline.");
	
	private static Map<UUID, String> uuidToName;
	private static Map<String, UUID> nameToUUID;

	public static UUID uuid(String name) {
		return nameToUUID.get(name.toLowerCase());
	}

	public static String name(UUID uuid) {
		return uuidToName.get(uuid);
    }
    static {
        Utils.STICK = "\u2503";
    }
	public static boolean ensure(UUID uuid) {
		if (String.valueOf(name(uuid)).equals("null")) {
			HCFaction.getPlugin().getLogger().warning(uuid + " didn't have a cached name.");
			return false;
		}
		return true;
	}

	static {
		uuidToName = new ConcurrentHashMap<UUID, String>();
		nameToUUID = new ConcurrentHashMap<String, UUID>();
	}
	
    public static Location destringifyLocation(String string) {
        String[] split = string.substring(1, string.length() - 2).split(",");
        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            return null;
        }
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);
        Location loc = new Location(world, x, y, z);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }
    
    public static List<String> getCompletions(String[] args, List<String> input) {
        return getCompletions(args, input);
    }
    
	public static String stringifyLocation(Location location) {
		return "[" + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + ","
				+ location.getZ() + "," + location.getYaw() + "," + location.getPitch() + "]";
	}
	
	public static String getRemaining(long millis, boolean milliseconds) {
		return getRemaining(millis, milliseconds, true);
	}
	
	public static String getRemainingg(long millis, boolean milliseconds) {
		return getRemainingg(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
		if (milliseconds && duration < MINUTE) {
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
		}
		return DurationFormatUtils.formatDuration((long) duration, (String) ((duration >= HOUR ? "HH:" : "") + "mm:ss"));
	}
	
	public static String getRemainingg(long duration, boolean milliseconds, boolean trail) {
		if (milliseconds && duration < MINUTE) {
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
		}
		return DurationFormatUtils.formatDuration((long) duration, (String) ((duration >= HOUR ? "H:" : "") + "mm:ss"));
	}
	
	public static String TimerFormat(double data) {
		int minutes = (int) (data / 60.0D);
		int seconds = (int) (data % 60);
		String str = String.format("%02d:%02d", new Object[] { Integer.valueOf(minutes), Integer.valueOf(seconds) });
		return str;
	}
	
	public static String formatLongMin(long time) {
        long totalSecs = time / 1000L;
        return String.format("%02d:%02d", totalSecs / 60L, totalSecs % 60L);
    }
	
	public static String formatSecondsToHours(double d) {
		int i = (int) (d / 3600);
		int j = (int) (d % 3600 / 60);
		int k = (int) (d % 60);

		String str = String.format("%02d:%02d:%02d",
				new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) });

		return str;
	}
	
    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        Preconditions.checkNotNull((Object) enumClass);
        Preconditions.checkNotNull((Object) value);
        try {
            return (Optional<T>) Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }

    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return (T) ((first != null) ? first : Preconditions.checkNotNull(second));
    }
    

	public static String handleBardFormat(long millis, boolean trailingZero, boolean showMillis) {
		return (showMillis ? (trailingZero ? Formats.REMAINING_SECONDS_TRAILING : Formats.REMAINING_SECONDS) : Formats.SECONDS).get().format(millis * 0.001);
	}
	
    public static int getProtocolVersion(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
    }

    public static void resendHeldItemPacket(Player player) {
        sendItemPacketAtHeldSlot(player, getCleanHeldItem(player));
    }

    public static void sendItemPacketAtHeldSlot(Player player, net.minecraft.server.v1_7_R4.ItemStack stack) {
        sendItemPacketAtSlot(player, stack, player.getInventory().getHeldItemSlot());
    }

    public static void sendItemPacketAtSlot(Player player, net.minecraft.server.v1_7_R4.ItemStack stack, int index) {
        sendItemPacketAtSlot(player, stack, index, ((CraftPlayer) player).getHandle().defaultContainer.windowId);
    }

    public static void sendItemPacketAtSlot(Player player, net.minecraft.server.v1_7_R4.ItemStack stack, int index, int windowID) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer.playerConnection != null) {
            // Safeguarding
            if (index < net.minecraft.server.v1_7_R4.PlayerInventory.getHotbarSize()) {
                index += 36;
            } else if (index > 35) {
                index = 8 - (index - 36);
            }

            entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(windowID, index, stack));
        }
    }

    public static net.minecraft.server.v1_7_R4.ItemStack getCleanItem(Inventory inventory, int slot) {
        return ((CraftInventory) inventory).getInventory().getItem(slot);
    }

    public static net.minecraft.server.v1_7_R4.ItemStack getCleanItem(Player player, int slot) {
        return getCleanItem(player.getInventory(), slot);
    }

    public static net.minecraft.server.v1_7_R4.ItemStack getCleanHeldItem(Player player) {
        return getCleanItem(player, player.getInventory().getHeldItemSlot());
    
    }
    public static boolean isOnline(CommandSender sender, Player player) {
        return player != null && (!(sender instanceof Player) || ((Player)sender).canSee(player));
    }
    
    public static int getPing(Player player) {
        CraftPlayer craft = (CraftPlayer)player;
        int ping = craft.getHandle().ping - 20;
        return (ping > 0) ? ping : 0;
	}
    
    public static List<Entity> getNearby(Location loc, int distance) {
        List<Entity> list = new ArrayList<Entity>();
        for (Entity e : loc.getWorld().getEntities()) {
            if (e instanceof Player) {
                continue;
            }
            if (!e.getType().isAlive()) {
                continue;
            }
            if (loc.distance(e.getLocation()) > distance) {
                continue;
            }
            list.add(e);
        }
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.getWorld() == loc.getWorld() && loc.distance(online.getLocation()) <= distance) {
                list.add((Entity)online);
            }
        }
        return list;
    }

    public static void setMaxPlayers(int amount) throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle", (Class<?>[])null).invoke(Bukkit.getServer(), (Object[])null);
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, amount);
        
    }

    public static Location getHighestLocation(Location origin) {
        return getHighestLocation(origin, null);
        
	}

    public static Location getHighestLocation(Location origin, Location def) {
        Preconditions.checkNotNull((Object)origin, (Object)"The location cannot be null");
        Location cloned = origin.clone();
        World world = cloned.getWorld();
        int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        int z = cloned.getBlockZ();
        while (y > origin.getBlockY()) {
            Block block = world.getBlockAt(x, --y, z);
            if (!block.isEmpty()) {
                Location next = block.getLocation();
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }
        return def;
    }
}
