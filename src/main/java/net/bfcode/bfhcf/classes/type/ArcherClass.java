package net.bfcode.bfhcf.classes.type;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.event.block.Action;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.utils.Cooldowns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import java.util.concurrent.TimeUnit;

import org.bukkit.potion.PotionEffect;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class ArcherClass extends PvpClass implements Listener
{
    public static HashMap<UUID, UUID> tagged;
    @SuppressWarnings("unused")
	private static HashMap<UUID, Long> ARCHER_COOLDOWN;
    @SuppressWarnings("unused")
	private static PotionEffect ARCHER_CRITICAL_EFFECT;
    @SuppressWarnings("unused")
	private static PotionEffect ARCHER_SPEED_EFFECT;
    @SuppressWarnings("unused")
	private static long ARCHER_SPEED_COOLDOWN_DELAY;
    @SuppressWarnings("unused")
	private static int MARK_TIMEOUT_SECONDS = 10;
    @SuppressWarnings("unused")
	private static int MARK_EXECUTION_LEVEL = 3;
    @SuppressWarnings("unused")
	private static float MINIMUM_FORCE = 0.5f;
    @SuppressWarnings("unused")
	private static String ARROW_FORCE_METADATA = "ARROW_FORCE";
    @SuppressWarnings("unused")
	private static PotionEffect ARCHER_JUMP_EFFECT;
    @SuppressWarnings("unused")
	private static long ARCHER_JUMP_COOLDOWN_DELAY;
    private HCFaction plugin;
    
    public ArcherClass(HCFaction plugin) {
        super("Archer", TimeUnit.SECONDS.toMillis(3L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityShootBow(EntityShootBowEvent event) {
        Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow) {
            projectile.setMetadata("ARROW_FORCE", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)event.getForce()));
        }
    }
    
    @EventHandler
    public void onPlayerClickSugar(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (this.plugin.getPvpClassManager().getEquippedClass(p) != null && this.plugin.getPvpClassManager().getEquippedClass(p).equals(this) && p.getItemInHand().getType() == Material.SUGAR) {
            if (Cooldowns.isOnCooldown("ARCHER_ITEM_COOLDOWN", p)) {
                p.sendMessage(ChatColor.RED + "You cannot use this for another §l" + Cooldowns.getCooldownForPlayerInt("ARCHER_ITEM_COOLDOWN", p) + ChatColor.RED.toString() + " seconds.");
                e.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("ARCHER_ITEM_COOLDOWN", p, 25);
            p.sendMessage(ChatColor.RED + "Speed 4 now activated.");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 3));
            new BukkitRunnable() {
                public void run() {
                    if (ArcherClass.this.isApplicableFor(p)) {
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    }
                }
            }.runTaskLater(this.plugin, 120L);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (ArcherClass.tagged.containsKey(e.getPlayer().getUniqueId())) {
            ArcherClass.tagged.remove(e.getPlayer().getUniqueId());
        }
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            Arrow arrow = (Arrow)damager;
			ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                Player damaged = (Player)event.getEntity();
                Player shooter = (Player)source;
                PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }
                if (this.plugin.getTimerManager().archerTimer.getRemaining((Player)entity) == 0L) {
                    if (this.plugin.getPvpClassManager().getEquippedClass(damaged) != null && this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this)) {
                        return;
                    }
                    this.plugin.getTimerManager().archerTimer.setCooldown((Player)entity, entity.getUniqueId());
                    ArcherClass.tagged.put(damaged.getUniqueId(), shooter.getUniqueId());
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        HCFaction.getPlugin().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).init(damaged);
                    }
                    shooter.sendMessage(ChatColor.GOLD + "You have hit a player (" + ChatColor.GRAY + damaged.getName() + ChatColor.GOLD + ")");
                    damaged.sendMessage(ChatColor.GOLD + "§c§lMarked! §eAn archer has hit you and §dArcher Tagged§e you. §7(Taken damage will be increased by +25% for 10 seconds.)");
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 1));
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onArcherJumpClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.FEATHER) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            if (Cooldowns.isOnCooldown("ARCHER_JUMP_COOLDOWN", p)) {
                p.sendMessage(ChatColor.RED + "You cannot use this for another §l" + Cooldowns.getCooldownForPlayerInt("ARCHER_JUMP_COOLDOWN", p) + ChatColor.RED.toString() + " seconds.");
                event.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("ARCHER_JUMP_COOLDOWN", p, 25);
            p.sendMessage(ChatColor.RED.toString() + "§cArcher Jump boost enabled.");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.removePotionEffect(PotionEffectType.JUMP);
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 6));
            new BukkitRunnable() {
                public void run() {
                    if (ArcherClass.this.isApplicableFor(p)) {
                        p.removePotionEffect(PotionEffectType.JUMP);
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 120L);
        }
    }
    
    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.LEATHER_BOOTS;
    }
    
    static {
        tagged = new HashMap<UUID, UUID>();
        ARCHER_COOLDOWN = new HashMap<UUID, Long>();
        ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.POISON, 60, 0);
        ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
        ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
        ArcherClass.ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 3);
        ArcherClass.ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    }
}
