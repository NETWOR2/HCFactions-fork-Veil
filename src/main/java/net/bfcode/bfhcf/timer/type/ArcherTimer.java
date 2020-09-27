package net.bfcode.bfhcf.timer.type;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;

import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.type.ArcherClass;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.event.TimerExpireEvent;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.collect.Sets;

import org.bukkit.event.Listener;

public class ArcherTimer extends PlayerTimer implements Listener
{
    private HCFaction plugin;
    
    public ArcherTimer(HCFaction plugin) {
        super(ConfigurationService.ARCHER_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.archer-time")));
        this.plugin = plugin;
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.ARCHER_COLOUR;
    }
    
    public void run() {
    }
    
    @EventHandler
    public void onExpire(TimerExpireEvent e) {
        if (e.getUserUUID().isPresent() && e.getTimer().equals(this)) {
            UUID userUUID = (UUID)e.getUserUUID().get();
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null) {
                return;
            }
            if (!player.isOnline()) {
                return;
            }
            Bukkit.getPlayer((UUID)ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.GOLD + "Your archer mark on " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has expired.");
            player.sendMessage(ChatColor.GOLD + "You are no longer archer marked.");
            ArcherClass.tagged.remove(player.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, () -> {
            	for (Player players : Bukkit.getOnlinePlayers()) {
                    this.plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).init(Sets.newHashSet(Bukkit.getOnlinePlayers()));
                }
            }, 10L);
        }
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player entity = (Player)e.getEntity();
            Entity damager = e.getDamager();
            if (this.getRemaining(entity) > 0L) {
                e.setDamage(e.getDamage() * 1.3);
            }
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && ((Arrow)e.getDamager()).getShooter() instanceof Player) {
            Player entity = (Player)e.getEntity();
            Entity damager = (Entity)((Arrow)e.getDamager()).getShooter();
            if (damager instanceof Player && this.getRemaining(entity) > 0L) {
                if (ArcherClass.tagged.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
                    this.setCooldown(entity, entity.getUniqueId());
                }
                e.setDamage(e.getDamage() * 1.3);
            }
        }
    }
}
