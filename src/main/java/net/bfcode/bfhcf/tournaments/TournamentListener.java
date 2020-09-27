package net.bfcode.bfhcf.tournaments;

import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.bfcode.bfbase.kit.event.KitApplyEvent;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.file.TournamentFile;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ItemMaker;
import net.bfcode.bfhcf.utils.LocationUtils;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class TournamentListener implements Listener {
	
    private HCFaction plugin;
    
    public TournamentListener(HCFaction plugin) {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = HCFaction.getPlugin();
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        if (this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cDo not have permissions to Place blocks in the event."));
        }
    }
    
    @EventHandler
    public void onKitApply(KitApplyEvent event) {
        if (this.plugin.getTournamentManager().isInTournament(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getTournamentManager().isInTournament(player)
        		&& (event.getMessage().equalsIgnoreCase("/staff")
        				|| event.getMessage().equalsIgnoreCase("/mod")
        				|| event.getMessage().equalsIgnoreCase("/v")
        				|| event.getMessage().equalsIgnoreCase("/vanish")
        				|| event.getMessage().equalsIgnoreCase("/hcf:staff")
        				|| event.getMessage().equalsIgnoreCase("/hcf:vanish")
        				|| event.getMessage().equalsIgnoreCase("/hcf:mod")
        				|| event.getMessage().equalsIgnoreCase("/hcf:v")
        				|| event.getMessage().equalsIgnoreCase("/f")
        				|| event.getMessage().equalsIgnoreCase("hcf:/spawn")
        				|| event.getMessage().equalsIgnoreCase("/spawn")
        				|| event.getMessage().equalsIgnoreCase("/enderchest")
        				|| event.getMessage().equalsIgnoreCase("/ec")
        				|| event.getMessage().equalsIgnoreCase("/echest")
        				|| event.getMessage().equalsIgnoreCase("/chest") 
        				|| event.getMessage().equalsIgnoreCase("/pv")
        				|| event.getMessage().equalsIgnoreCase("/playervault")
        				|| event.getMessage().equalsIgnoreCase("/faction")
        				|| event.getMessage().equalsIgnoreCase("/f")
        				|| event.getMessage().equalsIgnoreCase("/fac")
        				|| event.getMessage().equalsIgnoreCase("/f home")
        				|| event.getMessage().equalsIgnoreCase("/fac home")
        				|| event.getMessage().equalsIgnoreCase("/faction home")
        				|| event.getMessage().equalsIgnoreCase("/kit")
        				|| event.getMessage().equalsIgnoreCase("/gkit")
        				|| event.getMessage().equalsIgnoreCase("/kits")
        				|| event.getMessage().equalsIgnoreCase("/reclaim")
        				|| event.getMessage().equalsIgnoreCase("/more")
        				|| event.getMessage().equalsIgnoreCase("/feed")
        				|| event.getMessage().equalsIgnoreCase("/heal")
        				|| event.getMessage().equalsIgnoreCase("/rename")
        				|| event.getMessage().equalsIgnoreCase("/reclaim")
        				|| event.getMessage().equalsIgnoreCase("/fix")
        				|| event.getMessage().equalsIgnoreCase("/repair")
        				|| event.getMessage().contains("/ec")
        				|| event.getMessage().contains("/enderchest")
        				|| event.getMessage().equalsIgnoreCase("/fixall"))) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou do not have permissions to use commands in Spawn."));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        if (this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId()) && !(this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SPLEEF)) {
            event.setCancelled(true);
        }
        else if(this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId()) && this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SPLEEF && this.plugin.getTournamentManager().getTournament().getProtection() != 0) {
        	if(this.plugin.getTournamentManager().getTournament().isActiveProtection()) {
        		event.setCancelled(true);
        	}
        	if(event.getBlock().getTypeId() == 80) {
                event.getBlock().getDrops().forEach(itemStack -> player.getInventory().addItem(itemStack));
                event.getBlock().setType(Material.AIR);
                TournamentManager.spleefblocks.put(event.getBlock().getLocation(), event.getBlock().getType());
        	} else {
        		event.setCancelled(true);
        	}
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.getItemInHand().getType().equals(Material.NETHER_STAR) && 
            		player.getItemInHand().getItemMeta().hasDisplayName() && 
            		player.getItemInHand().getItemMeta().hasLore() && 
            		this.plugin.getTournamentManager().getTournament() != null && 
            		this.plugin.getTournamentManager().getTournament().getTournamentState() != 
            		TournamentState.FIGHTING && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                player.performCommand("tournament leave");
            }
            Tournament tournament = this.plugin.getTournamentManager().getTournament();
            if (tournament.getType() == TournamentType.DIAMOND && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(1, new ItemMaker(Material.ENDER_PEARL, 16).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                for (int i = 0; i < 17; ++i) {
                    inventory.addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                }
                player.updateInventory();
            }
            if (tournament.getType() == TournamentType.BARD && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.GOLD_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.GOLD_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(1, new ItemMaker(Material.ENDER_PEARL, 16).create());
                inventory.setItem(2, new ItemMaker(Material.SUGAR, 64).create());
                inventory.setItem(3, new ItemMaker(Material.BLAZE_POWDER, 64).create());
                inventory.setItem(6, new ItemMaker(Material.GHAST_TEAR, 64).create());
                inventory.setItem(7, new ItemMaker(Material.IRON_INGOT, 64).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                for (int i = 0; i < 17; ++i) {
                    inventory.addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                }
                player.updateInventory();
            }
            if (tournament.getType() == TournamentType.ASSASSIN && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.CHAINMAIL_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(1, new ItemMaker(Material.ENDER_PEARL, 16).create());
                inventory.setItem(2, new ItemMaker(Material.QUARTZ, 1).enchant(Enchantment.DURABILITY, 10).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                for (int i = 0; i < 17; ++i) {
                    inventory.addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                }
                player.updateInventory();
            }
            if (tournament.getType() == TournamentType.BOMBER && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(1, new ItemMaker(Material.ENDER_PEARL, 16).create());
                inventory.setItem(7, new ItemMaker(Material.STICK, 1).enchant(Enchantment.DURABILITY, 10).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                for (int i = 0; i < 17; ++i) {
                    inventory.addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                }
                player.updateInventory();
            }
            if (tournament.getType() == TournamentType.AXE && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.IRON_AXE).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                for (int i = 0; i < 8; ++i) {
                    inventory.addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                }
                player.updateInventory();
            }
            if (tournament.getType() == TournamentType.ARCHER && player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).create());
                inventory.setItem(0, new ItemMaker(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 4).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.DURABILITY, 3).create());
                inventory.setItem(1, new ItemMaker(Material.ARROW, 1).create());
                inventory.setItem(7, new ItemMaker(Material.GOLDEN_APPLE, 4).create());
                inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO, 64).create());
                player.updateInventory();
            }
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if (player.getItemInHand().getType().equals(Material.ENDER_PEARL) && this.plugin.getTournamentManager().getTournament().getTournamentState() == TournamentState.FIGHTING && this.plugin.getTournamentManager().getTournament().isActiveProtection()) {
                event.setUseItemInHand(Event.Result.DENY);
                player.sendMessage(CC.translate("&cYou cant use enderpearls in grace period!"));
            }
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            event.setCancelled(true);
            player.updateInventory();
            if (this.plugin.getTournamentManager().getTournament().getTournamentState() == TournamentState.FIGHTING) {
                if (player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore()) {
                    player.updateInventory();
                    return;
                }
                player.updateInventory();
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player)event.getEntity();
            Player damager = (Player)event.getDamager();
            if (this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().getTournament().getTournamentState() == TournamentState.FIGHTING && (this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SUMO || this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SPLEEF) && this.plugin.getTournamentManager().isInTournament(player.getUniqueId()) && this.plugin.getTournamentManager().isInTournament(damager.getUniqueId())) {
                event.setDamage(0.0);
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        if (this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().isInTournament(player)) {
            if (to.getBlock().isLiquid() && !player.isDead() && (this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SPLEEF || this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SUMO)) {
                TournamentListener.this.plugin.getTournamentManager().playerLeft(TournamentListener.this.plugin.getTournamentManager().getTournament(), player, false);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        Tournament tournament = this.plugin.getTournamentManager().getTournament();
        if (this.plugin.getTournamentManager().isInTournament(player) && 
        		(tournament.getType() == TournamentType.DIAMOND || 
        		tournament.getType() == TournamentType.BARD || 
        		tournament.getType() == TournamentType.ASSASSIN || 
        		tournament.getType() == TournamentType.BOMBER || 
        		tournament.getType() == TournamentType.AXE || 
        		tournament.getType() == TournamentType.ARCHER || 
        		tournament.getType() == TournamentType.SPLEEF)) {
            event.getDrops().clear();
        }
        
        if (this.plugin.getTournamentManager().isInTournament(player) && tournament.getType() == TournamentType.SUMO) {
        	
            if (player.getKiller() == null) {
            	
            	Player first = tournament.getFirstPlayer();
            	Player second = tournament.getSecondPlayer();
            	
            	if (HCFaction.getPlugin().getTournamentManager().isInTournament(first)) {
            		tournament.teleport(first, "Sumo.Spawn");
            	} 
            	else if (HCFaction.getPlugin().getTournamentManager().isInTournament(second)) {
            		tournament.teleport(second, "Sumo.Spawn");
            	}
            }
            
            else if (player.getKiller() instanceof Player && plugin.getTournamentManager().isInTournament(player.getKiller())) {
                tournament.teleport(player.getKiller(), "Sumo.Spawn");	
            }
        }
        if (this.plugin.getTournamentManager().getTournament() != null && this.plugin.getTournamentManager().getTournament().getTournamentState() == TournamentState.FIGHTING && this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            new BukkitRunnable() {
                public void run() {
                    TournamentListener.this.plugin.getTournamentManager().playerLeft(TournamentListener.this.plugin.getTournamentManager().getTournament(), player, false);
                }
            }.runTaskLater(this.plugin, 20L);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            this.plugin.getTournamentManager().playerLeft(this.plugin.getTournamentManager().getTournament(), player, false);
        }
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            this.plugin.getTournamentManager().playerLeft(this.plugin.getTournamentManager().getTournament(), player, false);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            Tournament tournament = this.plugin.getTournamentManager().getTournament();
            if (tournament != null && this.plugin.getTournamentManager().isInTournament(player)) {
                if (tournament.getTournamentState() == TournamentState.WAITING) {
                    event.setCancelled(true);
                }
                else if (tournament.getTournamentState() == TournamentState.STARTING) {
                    event.setCancelled(true);
                }
                else if (tournament.getTournamentState() == TournamentState.FIGHTING && tournament.isActiveProtection()) {
                    event.setCancelled(true);
                }
                else if (tournament.getTournamentState() == TournamentState.FIGHTING && tournament.getType() == TournamentType.SUMO) {
                    if (tournament.getFirstPlayer() == player || tournament.getSecondPlayer() == player) {
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	Player player = (Player) event.getWhoClicked();
    	if(!this.plugin.getTournamentManager().isInTournament(player)) {
    		return;
    	}
    	if (event.getCurrentItem() == null || event.getSlotType() == null || event.getCurrentItem().getType() == Material.AIR) {
			return;
		}
    	if(event.getCurrentItem().getType().equals(Material.DIAMOND_BOOTS) || event.getCurrentItem().getType().equals(Material.DIAMOND_LEGGINGS) || event.getCurrentItem().getType().equals(Material.DIAMOND_CHESTPLATE) || event.getCurrentItem().getType().equals(Material.DIAMOND_HELMET)) {
    		player.sendMessage(CC.translate("&cYou do not move items in the inventory."));
    		event.setCancelled(true);
    	}
    }
    
//    @EventHandler
//    public void onFoodChange(FoodLevelChangeEvent event) {
//        Player player = (Player)event.getEntity();
//        if (this.plugin.getTournamentManager().isInTournament(player)) {
//            if (this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SUMO) {
//                event.setCancelled(true);
//            }
//            else if(this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SPLEEF) {
//            	event.setCancelled(true);
//            }
//            else if (this.plugin.getTournamentManager().getTournament().getTournamentState() != TournamentState.FIGHTING) {
//                event.setCancelled(true);
//            }
//            if (event.isCancelled()) {
//                player.setFoodLevel(20);
//            }
//        }
//    }
}
