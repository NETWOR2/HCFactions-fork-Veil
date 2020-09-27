package net.bfcode.bfhcf.combatlog;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.PlayerUtil;

import java.util.Collection;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.OfflinePlayer;
import java.util.Iterator;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import org.bukkit.event.Listener;

public class CombatLogListener implements Listener {
	
	private static Set<UUID> SAFE_DISCONNECTS;
	private static Map<UUID, CombatLogEntry> LOGGERS;
	private HCFaction plugin;

	static {
		SAFE_DISCONNECTS = new HashSet<UUID>();
		LOGGERS = new HashMap<UUID, CombatLogEntry>();
	}

	public CombatLogListener(HCFaction plugin) {
		this.plugin = plugin;
	}

	public static void safelyDisconnect(Player player, String reason) {
		if (CombatLogListener.SAFE_DISCONNECTS.add(player.getUniqueId())) {
			PlayerUtil.sendToHub(player);
		}
	}

	public static void removeCombatLoggers() {
		Iterator<CombatLogEntry> iterator = CombatLogListener.LOGGERS.values().iterator();
		while (iterator.hasNext()) {
			CombatLogEntry entry = iterator.next();
			entry.task.cancel();
			entry.loggerEntity.getBukkitEntity().remove();
			iterator.remove();
		}
		CombatLogListener.SAFE_DISCONNECTS.clear();
	}

	public static void removeCombatLogger(OfflinePlayer player) {
		CombatLogEntry entry = CombatLogListener.LOGGERS.get(player.getUniqueId());
		entry.task.cancel();
		entry.loggerEntity.getBukkitEntity().remove();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuitSafe(PlayerQuitEvent event) {
		CombatLogListener.SAFE_DISCONNECTS.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLoggerInteract(EntityInteractEvent event) {
		Collection<CombatLogEntry> entries = CombatLogListener.LOGGERS.values();
		for (CombatLogEntry entry : entries) {
			if (entry.loggerEntity.getBukkitEntity().equals((Object) event.getEntity())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLoggerDeath(LoggerDeathEvent event) {
		CombatLogEntry entry = CombatLogListener.LOGGERS.remove(event.getLoggerEntity().getPlayerUUID());
		if (entry != null) {
			entry.task.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		CombatLogEntry combatLogEntry = CombatLogListener.LOGGERS.remove(event.getPlayer().getUniqueId());
		if (combatLogEntry != null) {
			CraftLivingEntity loggerEntity = combatLogEntry.loggerEntity.getBukkitEntity();
			Player player = event.getPlayer();
			event.setSpawnLocation(loggerEntity.getLocation());
			player.setFallDistance(loggerEntity.getFallDistance());
			player.setRemainingAir(loggerEntity.getRemainingAir());
			loggerEntity.remove();
			combatLogEntry.task.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerInventory inventory = player.getInventory();
		if (player.getGameMode() != GameMode.CREATIVE && !player.isDead()
				&& !CombatLogListener.SAFE_DISCONNECTS.contains(uuid) && StaffModeCommand.isMod(player)) {
			if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(uuid) > 0L) {
				return;
			}
			if (this.plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, 64) <= 0) {
				return;
			}
			Location location = player.getLocation();
			if (this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
				return;
			}
			if (CombatLogListener.LOGGERS.containsKey(player.getUniqueId())) {
				return;
			}
			World world = location.getWorld();
			LoggerEntity loggerEntity = new LoggerEntity(world, location, player);
			LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
			Bukkit.getPluginManager().callEvent((Event) calledEvent);
			CombatLogListener.LOGGERS.put(uuid, new CombatLogEntry(loggerEntity,
					new LoggerRemovable(uuid, loggerEntity).runTaskLater((Plugin) this.plugin, 600L)));
			CraftEntity craftEntity = (CraftEntity) loggerEntity.getBukkitEntity();
			if (craftEntity != null) {
				CraftLivingEntity craftLivingEntity = (CraftLivingEntity) craftEntity;
				EntityEquipment entityEquipment = craftLivingEntity.getEquipment();
				entityEquipment.setItemInHand(inventory.getItemInHand());
				entityEquipment.setArmorContents(inventory.getArmorContents());
			}
		}
	}

	private static class LoggerRemovable extends BukkitRunnable {
		private UUID uuid;
		private LoggerEntity loggerEntity;

		public LoggerRemovable(UUID uuid, LoggerEntity loggerEntity) {
			this.uuid = uuid;
			this.loggerEntity = loggerEntity;
		}

		public void run() {
			if (CombatLogListener.LOGGERS.remove(this.uuid) != null) {
				this.loggerEntity.dead = true;
			}
		}
	}
}
