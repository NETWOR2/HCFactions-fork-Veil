package net.bfcode.bfhcf.listener;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.base.Predicate;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.visualise.VisualBlock;
import net.bfcode.bfhcf.visualise.VisualType;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class CoreListener implements Listener
{
    private HCFaction plugin;
    
    public CoreListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerJoinKit(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for(String string : HCFaction.getPlugin().getConfig().getStringList("JOIN_MESSAGE")) {
        	p.sendMessage(CC.translate(string
        			.replace("%player%", p.getName())
        			.replace("%line%", BukkitUtils.STRAIGHT_LINE_DEFAULT)));
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            event.getDrops().clear();
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }
        if(reason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
        	return;
        }
        Location location = event.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
        if(ConfigurationService.KIT_MAP && reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLightning(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
    		if(block.getType() == Material.ANVIL) {
    			event.setCancelled(true);
    			player.sendMessage(CC.translate("&cLos yunques estan desactivados!"));
    		}
    		
    	}
    }
}
