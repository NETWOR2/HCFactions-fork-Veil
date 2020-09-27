package net.bfcode.bfhcf.listener;

import dev.hatsur.library.Library;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.entity.Squid;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.material.EnderChest;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.bfcode.bfbase.command.module.essential.FreezeCommand;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.config.PlayerDataFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Utils;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class WorldListener implements Listener {
	
    private HCFaction plugin;
    
    public WorldListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onUserJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	PlayerDataFile data = PlayerDataFile.getConfig();
    	UUID UUID = player.getUniqueId();
    	if(!player.hasPlayedBefore()) {
    		data.set(UUID + ".name", player.getName());
    		data.save();
    		data.reload();
    		player.sendMessage(CC.translate("&aYour profile has been successfully created."));
    		new BukkitRunnable() {
				
				@Override
				public void run() {
		    		Bukkit.broadcastMessage(CC.translate("&3Welcome &r" + Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getFormattedName() + " &3to &l" + (ConfigurationService.KIT_MAP ? "KitMap" : "HCFaction") + " &b[#" + data.getKeys(false).size() + "]"));
				}
			}.runTaskLater(plugin, 10L);
    		return;
    	}
    	return;
    }
    
    @EventHandler
	public void onUserLeft(PlayerQuitEvent event) {
    	PlayerDataFile data = PlayerDataFile.getConfig();
    	data.save();
    	data.reload();
		return;
	}
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner && !player.hasPermission("hcf.faction.protection.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not break spawners in the nether.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner && !player.hasPermission("hcf.faction.protection.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not place spawners in the nether.");
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onBlockChange(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPortalEnter(EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onFreeze(EntityDamageByEntityEvent event) {
    	if(event.getEntity() instanceof Player) {
    		Player player = (Player) event.getEntity();
    		if(event.getDamager() instanceof Player && FreezeCommand.isFrozen(player.getUniqueId())) {
    			Player damager = (Player) event.getDamager();
    			event.setCancelled(true);
    			damager.sendMessage(CC.translate("&cThis player is freeze."));
    		}
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBedEnter(PlayerBedEnterEvent event) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWitherChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @SuppressWarnings("incomplete-switch")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
        switch (event.getBlock().getType()) {
            case SNOW:
            case ICE: {
                event.setCancelled(true);
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), HCFaction.getPlugin().getConfig().getInt("STARTING_BALANCE"));
            event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Squid) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeave(PlayerMoveEvent event) {
    	Player player = event.getPlayer();
    	if(event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END && (event.getPlayer().getLocation().getBlock().getType() == Material.WATER || event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
            Location spawn = Utils.destringifyLocation(HCFaction.getPlugin().getConfig().getString("Locations.end_exit"));
            if(spawn == null) {
            	player.sendMessage(CC.translate("&cYou do not created endexit!."));
            }
            event.getPlayer().teleport(spawn);
    	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller();

        if ((ConfigurationService.KIT_MAP) && event.getEntity().getKiller() != null && !this.plugin.getTournamentManager().isInTournament(killer)) {
            if (killer != player) {
                int mult = this.getMultiplier(killer);
                int eco = HCFaction.getPlugin().getConfig().getInt("STARTING_BALANCE") * mult;
                this.plugin.getEconomyManager().addBalance(killer.getUniqueId(), eco);
                killer.sendMessage(ChatColor.GREEN + "You have gained $" + eco + " for killing " + ChatColor.WHITE + event.getEntity().getName() + ChatColor.GREEN + ". " + ((mult != 1) ? (ChatColor.GRAY + " (x" + mult + " multiplier)") : ""));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + killer.getName() + " " + this.plugin.getConfig().getString("KillReward.name") + " " + this.plugin.getConfig().getInt("KillReward.number"));
            }
        }
    }
    
    @EventHandler
    public void onSpawnMob(CreatureSpawnEvent event) {
    	if(!ConfigurationService.KIT_MAP) {
    		return;
    	}
    	if(ConfigurationService.KIT_MAP && !(event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG)) {
        	event.setCancelled(true);
    	}
    	Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getLocation());
    	if(factionAt instanceof EventFaction || factionAt instanceof RoadFaction) {
    		event.setCancelled(true);
    	}
    }
    
    private int getMultiplier(Player player) {
        for (int i = 5; i > 1; --i) {
            if (player.hasPermission("balance.multiplayer." + i)) {
                return i;
            }
        }
        return 1;
    }
}
