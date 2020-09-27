package net.bfcode.bfhcf.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
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

public class SnowBall implements Listener {
	
	public static Map<String, Long> cooldown;
	public static String name;
  
	static {
		SnowBall.cooldown = new HashMap<String, Long>();
		SnowBall.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.NAME"));
	}

	public static Map<String, Long> getCooldown() {
		return SnowBall.cooldown;
	}

	public static boolean isOnCooldown(final Player player) {
		return SnowBall.cooldown.containsKey(player.getName())
				&& SnowBall.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = SnowBall.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getSnowball(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(SnowBall.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SNOWBALL.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(SnowBall.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(SnowBall.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(SnowBall.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}
	
	@SuppressWarnings("deprecation")
	public void refund(Player player) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.DATA");
		ItemStack item = new ItemStack(material, 1, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(SnowBall.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SNOWBALL.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		SnowBall.cooldown.remove(player.getName());
		player.getInventory().addItem(new ItemStack[] { item });
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
            int material = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.ITEM");
    		int data = AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.DATA");
    		ItemStack item = new ItemStack(material, (short)data);
    		ItemMeta meta = item.getItemMeta();
    		List<String> lore = new ArrayList<String>();
    		meta.setDisplayName(CC.translate(SnowBall.name));
    		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.LORE");
    		for (int i = 0; i < lore.size(); i++) {
    			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
    					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.COOLDOWN"))));
    		}
    		meta.setLore(lore);
    		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SNOWBALL.GLOW")) {
    			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SNOWBALL.ENCHANTMENT")) {
    				String enchantment = enchant.split(":")[0].toUpperCase();
    				Integer level = Integer.valueOf(enchant.split(":")[1]);
    				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
    			}
    		}
    		item.setItemMeta(meta);
            if (shooter.getItemInHand().isSimilar(item)) {
            	SnowBall.cooldown.put(shooter.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.COOLDOWN") * 1000));
                Projectile entity3 = event.getEntity();
                entity3.setMetadata("snowball", new FixedMetadataValue(HCFaction.getPlugin(), true));
            }
        }
    }

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player shooter = event.getPlayer();
		ItemStack is = event.getItem();
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if ((is == null) || (is.getType().equals(Material.AIR)) || (is.getItemMeta().getDisplayName() == null)) {
				return;
			}
			if (is.getItemMeta().getDisplayName().equals(SnowBall.name) && is.getItemMeta().hasLore()) {
				if (SnowBall.cooldown.containsKey(event.getPlayer().getName()) && SnowBall.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
					long millisLeft = SnowBall.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
					event.setCancelled(true);
					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.COOLDOWN-MESSAGE"))
							.replace("<SNOWBALL>", SnowBall.name)
							.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
					shooter.updateInventory();
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
				if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
					event.setCancelled(true);
					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", SnowBall.name));
					shooter.updateInventory();
	                return;
				}
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
                if (projectile instanceof Egg && projectile.hasMetadata("snowball")) {
                	ProjectileSource shooter2 = ((Egg)projectile).getShooter();
                	if (shooter2 == null) {
                			return;
                	}
                	Player shooter = (Player)shooter2;
                	Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
                	Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(damaged.getLocation());
                	if (factionAt instanceof SpawnFaction) {
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", SnowBall.name)
								.replace("<TARGET>", damaged.getName()));
                		refund(shooter);
	                    shooter.updateInventory();
	                    return;
    				}
                	if (factionAt2 instanceof SpawnFaction) {
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", SnowBall.name)
								.replace("<TARGET>", damaged.getName()));
	                    refund(shooter);
	                    shooter.updateInventory();
	                    return;
    				}
                	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(entity.getUniqueId()) != null) {
                		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(shooter.getUniqueId());
                		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damaged.getUniqueId());
                		if(playerFaction != null && playerFaction.equals(victimFaction)) {
                			shooter.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                			refund(shooter);
                			return;
                		}
                	}
                	shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.PLAYER-MESSAGE"))
                			.replace("<SNOWBALL>", name)
                			.replace("<TARGET>", damaged.getName()));
                	damaged.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.TARGET-MESSAGE"))
                			.replace("<SNOWBALL>", name)
                			.replace("<PLAYER>", shooter.getName()));
                	damaged.removePotionEffect(PotionEffectType.SLOW);
                	damaged.removePotionEffect(PotionEffectType.BLINDNESS);
                	damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
                	damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                	return;
               	}
                if (projectile instanceof Snowball && projectile.hasMetadata("snowball")) {
                    ProjectileSource shooter2 = ((Snowball)projectile).getShooter();
                    if (shooter2 == null) {
                        return;
                    }
                    Player shooter = (Player)shooter2;
                    Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damaged.getLocation());
                    Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
                    if (factionAt instanceof SpawnFaction) {
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", SnowBall.name)
								.replace("<TARGET>", damaged.getName()));
                		refund(shooter);
	                    shooter.updateInventory();
	                    return;
    				}
                	if (factionAt2 instanceof SpawnFaction) {
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", SnowBall.name)
								.replace("<TARGET>", damaged.getName()));
	                    refund(shooter);
	                    shooter.updateInventory();
	                    return;
    				}
                    if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(entity.getUniqueId()) != null && HCFaction.getPlugin().getFactionManager().getPlayerFaction(shooter.getUniqueId()) != null) {
                		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(shooter.getUniqueId());
                		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damaged.getUniqueId());
                		if(playerFaction.equals(victimFaction)) {
                			shooter.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                			refund(shooter);
                			return;
                		}
                	}
                    if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) entity)) {
            			refund(shooter);
            			return;
                    }
                    if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(shooter)) {
                    	refund(shooter);
                    	return;
                    }
                	if(FFACommand.mode == true) {
                		return;
                	}
                    shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.PLAYER-MESSAGE"))
    						.replace("<SNOWBALL>", name)
    						.replace("<TARGET>", damaged.getName()));
                	 damaged.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SNOWBALL.TARGET-MESSAGE"))
    						.replace("<SNOWBALL>", name)
    						.replace("<PLAYER>", shooter.getName()));
                	 damaged.removePotionEffect(PotionEffectType.SLOW);
                	 damaged.removePotionEffect(PotionEffectType.BLINDNESS);
                	 damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
                	 damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                	 return;
                }
            }
        }
    }
}