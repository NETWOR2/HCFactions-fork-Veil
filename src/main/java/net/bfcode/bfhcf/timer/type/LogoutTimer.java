package net.bfcode.bfhcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.combatlog.CombatLogListener;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.Listener;

public class LogoutTimer extends PlayerTimer implements Listener
{
    public LogoutTimer() {
        super(ConfigurationService.LOGOUT_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.logout-time")), false);
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.LOGOUT_COLOUR;
    }
    
    private void checkMovement(Player player, Location from, Location to) {
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You moved a block, " + this.getDisplayName() + ChatColor.RED + " timer cancelled.");
            this.clearCooldown(player);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Player player;
        if (entity instanceof Player && this.getRemaining(player = (Player)entity) > 0L) {
            player.sendMessage(ChatColor.RED + "You were damaged, " + this.getDisplayName() + ChatColor.RED + " timer ended.");
            this.clearCooldown(player);
        }
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        CombatLogListener.safelyDisconnect(player, ConfigurationService.LOGOUT_DISCONNECT);
    }
    
    public void run(Player player) {
        long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(ChatColor.YELLOW + "Logging out in: " + ChatColor.RED + HCFaction.getRemaining(remainingMillis, true));
            player.sendMessage(this.getDisplayName() + ChatColor.YELLOW + " timer is disconnecting you in " + ChatColor.RED + ChatColor.BOLD + HCFaction.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}
