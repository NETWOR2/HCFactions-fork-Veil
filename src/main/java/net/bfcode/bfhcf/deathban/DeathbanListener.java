package net.bfcode.bfhcf.deathban;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class DeathbanListener implements Listener {
	
	private static long LIFE_USE_DELAY_MILLIS;
	private static String LIFE_USE_DELAY_WORDS;
	private static String DEATH_BAN_BYPASS_PERMISSION = "hcf.deathban.bypass";
	private ConcurrentMap<Object, Object> lastAttemptedJoinMap;
	private HCFaction plugin;

	public DeathbanListener(HCFaction plugin) {
		this.plugin = plugin;
		this.lastAttemptedJoinMap = (ConcurrentMap<Object, Object>) CacheBuilder.newBuilder()
				.expireAfterWrite(DeathbanListener.LIFE_USE_DELAY_MILLIS, TimeUnit.MILLISECONDS).build().asMap();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
		Deathban deathban = user.getDeathban();
		if (deathban == null || !deathban.isActive()) {
			return;
		}
		if (player.hasPermission(DEATH_BAN_BYPASS_PERMISSION)) {
			return;
		}
		if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ConfigurationService.DEATHBANNED_EOTW);
		} else {
			UUID uuid = player.getUniqueId();
			int lives = this.plugin.getDeathbanManager().getLives(uuid);
			String formattedDuration = HCFaction.getRemaining(deathban.getRemaining(), true, false);
			String reason = deathban.getReason();
			String prefix = ConfigurationService.DEATHBANNED_ACTIVE.replace("%reason%", reason).replace("%time%",
					formattedDuration) + ChatColor.RED + " You currently have " + ((lives <= 0) ? "no" : lives)
					+ " lives.";
			if (lives > 0) {
				long millis = System.currentTimeMillis();
				Long lastAttemptedJoinMillis = (Long) this.lastAttemptedJoinMap.get(uuid);
				if (lastAttemptedJoinMillis != null && lastAttemptedJoinMillis
						- System.currentTimeMillis() < DeathbanListener.LIFE_USE_DELAY_MILLIS) {
					this.lastAttemptedJoinMap.remove(uuid);
					user.removeDeathban();
					lives = this.plugin.getDeathbanManager().takeLives(uuid, 1);
					event.setResult(PlayerLoginEvent.Result.ALLOWED);
					new LoginMessageRunnable(player,
							ChatColor.YELLOW + "You have used a life bypass your death. You now have " + ChatColor.GOLD
									+ lives + ChatColor.YELLOW + " lives.").runTask(this.plugin);
				} else {
					this.lastAttemptedJoinMap.put(uuid, millis + DeathbanListener.LIFE_USE_DELAY_MILLIS);
					event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
							prefix + ChatColor.GOLD + "\n\nYou may use a life by reconnecting within " + ChatColor.WHITE
									+ DeathbanListener.LIFE_USE_DELAY_WORDS + ChatColor.GOLD + '.');
				}
				return;
			}
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
					ChatColor.RED + "Deathbanned for " + formattedDuration + ChatColor.RED + "\nReason: "
							+ ChatColor.WHITE + deathban.getReason() + "\n" + ChatColor.YELLOW
							+ "\nYou can purchase lives at " + ConfigurationService.STORE + ".");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (ConfigurationService.KIT_MAP) {
			return;
		}
		Player player = event.getEntity();
		if (player.hasPermission(DEATH_BAN_BYPASS_PERMISSION)) {
			return;
		}
		Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
		String durationString = HCFaction.getRemaining(deathban.getRemaining(), true, false);
		String formattedDuration = HCFaction.getRemaining(deathban.getRemaining(), true, false);
		if (player.hasPermission("deathban.nokick")) {
			return;
		}
		new BukkitRunnable() {
			public void run() {
				if (DeathbanListener.this.plugin.getEotwHandler().isEndOfTheWorld()) {
					player.kickPlayer(ConfigurationService.DEATHBANNED_EOTW_ENTIRE);
				} else {
					player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "You have been " + ChatColor.RED
							+ "Death-banned");
					player.sendMessage(ChatColor.YELLOW + " ");
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "This deathban expires in "
							+ ChatColor.GOLD + durationString);
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "Reason: " + ChatColor.RED
							+ ChatColor.stripColor(deathban.getReason()));
					player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					try {
						out.writeUTF("Connect");
						out.writeUTF("lobby");
					} catch (IOException e) {
						player.sendMessage(ChatColor.RED + "Error while trying to connect to the lobby.");
					}
					player.sendPluginMessage(DeathbanListener.this.plugin, "BungeeCord", b.toByteArray());
					new BukkitRunnable() {
						public void run() {
							player.kickPlayer(ChatColor.RED + "Deathbanned for " + ChatColor.RED + formattedDuration
									+ ChatColor.RED + ".\n" + ChatColor.RED + "Reason: " + ChatColor.WHITE
									+ deathban.getReason() + "\n" + ChatColor.YELLOW + "\nYou can purchase lives at "
									+ ConfigurationService.STORE + ".");
						}
					}.runTaskLater(BasePlugin.getPlugin(), 20L);
				}
			}
		}.runTaskLater(this.plugin, 20L);
	}

	static {
		LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(10L);
		LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(DeathbanListener.LIFE_USE_DELAY_MILLIS, true,
				true);
	}

	private static class LoginMessageRunnable extends BukkitRunnable {
		private Player player;
		private String message;

		public LoginMessageRunnable(Player player, String message) {
			this.player = player;
			this.message = message;
		}

		public void run() {
			this.player.sendMessage(this.message);
		}
	}
}
