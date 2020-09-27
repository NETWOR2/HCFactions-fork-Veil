package net.bfcode.bfhcf.classes.type;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Cooldowns;

import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class HulkClass extends PvpClass implements Listener {
	
	private HCFaction plugin;
    
    public HulkClass(HCFaction plugin) {
        super("Hulk", TimeUnit.SECONDS.toMillis(5L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
    }
    
    @Override
    public void onUnequip(Player player) {
        super.onUnequip(player);
    }
    
    @Override
    public boolean onEquip(Player player) {
    	if(player.hasPermission("hcf.class.hulk")) {
            return super.onEquip(player);
    	} else {
    		player.sendMessage(ChatColor.RED + "You dont have permissions for use this Class.");
    		return false;
    	}
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onHulkSpecialItemClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.FEATHER) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            if (HCFaction.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()) {
    			return;
    		}
            if (Cooldowns.isOnCooldown("HULK_FEATHER_COOLDOWN", p)) {
                p.sendMessage(CC.translate(ChatColor.RED + "You cannot use this for another &l" + Cooldowns.getCooldownForPlayerInt("HULK_FEATHER_COOLDOWN", p) + ChatColor.RED.toString() + " seconds."));
                event.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("HULK_FEATHER_COOLDOWN", p, 90);
            p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "Hulk Feather effect activated.");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            
            p.setVelocity(p.getLocation().getDirection().normalize().add(new Vector(0.0D, 5.0D, 0.0D)));
            Cooldowns.addCooldown("JUMP_HULK_COOLDOWN", p, 5);
            Cooldowns.addCooldown("FALL_DAMAGE_HULK", p, 10);
            if(p.isSneaking() && Cooldowns.isOnCooldown("JUMP_HULK_COOLDOWN", p)) {
            	p.setVelocity(p.getLocation().getDirection().normalize().add(new Vector(0.0D, -5.0D, 0.0D)));
            }
        }
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.IRON_INGOT) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            if (HCFaction.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()) {
    			return;
    		}
            if (Cooldowns.isOnCooldown("HULK_INGOT_COOLDOWN", p)) {
                p.sendMessage(CC.translate(ChatColor.RED + "You cannot use this for another &l" + Cooldowns.getCooldownForPlayerInt("HULK_INGOT_COOLDOWN", p) + ChatColor.RED.toString() + " seconds."));
                event.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("HULK_INGOT_COOLDOWN", p, 60);
            p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "Hulk Ingot effect activated.");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 3));
            return;
        }
    }
    
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause().equals(DamageCause.FALL)) {
                if (Cooldowns.isOnCooldown("FALL_DAMAGE_HULK", player)) {
                    for(Player players : getNearEnemies(player)) {
                    	players.damage(10.0);
                    	players.playSound(players.getLocation(), Sound.EXPLODE, 6, 1);
                    }
                    player.playSound(player.getLocation(), Sound.EXPLODE, 6, 1);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    private List<Player> getNearEnemies(Player player) {
        List<Player> players = new ArrayList<Player>();
        Collection<Entity> nearby = (Collection<Entity>)player.getNearbyEntities(4.0, 4.0, 4.0);
        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                Player target = (Player)entity;
                if (!target.canSee(player)) {
                    continue;
                }
                if (!player.canSee(target)) {
                    continue;
                }
                players.add(target);
            }
        }
        return players;
    }
    
    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.DIAMOND_LEGGINGS) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.DIAMOND_BOOTS;
    }
}
