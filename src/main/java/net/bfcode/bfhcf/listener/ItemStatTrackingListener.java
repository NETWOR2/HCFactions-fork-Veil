package net.bfcode.bfhcf.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class ItemStatTrackingListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        ItemStack stack;
        if (killer != null && (stack = killer.getItemInHand()) != null && EnchantmentTarget.WEAPON.includes(stack)) {
            this.addDeathLore(stack, player, killer);
        }
    }
    
    private CraftEntity getKiller(PlayerDeathEvent event) {
        EntityLiving lastAttacker = ((CraftPlayer)event.getEntity()).getHandle().aX();
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }
    
    private String getEntityName(Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        return (entity instanceof Player) ? ((Player)entity).getName() : ((CraftEntity)entity).getHandle().getName();
    }
    
    private String getDisplayName(Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return player.getName();
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
    
    private void addDeathLore(ItemStack stack, Player player, Player killer) {
        ItemMeta meta = stack.getItemMeta();
        List list;
        List lore = list = (meta.hasLore() ? meta.getLore() : new ArrayList(2));
        if (lore.isEmpty() || lore.size() <= 1 || !((String) lore.get(1)).startsWith(ChatColor.GOLD + "Kills ")) {
            lore.add(0, ChatColor.WHITE + "");
            lore.add(1, ChatColor.GOLD + "Kills " + ChatColor.WHITE + 1);
            lore.add(2, ChatColor.WHITE + "");
            lore.add(3, ChatColor.YELLOW + this.getEntityName((Entity)killer) + ChatColor.WHITE + " killed " + ChatColor.YELLOW + this.getDisplayName((Entity)player) + ChatColor.WHITE + ".");
        }
        else {
            String killsString = ((String) lore.get(1)).replace(ChatColor.GOLD + "Kills ", "").replace(ChatColor.WHITE + "]", "");
            Integer kills = 1;
            try {
                kills = Integer.parseInt(ChatColor.stripColor(killsString));
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
            Integer killafteradd = kills + 1;
            lore.set(1, ChatColor.GOLD + "Kills " + ChatColor.WHITE + killafteradd);
            lore.add(ChatColor.YELLOW + this.getDisplayName((Entity)killer) + ChatColor.WHITE + " killed " + ChatColor.YELLOW + this.getDisplayName((Entity)player) + ChatColor.WHITE + ".");
        }
        meta.setLore((List)lore.subList(0, Math.min(6, lore.size())));
        stack.setItemMeta(meta);
    }
}
