package net.bfcode.bfhcf.classes.type;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.classes.event.PvpClassEquipEvent;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;

public class MinerClass extends PvpClass implements Listener {
	
    private static PotionEffect HEIGHT_INVISIBILITY;
    public static ArrayList<String> miners;
    private HCFaction plugin;
    
    public MinerClass(HCFaction plugin) {
        super("Miner", TimeUnit.SECONDS.toMillis(5L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    }
    
    private void removeInvisibilitySafely(Player player) {
        for (PotionEffect active : player.getActivePotionEffects()) {
            if (active.getType().equals(PotionEffectType.INVISIBILITY) && active.getDuration() > MinerClass.DEFAULT_MAX_DURATION) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste disabled.");
                player.removePotionEffect(active.getType());
                break;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false) != null) {
            Player player = (Player)entity;
            if (this.plugin.getPvpClassManager().hasClassEquipped(player, this) && !HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
                this.removeInvisibilitySafely(player);
            }
        }
    }
    
    @Override
    public void onUnequip(Player player) {
    	if(!HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
	        super.onUnequip(player);
	        this.removeInvisibilitySafely(player);
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        int toX = event.getTo().getBlockX();
        int toY = event.getTo().getBlockY();
        int toZ = event.getTo().getBlockZ();
        if (!HCFaction.getPlugin().getTournamentManager().isInTournament(event.getPlayer()) && from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
            this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	if(!HCFaction.getPlugin().getTournamentManager().isInTournament(event.getPlayer())) {
            this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClassEquip(PvpClassEquipEvent event) {
        Player player = event.getPlayer();
        if (event.getPvpClass() == this && player.getLocation().getBlockY() <= 30 && !HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
            player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
            player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
        }
    }
    
    private void conformMinerInvisibility(Player player, Location from, Location to) {
        int fromY = from.getBlockY();
        int toY = to.getBlockY();
        if (fromY != toY && this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
            boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if (toY > 30) {
                if (fromY <= 30 && isInvisible) {
                    this.removeInvisibilitySafely(player);
                }
            }
            else if (!isInvisible) {
                player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
                player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.IRON_HELMET) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS;
    }
    
    static {
        MinerClass.miners = new ArrayList<String>();
        HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
    }
}
