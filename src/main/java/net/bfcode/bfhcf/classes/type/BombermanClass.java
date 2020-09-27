package net.bfcode.bfhcf.classes.type;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.classes.event.PvpClassUnequipEvent;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class BombermanClass extends PvpClass implements Listener {
	private HCFaction plugin;
	public HashMap<String, Integer> firstAssassinEffects;
	private static long BOMBERMAN_TNT_COOLDOWN_DELAY;
	private TObjectLongMap<UUID> bombermanTNTCooldowns;
	private static double throwForce = 1.5D;
	private static int fuse = 30;
  
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BombermanClass(HCFaction plugin) {
		super("Bomberman", TimeUnit.SECONDS.toMillis(5L));
		this.firstAssassinEffects = new HashMap<String, Integer>();
		this.bombermanTNTCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	}
  
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler
	public void onUnEquip(PvpClassUnequipEvent event) {
		Player player = event.getPlayer();
		this.firstAssassinEffects.remove(player);
	}
  
	public boolean onEquip(Player player) {
		return super.onEquip(player);
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.STICK) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            long timestamp = this.bombermanTNTCooldowns.get(uuid);
            long millis = System.currentTimeMillis();
            long remaining = (timestamp == this.bombermanTNTCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
            if (remaining > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot use this for another " + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
                return;
            }
            if (HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
            	player.sendMessage(CC.translate("&cTNT Launch are disabled in safe-zones!!"));
    			return;
    		}
            shootTnt(player, throwForce, fuse);
            this.bombermanTNTCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + BombermanClass.BOMBERMAN_TNT_COOLDOWN_DELAY);
            return;
        }
    }
  
	public boolean isApplicableFor(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		ItemStack helmet = playerInventory.getHelmet();
		if ((helmet == null) || (helmet.getType() != Material.GOLD_HELMET)) {
			return false;
		}
		ItemStack chestplate = playerInventory.getChestplate();
		if ((chestplate == null) || (chestplate.getType() != Material.DIAMOND_CHESTPLATE)) {
			return false;
		}
		ItemStack leggings = playerInventory.getLeggings();
		if ((leggings == null) || (leggings.getType() != Material.DIAMOND_LEGGINGS)) {
			return false;
		}
		ItemStack boots = playerInventory.getBoots();
		return (boots != null) && (boots.getType() == Material.GOLD_BOOTS);
	}
	
	public static void shootTnt(Player player, double throwForce, int fuseTicks) {
		TNTPrimed tnt = (TNTPrimed)player.getWorld().spawnEntity(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 2.0D, player.getLocation().getZ()), EntityType.PRIMED_TNT);
		tnt.setVelocity(player.getLocation().getDirection().multiply(throwForce));
		tnt.setFuseTicks(fuseTicks);
	}
	
	static {
		BOMBERMAN_TNT_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(10L);
    }
}