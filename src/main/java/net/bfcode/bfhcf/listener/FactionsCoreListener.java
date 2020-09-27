package net.bfcode.bfhcf.listener;

import com.google.common.collect.Sets;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfbase.listener.VanishListener;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.faction.event.CaptureZoneEnterEvent;
import net.bfcode.bfhcf.faction.event.CaptureZoneLeaveEvent;
import net.bfcode.bfhcf.faction.event.PlayerClaimEnterEvent;
import net.bfcode.bfhcf.faction.struct.Raidable;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.faction.type.WarzoneFaction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.faction.CapturableFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.mountain.DestroyTheCoreFaction;
import net.bfcode.bfhcf.mountain.GlowstoneFaction;
import net.bfcode.bfhcf.mountain.OreFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Cauldron;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Horse;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.TravelAgent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import java.util.Iterator;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import java.util.Objects;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMultimap;
import org.bukkit.event.Listener;

public class FactionsCoreListener implements Listener
{
    public static String PROTECTION_BYPASS_PERMISSION = "hcf.faction.protection.bypass";
    private static ImmutableMultimap<Object, Object> ITEM_BLOCK_INTERACTABLES;
    private static ImmutableSet<Material> BLOCK_INTERACTABLES;
    private HCFaction plugin;
    
    public FactionsCoreListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public static boolean attemptBuild(Entity entity, Location location, String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }
    
    public static boolean attemptBuild(Entity entity, Location location, String denyMessage, boolean isInteraction) {
        boolean result = false;
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player != null && player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
                return true;
            }
            if (player != null && player.getWorld().getEnvironment() == World.Environment.THE_END) {
                player.sendMessage(ConfigurationService.END_CANNOT_BUILD);
                return false;
            }
            Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(location);
            if (!(factionAt instanceof ClaimableFaction)) {
                result = true;
            }
            else if (factionAt instanceof Raidable && ((Raidable)factionAt).isRaidable()) {
                result = true;
            }
            PlayerFaction playerFaction;
            if (player != null && factionAt instanceof PlayerFaction && (playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId())) != null && playerFaction.equals(factionAt)) {
                result = true;
            }
            if (result) {
                if (!isInteraction && Math.abs(location.getBlockX()) <= ConfigurationService.UNBUILDABLE_RANGE && Math.abs(location.getBlockZ()) <= ConfigurationService.UNBUILDABLE_RANGE) {
                    if (denyMessage != null && player != null) {
                        player.sendMessage(ConfigurationService.WORLD_CANNOT_BUILD);
                    }
                    return false;
                }
            }
            else if (denyMessage != null && player != null) {
                player.sendMessage(String.format(denyMessage, factionAt.getDisplayName((CommandSender)player)));
            }
        }
        return result;
    }
    
    public static boolean canBuildAt(Location from, Location to) {
        Faction toFactionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(to);
        return !(toFactionAt instanceof Raidable) || ((Raidable)toFactionAt).isRaidable() || toFactionAt.equals(HCFaction.getPlugin().getFactionManager().getFactionAt(from));
    }
    
    private void handleMove(PlayerMoveEvent event, PlayerClaimEnterEvent.EnterCause enterCause) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        boolean cancelled = false;
        Faction fromFaction = this.plugin.getFactionManager().getFactionAt(from);
        Faction toFaction;
        if (!Objects.equals(fromFaction, toFaction = this.plugin.getFactionManager().getFactionAt(to))) {
            PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            Bukkit.getPluginManager().callEvent((Event)calledEvent);
            cancelled = calledEvent.isCancelled();
        }
        else if (toFaction instanceof CapturableFaction) {
            CapturableFaction capturableFaction = (CapturableFaction)toFaction;
            for (CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                Cuboid cuboid = captureZone.getCuboid();
                if (cuboid == null) {
                    continue;
                }
                boolean containsFrom = cuboid.contains(from);
                boolean containsTo = cuboid.contains(to);
                if (containsFrom && !containsTo) {
                    CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                    Bukkit.getPluginManager().callEvent((Event)calledEvent2);
                    cancelled = calledEvent2.isCancelled();
                    break;
                }
                if (containsFrom) {
                    continue;
                }
                if (!containsTo) {
                    continue;
                }
                if(StaffModeCommand.isMod(player) || VanishListener.isVanished(player) || player.getGameMode() == GameMode.CREATIVE) {
                	return;
                }
                CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                Bukkit.getPluginManager().callEvent((Event)calledEvent3);
                cancelled = calledEvent3.isCancelled();
                break;
            }
        }
        if (cancelled) {
            if (enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                event.setCancelled(true);
            }
            else {
                from.add(0.5, 0.0, 0.5);
                event.setTo(from);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getBlockY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
            this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerTeleportEvent event) {
    	if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
    		return;
    	}
    	this.handleMove((PlayerMoveEvent)event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        switch (event.getCause()) {
            case FLINT_AND_STEEL:
            case ENDER_CRYSTAL: {}
            default: {
                Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
                if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        Faction targetFaction;
        if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && (targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation())) instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        Location retractLocation = event.getRetractLocation();
        Block retractBlock = retractLocation.getBlock();
        if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            Block block = event.getBlock();
            Faction targetFaction = this.plugin.getFactionManager().getFactionAt(retractLocation);
            if (targetFaction instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Block fromBlock = event.getBlock();
        Material fromType = fromBlock.getType();
        Material toType = toBlock.getType();
        if ((toType == Material.REDSTONE_WIRE || toType == Material.TRIPWIRE) && (fromType == Material.AIR || fromType == Material.STATIONARY_LAVA || fromType == Material.LAVA)) {
            toBlock.setType(Material.AIR);
        }
        if ((toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER || toBlock.getType() == Material.LAVA || toBlock.getType() == Material.STATIONARY_LAVA) && !canBuildAt(fromBlock.getLocation(), toBlock.getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && (this.plugin.getFactionManager().getFactionAt(event.getTo())).isSafezone() && !this.plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
            Player player = event.getPlayer();
            player.sendMessage(ConfigurationService.FAILED_PEARL);
            this.plugin.getTimerManager().enderPearlTimer.refund(player);
            event.setCancelled(true);
        }
    }
    
    
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPalacePearl(PlayerTeleportEvent event) {
    	Player shooter = event.getPlayer();
    	Location entityLoc = shooter.getLocation().clone();
        Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(entityLoc);
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && (factionAt instanceof CitadelFaction)) {
            Player player = event.getPlayer();
            player.sendMessage(CC.translate("&cNo puedes usar la enderpearl en un evento Citadel, se te ha devuelto la Enderpearl."));
            this.plugin.getTimerManager().enderPearlTimer.refund(player);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Location from = event.getFrom();
            Location to = event.getTo();
            Player player = event.getPlayer();
            Faction fromFac = this.plugin.getFactionManager().getFactionAt(from);
            if (fromFac.isSafezone()) {
                event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0.0, 0.5));
                event.useTravelAgent(false);
                player.sendMessage(ConfigurationService.TELEPORTED_SPAWN);
                return;
            }
            if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                TravelAgent travelAgent = event.getPortalTravelAgent();
                if (!travelAgent.getCanCreatePortal()) {
                    return;
                }
                Location foundPortal = travelAgent.findPortal(to);
                if (foundPortal != null) {
                    return;
                }
                Faction factionAt = this.plugin.getFactionManager().getFactionAt(to);
                if (factionAt instanceof ClaimableFaction) {
                    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                    if (playerFaction != null && playerFaction.equals(factionAt)) {
                        return;
                    }
                    player.sendMessage(ChatColor.AQUA + "Portal would have created portal in territory of " + factionAt.getDisplayName((CommandSender)player) + ChatColor.YELLOW + '.');
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }
        Location location = event.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE && !this.plugin.getTournamentManager().isInTournament(player)) {
                event.setCancelled(true);
            }
            Player attacker;
            if ((attacker = BukkitUtils.getFinalAttacker(event, true)) != null) {
                Faction attackerFactionAt = this.plugin.getFactionManager().getFactionAt(attacker.getLocation());
                if (attackerFactionAt.isSafezone() && !this.plugin.getTournamentManager().isInTournament(attacker)) {
                    event.setCancelled(true);
                    this.plugin.getMessage().sendMessage(attacker, ConfigurationService.CANNOT_ATTACK);
                    return;
                }
                if (playerFactionAt.isSafezone() && !this.plugin.getTournamentManager().isInTournament(attacker)) {
                    this.plugin.getMessage().sendMessage(attacker, ConfigurationService.CANNOT_ATTACK);
                    return;
                }
                PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                PlayerFaction attackerFaction;
                if (playerFaction != null && (attackerFaction = this.plugin.getFactionManager().getPlayerFaction(attacker)) != null) {
                	
					if (this.plugin.getTournamentManager().isInTournament(player) && this.plugin.getTournamentManager().isInTournament(attacker)) {
                        return;
                    }

                    if (attackerFaction.equals(playerFaction)) {
                    	if (playerFaction.isFriendlyFire()) {
                    		event.setCancelled(false);
                    		return;
                    	}
                        this.plugin.getMessage().sendMessage(attacker, ConfigurationService.IN_FACTION.replace("%player%", player.getName()));
                        event.setCancelled(true);
                    }
                    
                    else if (attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
                        this.plugin.getMessage().sendMessage(attacker, ConfigurationService.ALLY_FACTION.replace("%allyplayer%", player.getName()));
                        event.setCancelled(true);
                    }
                    
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        AnimalTamer owner;
        if (entered instanceof Player && (event.getVehicle()) instanceof Horse && (owner = ((Horse)event.getVehicle()).getOwner()) != null && !owner.equals(entered)) {
            ((Player)entered).sendMessage(ChatColor.AQUA + "You cannot enter a Horse that belongs to " + ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (!BukkitUtils.isDebuff(potion)) {
            return;
        }
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(potion.getLocation());
        if (factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }
        ProjectileSource source = potion.getShooter();
        if (source instanceof Player) {
            Player player = (Player)source;
            for (LivingEntity affected : event.getAffectedEntities()) {
                Player target;
                if (affected instanceof Player && !player.equals(affected) && !(target = (Player)affected).equals(source)) {
                    if (!this.plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
                        continue;
                    }
                    event.setIntensity(affected, 0.0);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent event) {
        switch (event.getReason()) {
            case CLOSEST_PLAYER:
            case RANDOM_TARGET: {
                Entity target = event.getTarget();
                if (event.getEntity() instanceof LivingEntity && target instanceof Player) {
                    Faction factionAt;
                    if (!(factionAt = this.plugin.getFactionManager().getFactionAt(target.getLocation())).isSafezone()) {
                        PlayerFaction playerFaction;
                        if ((playerFaction = this.plugin.getFactionManager().getPlayerFaction((Player)target)) == null) {
                            break;
                        }
                        if (!factionAt.equals(playerFaction)) {
                            break;
                        }
                    }
                    event.setCancelled(true);
                    break;
                }
                break;
            }
		default:
			break;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        Block block = event.getClickedBlock();
        Action action = event.getAction();
        if (action == Action.PHYSICAL && !attemptBuild(event.getPlayer(), block.getLocation(), null)) {
        	Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(event.getPlayer().getLocation());
        	if(factionAt instanceof WarzoneFaction || factionAt instanceof EventFaction) {
	        	if(block.getType() == Material.STONE_PLATE) {
	        		return;
	        	}
        	}
            event.setCancelled(true);
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean canBuild = !FactionsCoreListener.BLOCK_INTERACTABLES.contains(block.getType());
            if (canBuild) {
                Material itemType = (event.hasItem() ? event.getItem().getType() : null);
                if (itemType != null && FactionsCoreListener.ITEM_BLOCK_INTERACTABLES.containsKey(itemType) && FactionsCoreListener.ITEM_BLOCK_INTERACTABLES.get(itemType).contains(event.getClickedBlock().getType())) {
                    canBuild = false;
                }
                else {
                    MaterialData materialData = block.getState().getData();
                    if (materialData instanceof Cauldron && !((Cauldron)materialData).isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
                        canBuild = false;
                    }
                }
            }
            if (!block.getType().equals(Material.WORKBENCH) && !canBuild && !attemptBuild((Entity)event.getPlayer(), block.getLocation(), ChatColor.AQUA + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event) {
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable)factionAt).isRaidable())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeavesDelay(LeavesDecayEvent event) {
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockForm(BlockFormEvent event) {
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(event.getBlock().getLocation());
        if(factionAt instanceof GlowstoneFaction) {
        	if(event.getBlock().getType() == Material.GLOWSTONE) {
        		return;
        	}
        }
//        if(factionAt instanceof DestroyTheCoreFaction) {
//        	if(event.getBlock().getType() == Material.OBSIDIAN) {
//        		return;
//        	}
//        }
        if(factionAt instanceof OreFaction) {
        	if(event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.COAL_ORE || 
        			event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.EMERALD_ORE || 
        			event.getBlock().getType() == Material.GLOWING_REDSTONE_ORE || event.getBlock().getType() == Material.GOLD_ORE || 
        			event.getBlock().getType() == Material.LAPIS_ORE || event.getBlock().getType() == Material.QUARTZ_ORE || 
        			event.getBlock().getType() == Material.REDSTONE_ORE) {
        		return;
        	}
        }
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlock().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (remover instanceof Player && !attemptBuild(remover, event.getEntity().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getEntity().getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Hanging && !attemptBuild((Entity)(BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false)), entity.getLocation(), ChatColor.AQUA + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Hanging && !attemptBuild((Entity)event.getPlayer(), entity.getLocation(), ChatColor.RED + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    static {
        ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.builder().put(Material.DIAMOND_HOE, Material.GRASS).put(Material.GOLD_HOE, Material.GRASS).put(Material.IRON_HOE, Material.GRASS).put(Material.STONE_HOE, Material.GRASS).put(Material.WOOD_HOE, Material.GRASS).build();
        BLOCK_INTERACTABLES = Sets.immutableEnumSet(Material.BED, new Material[] { Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE });
    }
}
