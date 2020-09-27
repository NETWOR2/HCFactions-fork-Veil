package net.bfcode.bfhcf.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.HCFaction;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class DeathMessageListener implements Listener
{
    private HCFaction plugin;
    
    public DeathMessageListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        if (message == null || message.isEmpty()) {
            return;
        }
		if (this.plugin.getTournamentManager().isInTournament(event.getEntity())) {
			event.setDeathMessage(null);
            return;
        }
        EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CUSTOM;
        if (event.getEntity().getLastDamageCause() != null) {
            cause = event.getEntity().getLastDamageCause().getCause();
        }
        boolean isLogger = false;
        if (event.getDeathMessage().contains("Combat-Logger")) {
            isLogger = true;
        }
        event.setDeathMessage(this.getDeathMessage(event.getEntity(), this.getKiller(event), cause, isLogger));
    }
    
    public String toReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "";
        }
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                return ChatColor.YELLOW + " using " + ChatColor.RED + meta.getDisplayName() + ChatColor.YELLOW + ".";
            }
        }
        return ChatColor.YELLOW + " using " + ChatColor.RED + this.toReadable((Enum)item.getType()) + ChatColor.YELLOW + ".";
    }
    
    public String toReadable(Enum enu) {
        return WordUtils.capitalize(enu.name().replace("_", " ").toLowerCase());
    }
    
    private CraftEntity getKiller(PlayerDeathEvent event) {
        EntityLiving lastAttacker = ((CraftPlayer)event.getEntity()).getHandle().aX();
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }
    
    private String getDeathMessage(Player player, Entity killer, EntityDamageEvent.DamageCause cause, boolean isLogger) {
        String input = "";
        if (killer instanceof Player) {
            ItemStack item = ((Player)killer).getItemInHand();
            if (item != null && item.getType() == Material.BOW) {
                input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " was shot by " + ChatColor.RED + this.getName(killer);
                input = input + ChatColor.YELLOW + " from " + ChatColor.LIGHT_PURPLE + (int)player.getLocation().distance(killer.getLocation()) + ChatColor.LIGHT_PURPLE + " blocks" + ChatColor.YELLOW + ".";
            }
            else {
                input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " was slain by " + ChatColor.RED + this.getName(killer);
                input += this.toReadable(item);
            }
        }
        else if (cause == EntityDamageEvent.DamageCause.FALL && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " fell from a high place.";
        }
        else if (cause == EntityDamageEvent.DamageCause.FIRE && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died to fire.";
        }
        else if (cause == EntityDamageEvent.DamageCause.LIGHTNING && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died to lightning.";
        }
        else if (cause == EntityDamageEvent.DamageCause.WITHER && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " withered away.";
        }
        else if (cause == EntityDamageEvent.DamageCause.DROWNING && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " drowned.";
        }
        else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died to a falling block.";
        }
        else if (cause == EntityDamageEvent.DamageCause.MAGIC && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died to magic.";
        }
        else if (cause == EntityDamageEvent.DamageCause.VOID && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " fell into the void.";
        }
        else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died to an explosion.";
        }
        else if (cause == EntityDamageEvent.DamageCause.LAVA && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " burnt to a crisp.";
        }
        else if (cause == EntityDamageEvent.DamageCause.STARVATION && !(this.plugin.getTournamentManager().isInTournament(player))) {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " starved to death.";
        }
        else {
            input = ChatColor.RED + this.getName(player) + ChatColor.YELLOW + " died.";
        }
        return input;
    }
    
    private String getName(Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return ChatColor.RED + player.getName() + ChatColor.GOLD + '[' + ChatColor.WHITE + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GOLD + ']';
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
}
