package net.bfcode.bfhcf.abilities;

import org.bukkit.util.Vector;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

import org.bukkit.event.Listener;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Switcher implements Listener {

	public static Map<String, Long> cooldown;
	public static String name;
	public static String soundString;

	static {
		Switcher.cooldown = new HashMap<String, Long>();
		Switcher.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.NAME"));
		Switcher.soundString = AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.SOUND").toUpperCase();
	}

	public static Map<String, Long> getCooldown() {
		return Switcher.cooldown;
	}

	public static boolean isOnCooldown(final Player player) {
		return Switcher.cooldown.containsKey(player.getName())
				&& Switcher.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = Switcher.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}
	
	@SuppressWarnings("deprecation")
	public static void getSwitcher(CommandSender sender, Player target, int amount) {
		int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Switcher.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Switcher.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Switcher.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(Switcher.name + " &ahas been added to '" + target.getName() + "' inventory."));
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
            int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
    		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
    		ItemStack item = new ItemStack(material, (short)data);
    		ItemMeta meta = item.getItemMeta();
    		List<String> lore = new ArrayList<String>();
    		meta.setDisplayName(CC.translate(Switcher.name));
    		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
    		for (int i = 0; i < lore.size(); i++) {
    			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
    					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
    		}
    		meta.setLore(lore);
    		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
    			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
    				String enchantment = enchant.split(":")[0].toUpperCase();
    				Integer level = Integer.valueOf(enchant.split(":")[1]);
    				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
    			}
    		}
    		item.setItemMeta(meta);
            if (shooter.getItemInHand().isSimilar(item)) {
            	Switcher.cooldown.put(shooter.getName(), System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN") * 1000));
                Projectile entity3 = event.getEntity();
                entity3.setMetadata("switcher", new FixedMetadataValue(HCFaction.getPlugin(), true));
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
			if (is.getItemMeta().getDisplayName().equals(Switcher.name) && is.getItemMeta().hasLore()) {
				if (Switcher.cooldown.containsKey(event.getPlayer().getName()) && Switcher.cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
					long millisLeft = Switcher.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
					event.setCancelled(true);
					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.COOLDOWN-MESSAGE"))
							.replace("<SWITCHER>", Switcher.name)
							.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
					shooter.updateInventory();
					return;
				}
				Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
				if (factionAt instanceof SpawnFaction || 
					factionAt instanceof KothFaction || 
					factionAt instanceof ConquestFaction || 
					factionAt instanceof CitadelFaction) {
					event.setCancelled(true);
					shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
							.replace("<ABILITY>", Switcher.name));
					shooter.updateInventory();
	                return;
				}
            	if(FFACommand.mode == true) {
            		return;
            	}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
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
                if (projectile instanceof Egg && projectile.hasMetadata("switcher")) {
                    ProjectileSource shooter2 = ((Egg)projectile).getShooter();
                    if (shooter2 == null) {
                        return;
                    }
                    Player shooter = (Player)shooter2;
                    Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damaged.getLocation());
                    Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
                    if (factionAt instanceof SpawnFaction || 
        					factionAt instanceof KothFaction || 
        					factionAt instanceof ConquestFaction || 
        					factionAt instanceof CitadelFaction) {
                		int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
                		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
                		ItemStack item = new ItemStack(material, 1, (short)data);
                		ItemMeta meta = item.getItemMeta();
                		List<String> lore = new ArrayList<String>();
                		meta.setDisplayName(CC.translate(Switcher.name));
                		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
                		for (int i = 0; i < lore.size(); i++) {
                			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
                					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
                		}
                		meta.setLore(lore);
                		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
                			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
                				String enchantment = enchant.split(":")[0].toUpperCase();
                				Integer level = Integer.valueOf(enchant.split(":")[1]);
                				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
                			}
                		}
                		item.setItemMeta(meta);
                		shooter.getInventory().addItem(new ItemStack[] { item });
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", Switcher.name)
								.replace("<TARGET>", damaged.getName()));
                		Switcher.cooldown.remove(shooter.getName());
	                    shooter.updateInventory();
	                    return;
    				}
                    if (factionAt2 instanceof SpawnFaction || 
        					factionAt2 instanceof KothFaction || 
        					factionAt2 instanceof ConquestFaction || 
        					factionAt2 instanceof CitadelFaction) {
                		int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
                		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
                		ItemStack item = new ItemStack(material, 1, (short)data);
                		ItemMeta meta = item.getItemMeta();
                		List<String> lore = new ArrayList<String>();
                		meta.setDisplayName(CC.translate(Switcher.name));
                		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
                		for (int i = 0; i < lore.size(); i++) {
                			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
                					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
                		}
                		meta.setLore(lore);
                		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
                			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
                				String enchantment = enchant.split(":")[0].toUpperCase();
                				Integer level = Integer.valueOf(enchant.split(":")[1]);
                				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
                			}
                		}
                		item.setItemMeta(meta);
                		shooter.getInventory().addItem(new ItemStack[] { item });
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", Switcher.name)
								.replace("<TARGET>", damaged.getName()));
                		Switcher.cooldown.remove(shooter.getName());
	                    shooter.updateInventory();
	                    return;
    				}
                	if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(entity.getUniqueId()) != null) {
                		PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(shooter.getUniqueId());
                		PlayerFaction victimFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(damaged.getUniqueId());
                		if(playerFaction.equals(victimFaction)) {
                			shooter.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                			int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
                			int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
                			ItemStack item = new ItemStack(material, 1, (short)data);
                			ItemMeta meta = item.getItemMeta();
                			List<String> lore = new ArrayList<String>();
                			meta.setDisplayName(CC.translate(Switcher.name));
                			lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
                			for (int i = 0; i < lore.size(); i++) {
                				lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
                						String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
                			}
                			meta.setLore(lore);
                			if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
                				for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
                					String enchantment = enchant.split(":")[0].toUpperCase();
                					Integer level = Integer.valueOf(enchant.split(":")[1]);
                					meta.addEnchant(Enchantment.getByName(enchantment), level, true);
                				}
                			}
                			item.setItemMeta(meta);
                			Switcher.cooldown.remove(shooter.getName());
                			shooter.getInventory().addItem(new ItemStack[] { item });
                			return;
                		}
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) entity)) {
            			int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
            			int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
            			ItemStack item = new ItemStack(material, 1, (short)data);
            			ItemMeta meta = item.getItemMeta();
            			List<String> lore = new ArrayList<String>();
            			meta.setDisplayName(CC.translate(Switcher.name));
            			lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
            			for (int i = 0; i < lore.size(); i++) {
            				lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
            						String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
            			}
            			meta.setLore(lore);
            			if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
            				for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
            					String enchantment = enchant.split(":")[0].toUpperCase();
            					Integer level = Integer.valueOf(enchant.split(":")[1]);
            					meta.addEnchant(Enchantment.getByName(enchantment), level, true);
            				}
            			}
            			item.setItemMeta(meta);
            			Switcher.cooldown.remove(shooter.getName());
            			shooter.getInventory().addItem(new ItemStack[] { item });
            			return;
                	}
                	if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable()!= null && !HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled(shooter)) {
            			int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
            			int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
            			ItemStack item = new ItemStack(material, 1, (short)data);
            			ItemMeta meta = item.getItemMeta();
            			List<String> lore = new ArrayList<String>();
            			meta.setDisplayName(CC.translate(Switcher.name));
            			lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
            			for (int i = 0; i < lore.size(); i++) {
            				lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
            						String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
            			}
            			meta.setLore(lore);
            			if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
            				for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
            					String enchantment = enchant.split(":")[0].toUpperCase();
            					Integer level = Integer.valueOf(enchant.split(":")[1]);
            					meta.addEnchant(Enchantment.getByName(enchantment), level, true);
            				}
            			}
            			item.setItemMeta(meta);
            			Switcher.cooldown.remove(shooter.getName());
            			shooter.getInventory().addItem(new ItemStack[] { item });
            			return;
                	}
                    Location playerLoc = shooter.getLocation().clone();
    				Location entityLoc = damaged.getLocation().clone();
    				Vector playerLook = playerLoc.getDirection();
    				Vector playerVec = playerLoc.toVector();
    				Vector entityVec = entityLoc.toVector();
    				Vector toVec = playerVec.subtract(entityVec).normalize();
    				damaged.teleport(playerLoc.setDirection(playerLook.normalize()));
    				shooter.teleport(entityLoc.setDirection(toVec));
    				shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.PLAYER-MESSAGE"))
    						.replace("<SWITCHER>", Switcher.name)
    						.replace("<TARGET>", damaged.getName()));
    				damaged.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.TARGET-MESSAGE"))
    						.replace("<SWITCHER>", Switcher.name)
    						.replace("<PLAYER>", shooter.getName()));
    				Sound sound = Sound.valueOf(Sound.class, soundString);
    				damaged.playSound(entity.getLocation(), sound, 1F, 1F);
    				shooter.playSound(shooter.getLocation(), sound, 1F, 1F);
    				return;
                }
                if (projectile instanceof Snowball && projectile.hasMetadata("switcher")) {
                    ProjectileSource shooter2 = ((Snowball)projectile).getShooter();
                    if (shooter2 == null) {
                        return;
                    }
                    Player shooter = (Player)shooter2;
                    Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(damaged.getLocation());
                    Faction factionAt2 = HCFaction.getPlugin().getFactionManager().getFactionAt(shooter.getLocation());
                    if (factionAt instanceof SpawnFaction || 
        					factionAt instanceof KothFaction || 
        					factionAt instanceof ConquestFaction || 
        					factionAt instanceof CitadelFaction) {
                		int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
                		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
                		ItemStack item = new ItemStack(material, 1, (short)data);
                		ItemMeta meta = item.getItemMeta();
                		List<String> lore = new ArrayList<String>();
                		meta.setDisplayName(CC.translate(Switcher.name));
                		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
                		for (int i = 0; i < lore.size(); i++) {
                			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
                					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
                		}
                		meta.setLore(lore);
                		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
                			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
                				String enchantment = enchant.split(":")[0].toUpperCase();
                				Integer level = Integer.valueOf(enchant.split(":")[1]);
                				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
                			}
                		}
                		item.setItemMeta(meta);
                		shooter.getInventory().addItem(new ItemStack[] { item });
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", Switcher.name)
								.replace("<TARGET>", damaged.getName()));
	                    shooter.updateInventory();
	                    return;
    				}
                    if (factionAt2 instanceof SpawnFaction || 
        					factionAt2 instanceof KothFaction || 
        					factionAt2 instanceof ConquestFaction || 
        					factionAt2 instanceof CitadelFaction) {
                		int material = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.ITEM");
                		int data = AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.DATA");
                		ItemStack item = new ItemStack(material, 1, (short)data);
                		ItemMeta meta = item.getItemMeta();
                		List<String> lore = new ArrayList<String>();
                		meta.setDisplayName(CC.translate(Switcher.name));
                		lore = AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.LORE");
                		for (int i = 0; i < lore.size(); i++) {
                			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
                					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN"))));
                		}
                		meta.setLore(lore);
                		if (AbilitysFile.getConfig().getBoolean("ABILITIES.SWITCHER.GLOW")) {
                			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.SWITCHER.ENCHANTMENT")) {
                				String enchantment = enchant.split(":")[0].toUpperCase();
                				Integer level = Integer.valueOf(enchant.split(":")[1]);
                				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
                			}
                		}
                		item.setItemMeta(meta);
                		shooter.getInventory().addItem(new ItemStack[] { item });
                		shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("TARGET-SPAWN-MESSAGE"))
	                			.replace("<ABILITY>", Switcher.name)
								.replace("<TARGET>", damaged.getName()));
                		Switcher.cooldown.remove(shooter.getName());
	                    shooter.updateInventory();
	                    return;
    				}
                    Location playerLoc = shooter.getLocation().clone();
    				Location entityLoc = damaged.getLocation().clone();
    				Vector playerLook = playerLoc.getDirection();
    				Vector playerVec = playerLoc.toVector();
    				Vector entityVec = entityLoc.toVector();
    				Vector toVec = playerVec.subtract(entityVec).normalize();
    				damaged.teleport(playerLoc.setDirection(playerLook.normalize()));
    				shooter.teleport(entityLoc.setDirection(toVec));
    				shooter.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.PLAYER-MESSAGE"))
    						.replace("<SWITCHER>", Switcher.name)
    						.replace("<TARGET>", damaged.getName()));
    				damaged.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.SWITCHER.TARGET-MESSAGE"))
    						.replace("<SWITCHER>", Switcher.name)
    						.replace("<PLAYER>", shooter.getName()));
    				Sound sound = Sound.valueOf(Sound.class, soundString);
    				damaged.playSound(entity.getLocation(), sound, 1F, 1F);
    				shooter.playSound(shooter.getLocation(), sound, 1F, 1F);
    				return;
                }
            }
        }
    }
	
	@EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
		if (AbilitysFile.getConfig().getBoolean("EGG-SPAWN-CHICKEN")) {
			if (event.getSpawnReason() == SpawnReason.EGG) {
	            event.setCancelled(false);
	        }
		} else {
			if (event.getSpawnReason() == SpawnReason.EGG) {
	            event.setCancelled(true);
	        }
		}
    }
}
