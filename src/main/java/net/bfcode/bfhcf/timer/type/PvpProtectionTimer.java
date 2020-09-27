package net.bfcode.bfhcf.timer.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.google.common.base.Optional;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.event.FactionClaimChangedEvent;
import net.bfcode.bfhcf.faction.event.PlayerClaimEnterEvent;
import net.bfcode.bfhcf.faction.event.cause.ClaimChangeCause;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.TimerRunnable;
import net.bfcode.bfhcf.timer.event.TimerClearEvent;
import net.bfcode.bfhcf.timer.event.TimerStartEvent;
import net.bfcode.bfhcf.user.Config;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.DurationFormatter;
import net.bfcode.bfhcf.visualise.VisualType;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class PvpProtectionTimer extends PlayerTimer implements Listener
{
    private static String PVP_COMMAND = "/pvp enable";
    private static long ITEM_PICKUP_DELAY;
    private static long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
    private static String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";
    private Set<UUID> legible;
    private ConcurrentMap<Object, Object> itemUUIDPickupDelays;
    private HCFaction plugin;
    
    public PvpProtectionTimer(HCFaction plugin) {
        super(ConfigurationService.PVPTIMER_TIMER, TimeUnit.MINUTES.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.pvptimer-time")));
        this.legible = new HashSet<UUID>();
        this.plugin = plugin;
        this.itemUUIDPickupDelays = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(PvpProtectionTimer.ITEM_PICKUP_DELAY + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.PVPTIMER_COLOUR;
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        if (this.getRemaining(player) <= 0L) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYour PvP Protection has expired. PvP is now enabled."));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(TimerClearEvent event) {
        if (event.getTimer().equals(this)) {
            Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire((UUID)optionalUserUUID.get());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(FactionClaimChangedEvent event) {
        if (event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }
        Collection<Claim> claims = event.getAffectedClaims();
        for (Claim claim : claims) {
            Collection<Player> players = (Collection<Player>)claim.getPlayers();
            for (Player player : players) {
                if (this.getRemaining(player) > 0L) {
                    Location location = player.getLocation();
                    location.setX((double)(claim.getMinimumX() - 1));
                    location.setY(0.0);
                    location.setZ((double)(claim.getMinimumZ() - 1));
                    location = BukkitUtils.getHighestLocation(location, location);
                    if (!player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. As you still have your " + this.getName() + " timer, you were teleported away.");
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (ConfigurationService.KIT_MAP) {
            return;
        }
        Player player = event.getPlayer();
        if (this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)) {
            this.setPaused(player, player.getUniqueId(), true);
        }
    }
    
    @EventHandler
    public void onTimer(TimerStartEvent e) {
        if ((ConfigurationService.KIT_MAP) && e.getTimer() instanceof PvpProtectionTimer) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown((UUID)e.getUserUUID().get());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (ConfigurationService.KIT_MAP) {
            return;
        }
        Player player = event.getEntity();
        World world = player.getWorld();
        Location location = player.getLocation();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            this.itemUUIDPickupDelays.put(world.dropItemNaturally(location, (ItemStack)iterator.next()).getUniqueId(), System.currentTimeMillis() + PvpProtectionTimer.ITEM_PICKUP_DELAY);
            iterator.remove();
        }
        this.clearCooldown(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot empty buckets as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot ignite blocks as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            UUID itemUUID = event.getItem().getUniqueId();
            Long delay = (Long) this.itemUUIDPickupDelays.get(itemUUID);
            if (delay == null) {
                return;
            }
            long millis = System.currentTimeMillis();
            if (delay - millis > 0L) {
                event.setCancelled(true);
                List<MetadataValue> value = (List<MetadataValue>)player.getMetadata("pickupMessageDelay");
                if (value != null && !value.isEmpty() && value.get(0).asLong() - millis <= 0L) {
                    player.setMetadata("pickupMessageDelay", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (millis + 1250L)));
                    player.sendMessage(ChatColor.RED + "You cannot pick this item up for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + " as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
                }
            }
            else {
                this.itemUUIDPickupDelays.remove(itemUUID);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TimerRunnable runnable = this.cooldowns.get(player.getUniqueId());
        if (runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore() && !ConfigurationService.KIT_MAP) {
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && this.legible.add(player.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou now have PvP Protection since you have died."));
            }
        }
        else if (this.isPaused(player) && this.getRemaining(player) > 0L && !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
            this.setPaused(player, player.getUniqueId(), false);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            this.clearCooldown(player);
            return;
        }
        Faction toFaction = event.getToFaction();
        Faction fromFaction = event.getFromFaction();
        if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
            if (this.legible.remove(player.getUniqueId())) {
                this.setCooldown(player, player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
                return;
            }
            if (this.getRemaining(player) > 0L) {
                this.setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
            }
        }
        else if (!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has been paused."));
            this.setPaused(player, player.getUniqueId(), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        Faction toFaction = event.getToFaction();
        long remaining;
        if (toFaction instanceof ClaimableFaction && (remaining = this.getRemaining(player)) > 0L) {
            PlayerFaction playerFaction;
            if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction && (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(toFaction)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou have entered your claim, meaning you no longer have PvP Protection."));
                this.clearCooldown(player);
                return;
            }
            if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot enter this claim while you have PvP Protection."));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
            if (attacker == null) {
                return;
            }
            Player player = (Player)entity;
            long remaining;
            if ((remaining = this.getRemaining(player)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " still has PvP Protection."));
                return;
            }
            if ((remaining = this.getRemaining(attacker)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not attack players while you have PvP Protection. Use &6/pvp enable &cto enable PvP"));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for (LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player && this.getRemaining((Player)livingEntity) > 0L) {
                    event.setIntensity(livingEntity, 0.0);
                }
            }
        }
    }
    
    public Set<UUID> getLegible() {
        return this.legible;
    }
    
    @Override
    public long getRemaining(UUID playerUUID) {
        return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
    }
    
    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite) {
        return !this.plugin.getEotwHandler().isEndOfTheWorld() && super.setCooldown(player, playerUUID, duration, overwrite);
    }
    
    @Override
    public TimerRunnable clearCooldown(UUID playerUUID) {
        TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.legible.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void load(Config config) {
        super.load(config);
        Object object = config.get("pvp-timer-legible");
        if (object instanceof List) {
            this.legible.addAll((Collection<? extends UUID>)object);
        }
    }
    
    @Override
    public void onDisable(Config config) {
        super.onDisable(config);
        config.set("pvp-timer-legible", new ArrayList(this.legible).toArray(new UUID[this.legible.size()]));
    }
    
    static {
        ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
    }
}
