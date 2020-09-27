package net.bfcode.bfhcf.timer.type;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.TimerRunnable;
import net.bfcode.bfhcf.user.Config;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PlayerInventory;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.HumanEntity;
import java.util.Map;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collection;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.ChatColor;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.event.Listener;

public class EnderPearlTimer extends PlayerTimer implements Listener {
	
    private ConcurrentMap<Object, Object> itemNameFakes;
    private JavaPlugin plugin;
    
    public EnderPearlTimer(JavaPlugin plugin) {
        super(ConfigurationService.ENDERPEARL_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.enderpearl-time")));
        this.plugin = plugin;
        this.itemNameFakes = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 1500L + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.ENDERPEARL_COLOUR;
    }
    
    @Override
    public void load(Config config) {
        super.load(config);
        Collection<UUID> cooldowned = this.cooldowns.keySet();
        for (UUID uuid : cooldowned) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            this.startDisplaying(player);
        }
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        super.onExpire(userUUID);
        Player player = Bukkit.getPlayer(userUUID);
        if (player != null) {
            player.sendMessage(ConfigurationService.ENDERPEARL_COOLDOWN_EXPIRED);
        }
    }
    
    @Override
    public TimerRunnable clearCooldown(UUID playerUUID) {
        TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.itemNameFakes.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void clearCooldown(Player player) {
        this.stopDisplaying(player);
        super.clearCooldown(player);
    }
    
    public void refund(Player player) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ENDER_PEARL, 1) });
        this.clearCooldown(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EnderPearl enderPearl;
        ProjectileSource source;
        if (projectile instanceof EnderPearl && (source = (enderPearl = (EnderPearl)projectile).getShooter()) instanceof Player) {
            Player shooter = (Player)source;
            long remaining = this.getRemaining(shooter);
            if (remaining > 0L) {
                shooter.sendMessage(ChatColor.RED + "You cannot use" + ChatColor.YELLOW + " Enderpearl" + ChatColor.RED + " for another " + ChatColor.BOLD + HCFaction.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                event.setCancelled(true);
//                shooter.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ENDER_PEARL) });
                return;
            }
            if (this.setCooldown(shooter, shooter.getUniqueId(), this.defaultCooldown, true)) {
                this.startDisplaying(shooter);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.clearCooldown(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.clearCooldown(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
        if (pearlNameFaker != null) {
            int previousSlot = event.getPreviousSlot();
            ItemStack item = player.getInventory().getItem(previousSlot);
            if (item == null) {
                return;
            }
            pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(item), previousSlot);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if (pearlNameFaker == null) {
                return;
            }
            for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
                if (entry.getKey() == player.getInventory().getHeldItemSlot()) {
                    pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot());
                    break;
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if (pearlNameFaker == null) {
                return;
            }
            int heldSlot = player.getInventory().getHeldItemSlot();
            if (event.getSlot() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), heldSlot);
            }
            else if (event.getHotbarButton() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(event.getCurrentItem()), event.getSlot());
                new BukkitRunnable() {
                    public void run() {
                        player.updateInventory();
                    }
                }.runTask(this.plugin);
            }
        }
    }
    
    public void startDisplaying(Player player) {
        if (this.getRemaining(player) > 0L) {
            PearlNameFaker pearlNameFaker = new PearlNameFaker(this, player);
            if (this.itemNameFakes.putIfAbsent(player.getUniqueId(), pearlNameFaker) == null) {
                long ticks = (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47) ? 20L : 2L;
                pearlNameFaker.runTaskTimerAsynchronously(this.plugin, ticks, ticks);
            }
        }
    }
    
    public void stopDisplaying(Player player) {
        PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.remove(player.getUniqueId());
        if (pearlNameFaker != null) {
            pearlNameFaker.cancel();
        }
    }
    
    public static class PearlNameFaker extends BukkitRunnable
    {
        private PlayerTimer timer;
        private Player player;
        
        public PearlNameFaker(PlayerTimer timer, Player player) {
            this.timer = timer;
            this.player = player;
        }
        
        public void run() {
            ItemStack stack = this.player.getItemInHand();
            if (stack != null && stack.getType() == Material.ENDER_PEARL) {
                long remaining = this.timer.getRemaining(this.player);
                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                if (remaining > 0L) {
                    item = item.cloneItemStack();
                    item.c(ConfigurationService.ENDERPEARL_ITEM.replace("%time%", HCFaction.getRemaining(remaining, true, true)));
                    this.setFakeItem(item, this.player.getInventory().getHeldItemSlot());
                }
                else {
                    this.cancel();
                }
            }
        }
        
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.setFakeItem(CraftItemStack.asNMSCopy(this.player.getItemInHand()), this.player.getInventory().getHeldItemSlot());
        }
        
        public void setFakeItem(net.minecraft.server.v1_7_R4.ItemStack nms, int index) {
            EntityPlayer entityPlayer = ((CraftPlayer)this.player).getHandle();
            if (index < PlayerInventory.getHotbarSize()) {
                index += 36;
            }
            else if (index > 35) {
                index = 8 - (index - 36);
            }
            entityPlayer.playerConnection.sendPacket((Packet)new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, index, nms));
        }
    }
}
