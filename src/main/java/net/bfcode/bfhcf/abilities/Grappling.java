package net.bfcode.bfhcf.abilities;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.event.Listener;

public class Grappling implements Listener {

	public static String name;
	public static String soundString;
	public static Map<String, Long> cooldown;

	static {
		Grappling.cooldown = new HashMap<String, Long>();
		Grappling.name = CC.translate(AbilitysFile.getConfig().getString("ABILITIES.GRAPPLING.NAME"));
		Grappling.soundString = AbilitysFile.getConfig().getString("ABILITIES.GRAPPLING.SOUND").toUpperCase();
	}

	public static boolean isOnCooldown(final Player player) {
		return Grappling.cooldown.containsKey(player.getName())
				&& Grappling.cooldown.get(player.getName()) > System.currentTimeMillis();
	}

	public Grappling() {
		Grappling.cooldown = new HashMap<String, Long>();
	}

	public static String getCooldown(final Player player) {
		final long millisLeft = Grappling.cooldown.get(player.getName()) - System.currentTimeMillis();
		return Utils.formatLongMin(millisLeft);
	}

	public boolean isGrappleHook(final Player player) {
		return player.getItemInHand().getItemMeta().getDisplayName().equals(CC.translate(Grappling.name)) 
				&& player.getItemInHand().getItemMeta().hasLore();
	}

	public static Boolean hasCooldown(final Player player) {
		if (Grappling.cooldown.containsKey(player.getName())
				&& Grappling.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			return true;
		}
		return true;
	}

	public static double getSeconds(final Player player) {
		if (Grappling.cooldown.containsKey(player.getName())
				&& Grappling.cooldown.get(player.getName()) > System.currentTimeMillis()) {
			final long diff = Grappling.cooldown.get(player.getName()) - System.currentTimeMillis();
			final double value = diff / 1000.0;
			final double sec = Math.round(10.0 * value) / 10.0;
			if (diff >= 0L) {
				return sec;
			}
		}
		return -1.0;
	}
	
	public static void getGrappling(CommandSender sender, Player target, int amount) {
		ItemStack item = new ItemStack(Material.FISHING_ROD, amount);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		meta.setDisplayName(CC.translate(Grappling.name));
		lore = AbilitysFile.getConfig().getStringList("ABILITIES.GRAPPLING.LORE");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, CC.translate(lore.get(i)).replace("<COOLDOWN>",
					String.valueOf(AbilitysFile.getConfig().getInt("ABILITIES.GRAPPLING.COOLDOWN"))));
		}
		meta.setLore(lore);
		if (AbilitysFile.getConfig().getBoolean("ABILITIES.GRAPPLING.GLOW")) {
			for (String enchant : AbilitysFile.getConfig().getStringList("ABILITIES.GRAPPLING.ENCHANTMENT")) {
				String enchantment = enchant.split(":")[0].toUpperCase();
				Integer level = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(Enchantment.getByName(enchantment), level, true);
			}
		}
		item.setItemMeta(meta);
		target.getInventory().addItem(new ItemStack[] { item });
		if (target == sender) {
			target.sendMessage(CC.translate(Grappling.name + " &ahas been added to your inventory."));
		} else {
			target.sendMessage(CC.translate(Grappling.name + " &ahas been added to your inventory."));
			sender.sendMessage(CC.translate(Grappling.name + " &ahas been added to '" + target.getName() + "' inventory."));
		}
		return;
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
			if (player.getItemInHand().getType().equals(Material.FISHING_ROD)) {
				if (player.getItemInHand().getItemMeta().getDisplayName() == null) {
					return;
				}
				if (player.getItemInHand().getItemMeta().getDisplayName().equals(Grappling.name) && player.getItemInHand().getItemMeta().hasLore()) {
					if (Grappling.cooldown.containsKey(event.getPlayer().getName()) && Grappling.cooldown.get(player.getName()) > System.currentTimeMillis()) {
						long millisLeft = Grappling.cooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();
						event.setCancelled(true);
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("ABILITIES.GRAPPLING.COOLDOWN-MESSAGE"))
								.replace("<GRAPPLING>", Grappling.name)
								.replace("<SECONDS>", Utils.formatLongMin(millisLeft)));
						player.updateInventory();
						return;
					}
					Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation());
					if (factionAt instanceof SpawnFaction || factionAt instanceof CitadelFaction || factionAt instanceof ConquestFaction) {
						event.setCancelled(true);
						player.sendMessage(CC.translate(AbilitysFile.getConfig().getString("PLAYER-SPAWN-MESSAGE"))
								.replace("<ABILITY>", Grappling.name));
						player.updateInventory();
		                return;
					}
	            	if(FFACommand.mode == true) {
	            		return;
	            	}
				}
			}
		}
	}

	@EventHandler
	public void grapple(final PlayerFishEvent event) {
		try {
			final Player p = event.getPlayer();
			final Location h = event.getHook().getLocation();
			final Location h2 = new Location(h.getWorld(), h.getX(), h.getY() + 1.0, h.getZ());
			h.getBlock().getLocation().setY(h.getBlock().getLocation().getY() - 1.0);
			if (p != null && this.isGrappleHook(p) && (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT
					|| event.getState() == PlayerFishEvent.State.IN_GROUND)) {
				final Location pl = p.getLocation();
				final int x1 = pl.getBlockX();
				final int y1 = pl.getBlockZ();
				final Location loc = event.getHook().getLocation();
				final int x2 = loc.getBlockX();
				final int y2 = loc.getBlockZ();
				loc.setY(loc.getY() - 1.0);
				if ((x1 != x2 && y1 != y2 && loc.getBlock().getType() != Material.AIR
						&& loc.getBlock().getType() != Material.STATIONARY_WATER)
						|| h2.getBlock().getType() != Material.AIR
						|| event.getState() == PlayerFishEvent.State.IN_GROUND) {
	            	if(FFACommand.mode == true) {
	            		return;
	            	}
					final double kyori = loc.distance(pl);
					final double y3 = loc.getY();
					loc.setY(y3 + 1.0);
					final Vector vec = pl.toVector();
					final Vector vec2 = loc.toVector();
					p.setVelocity(vec2.subtract(vec).normalize().multiply(kyori / 4.5));
					Sound sound = Sound.valueOf(Sound.class, soundString);
					p.playSound(p.getLocation(), sound, 10F, 2F);
					ItemStack item = p.getInventory().getItemInHand();
					int durability = item.getDurability();
					int newdurability = durability + 1;
					item.setDurability((short) newdurability);
					Grappling.cooldown.put(p.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.GRAPPLING.COOLDOWN") * 1000));
				} else {
					if (hasCooldown(p)) {
						Grappling.cooldown.remove(p.getName());
					}
				}
			}
		} catch (NullPointerException ex) {
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		try {
			if (event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				if (p.getItemInHand().getType() != Material.FISHING_ROD || !this.isGrappleHook(p)
						|| event.getCause() != EntityDamageEvent.DamageCause.FALL) {
					return;
				}
				event.setDamage(event.getDamage() / 3.5);
			}
		} catch (NullPointerException ex) {
		}
	}

	public void pullPlayerSlightly(final Player p, final Location loc) {
		if (loc.getY() > p.getLocation().getY()) {
			p.setVelocity(new Vector(0.0, 0.25, 0.0));
			return;
		}
		final Location playerLoc = p.getLocation();
		final Vector vector = loc.toVector().subtract(playerLoc.toVector());
		p.setVelocity(vector);
	}

	public void pullEntityToLocation(final Player e, final Location loc) {
		final Location entityLoc = e.getLocation();
		entityLoc.setY(entityLoc.getY() + 0.1);
		e.teleport(entityLoc);
		final double t = loc.distance(entityLoc);
		final double v_x = (1.0 + 0.1 * t) * (loc.getX() - entityLoc.getX()) / t;
		final double v_y = (1.0 + 0.03 * t) * (loc.getY() - entityLoc.getY()) / t - -0.04 * t;
		final double v_z = (1.0 + 0.1 * t) * (loc.getZ() - entityLoc.getZ()) / t;
		final Vector v = e.getVelocity();
		v.setX(v_x);
		v.setY(v_y);
		v.setZ(v_z);
		e.setVelocity(v);
	}
}