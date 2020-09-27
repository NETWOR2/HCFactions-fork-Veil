package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

public class FreezeGun implements Listener {
	
	public static Map<String, Long> cooldown;
	public static String name;
  
	static {
		FreezeGun.cooldown = new HashMap<String, Long>();
		FreezeGun.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.FREEZE-GUN.NAME"));
	}

	public static Map<String, Long> getCooldown() {
		return FreezeGun.cooldown;
	}

	public static boolean isOnCooldown(final Player player) {
		return FreezeGun.cooldown.containsKey(player.getName())
				&& FreezeGun.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = FreezeGun.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public static double getCooldownDouble(final Player player) {
		final long millisLeft = FreezeGun.cooldown.get(player.getName()) - System.currentTimeMillis();
		final double value = millisLeft / 1000.0;
		final double sec = Math.round(10.0 * value) / 10.0;
		return sec;
	}

	public static double getCooldownInt(final Player player) {
		final long millisLeft = FreezeGun.cooldown.get(player.getName()) - System.currentTimeMillis();
		final int value = (int) (millisLeft / 1000.0);
		final int sec = (int) (Math.round(10.0 * value) / 10.0);
		return sec;
	}
	
	@SuppressWarnings("deprecation")
	public static void getFreezeGun(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(FreezeGun.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.FREEZE-GUN.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.FREEZE-GUN.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.FREEZE-GUN.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(FreezeGun.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(FreezeGun.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(FreezeGun.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        if (entity.getShooter() instanceof Player) {
            Projectile entity2 = event.getEntity();
            ProjectileSource shooter2 = entity2.getShooter();
            if (shooter2 == null) {
                return;
            }
            Player shooter = (Player)shooter2;
            int material = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.ITEM");
    		int data = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.DATA");
    		ItemStack item = new ItemStack(material, (short)data);
    		ItemMeta meta = item.getItemMeta();
    		List<String> lore = new ArrayList<String>();
    		meta.setDisplayName(CC.translate(FreezeGun.name));
    		lore = AbilitysFile.getConfig().getStringList("ABILITIES.FREEZE-GUN.LORE");
    		for (int i = 0; i < lore.size(); i++) {
    			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
    					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.COOLDOWN"))));
    		}
    		meta.setLore(lore);
    		if (AbilitysFile.getConfig().getBoolean("ABILITIES.FREEZE-GUN.GLOW")) {
    			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.FREEZE-GUN.ENCHANTMENT")) {
    				String enchantment = enchant.split(":")[0].toUpperCase();
    				Integer level = Integer.valueOf(enchant.split(":")[1]);
    				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
    			}
    		}
    		item.setItemMeta(meta);
            if (shooter.getItemInHand().isSimilar(item)) {
                Projectile entity3 = event.getEntity();
                entity3.setMetadata("freezegun", new FixedMetadataValue(HCFaction.getPlugin(), true));
            }
        }
    }
	
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack is = event.getItem();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if ((is == null) || (is.getType().equals(Material.AIR)) || (is.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (is.getItemMeta().getDisplayName().equals(FreezeGun.name) && is.getItemMeta().hasLore()) {
				event.setCancelled(true);
				if (FreezeGun.cooldown.containsKey(event.getPlayer().getName()) && FreezeGun.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
					long millisLeft = FreezeGun.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
					event.setCancelled(true);
					player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.FREEZE-GUN.COOLDOWN-MESSAGE"))
							.replace("<FREEZE-GUN>", FreezeGun.name)
							.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
					player.updateInventory();
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", FreezeGun.name));
					player.updateInventory();
	                return;
				}
            	if(FFACommand.mode == true) {
            		return;
            	}
				FreezeGun.cooldown.put(player.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.COOLDOWN") * 1000));
				Snowball freezeGun = (Snowball) player.launchProjectile(Snowball.class);
	            player.playSound(player.getLocation(), Sound.DIG_SNOW, 1F, 1F);
	            freezeGun.setFallDistance(100.0f);
	            return;
			}
        }
    }
	
	@EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Entity entity = event.getEntity();
            if (entity == null) {
                return;
            }
            Player damaged = (Player)entity;
            if (event.getDamager() instanceof Projectile) {
                Entity damager = event.getDamager();
                if (damager == null) {
                    return;
                }
                Projectile projectile = (Projectile)damager;
                if (projectile instanceof Snowball && projectile.hasMetadata("freezegun")) {
                    ProjectileSource shooter2 = ((Snowball)projectile).getShooter();
                    if (shooter2 == null) {
                        return;
                    }
                    Player shooter = (Player)shooter2;
    				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
    				Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(damaged.getLocation());
    				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
    					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
    							.replace("<ABILITY>", FreezeGun.name));
    					shooter.updateInventory();
    	                return;
    				}
    				if (factionAt2 instanceof SpawnFaction || factionAt2 instanceof CitadelFaction || factionAt2 instanceof ConquestFaction) {
    					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
    							.replace("<ABILITY>", FreezeGun.name));
    					FreezeGun.cooldown.remove(shooter.getName());
    					shooter.updateInventory();
    	                return;
    				}
                	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(entity.getUniqueId()) != null) {
                		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(shooter.getUniqueId());
                		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damaged.getUniqueId());
                		if(playerFaction != null && playerFaction.equals(victimFaction)) {
                			shooter.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                			return;
                		}
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) entity)) {
    					FreezeGun.cooldown.remove(shooter.getName());
                		return;
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(shooter)) {
    					FreezeGun.cooldown.remove(shooter.getName());
                		return;
                	}
                    this.giveEffect(damaged);
                    shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.FREEZE-GUN.PLAYER-MESSAGE"))
    						.replace("<FREEZE-GUN>", name)
    						.replace("<TARGET>", damaged.getName()));
                	 damaged.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.FREEZE-GUN.TARGET-MESSAGE"))
    						.replace("<FREEZE-GUN>", name)
    						.replace("<PLAYER>", shooter.getName()));
                	 return;
                }
            }
        }
    }
	
	private void giveEffect(Player damaged) {
		int duration = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.SLOWNESS.DURATION");
		int amplifier = AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.SLOWNESS.AMPLIFIER");
		damaged.removePotionEffect(PotionEffectType.SLOW);
		damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, amplifier - 1));
	}
}