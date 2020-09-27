package net.bfcode.bfhcf.listener.fixes;

import java.util.Set;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.entity.HumanEntity;
import net.minecraft.server.v1_7_R4.ItemArmor;
import net.minecraft.server.v1_7_R4.ItemSword;
import net.minecraft.server.v1_7_R4.ItemTool;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.enchantments.Enchantment;

import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.server.v1_7_R4.EnumArmorMaterial;
import net.minecraft.server.v1_7_R4.EnumToolMaterial;
import org.bukkit.Material;
import com.google.common.collect.ImmutableMap;

import org.bukkit.event.Listener;

public class EnchantLimitListener implements Listener
{
    private ImmutableMap<Material, EnumToolMaterial> ITEM_TOOL_MAPPING;
    private ImmutableMap<Material, EnumArmorMaterial> ITEM_ARMOUR_MAPPING;
    
    public EnchantLimitListener() {
        this.ITEM_TOOL_MAPPING = (ImmutableMap<Material, EnumToolMaterial>)ImmutableMap.of(Material.IRON_INGOT, EnumToolMaterial.IRON, Material.GOLD_INGOT, EnumToolMaterial.GOLD, Material.DIAMOND, EnumToolMaterial.DIAMOND);
        this.ITEM_ARMOUR_MAPPING = (ImmutableMap<Material, EnumArmorMaterial>)ImmutableMap.of(Material.IRON_INGOT, EnumArmorMaterial.IRON, Material.GOLD_INGOT, EnumArmorMaterial.GOLD, Material.DIAMOND, EnumArmorMaterial.DIAMOND);
    }
    
    public int getMaxLevel(Enchantment enchant) {
        return ConfigurationService.ENCHANTMENT_LIMITS.getOrDefault(enchant, enchant.getMaxLevel());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent event) {
        Map<Enchantment, Integer> adding = (Map<Enchantment, Integer>)event.getEnchantsToAdd();
        Iterator<Map.Entry<Enchantment, Integer>> iterator = adding.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = iterator.next();
            Enchantment enchantment = entry.getKey();
            int maxLevel = this.getMaxLevel(enchantment);
            if (entry.getValue() > maxLevel) {
                if (maxLevel > 0) {
                    adding.put(enchantment, maxLevel);
                }
                else {
                    iterator.remove();
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            for (ItemStack drop : event.getDrops()) {
                this.validateIllegalEnchants(drop);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFishEvent(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if (caught instanceof Item) {
            this.validateIllegalEnchants(((Item)caught).getItemStack());
        }
    }
    
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    public void onPrepareAnvilRepair(PrepareAnvilRepairEvent event) {
//        ItemStack firstAssassinEffects = event.getInventory().getItem(0);
//        ItemStack second = event.getInventory().getItem(1);
//        if (firstAssassinEffects != null && firstAssassinEffects.getType() != Material.AIR && second != null && second.getType() != Material.AIR) {
//            Object firstItemObj = net.minecraft.server.v1_7_R4.Item.REGISTRY.a(firstAssassinEffects.getTypeId());
//            if (firstItemObj instanceof net.minecraft.server.v1_7_R4.Item) {
//                net.minecraft.server.v1_7_R4.Item nmsFirstItem = (net.minecraft.server.v1_7_R4.Item)firstItemObj;
//                if (nmsFirstItem instanceof ItemTool) {
//                    if (this.ITEM_TOOL_MAPPING.get(second.getType()) == ((ItemTool)nmsFirstItem).i()) {
//                        return;
//                    }
//                }
//                else if (nmsFirstItem instanceof ItemSword) {
//                    EnumToolMaterial comparison = (EnumToolMaterial)this.ITEM_TOOL_MAPPING.get(second.getType());
//                    if (comparison != null && comparison.e() == nmsFirstItem.c()) {
//                        return;
//                    }
//                }
//                else if (nmsFirstItem instanceof ItemArmor && this.ITEM_ARMOUR_MAPPING.get(second.getType()) == ((ItemArmor)nmsFirstItem).m_()) {
//                    return;
//                }
//            }
//        }
//        HumanEntity repairer = event.getRepairer();
//        if (repairer instanceof Player) {
//            this.validateIllegalEnchants(event.getResult());
//        }
//    }
    
    private boolean validateIllegalEnchants(ItemStack stack) {
        boolean updated = false;
        if (stack != null && stack.getType() != Material.AIR) {
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                Set<Map.Entry<Enchantment, Integer>> entries = enchantmentStorageMeta.getStoredEnchants().entrySet();
                for (Map.Entry<Enchantment, Integer> entry : entries) {
                    Enchantment enchantment = entry.getKey();
                    int maxLevel = this.getMaxLevel(enchantment);
                    if (entry.getValue() > maxLevel) {
                        updated = true;
                        if (maxLevel > 0) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, maxLevel, false);
                        }
                        else {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                    }
                }
                stack.setItemMeta(meta);
            }
            else {
                Set<Map.Entry<Enchantment, Integer>> entries2 = stack.getEnchantments().entrySet();
                for (Map.Entry<Enchantment, Integer> entry2 : entries2) {
                    Enchantment enchantment2 = entry2.getKey();
                    int maxLevel2 = this.getMaxLevel(enchantment2);
                    if (entry2.getValue() > maxLevel2) {
                        updated = true;
                        stack.removeEnchantment(enchantment2);
                        if (maxLevel2 <= 0) {
                            continue;
                        }
                        stack.addEnchantment(enchantment2, maxLevel2);
                    }
                }
            }
        }
        return updated;
    }
}
