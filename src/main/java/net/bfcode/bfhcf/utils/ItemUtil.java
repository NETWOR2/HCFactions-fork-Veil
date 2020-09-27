package net.bfcode.bfhcf.utils;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import java.util.*;
import org.bukkit.enchantments.*;
import java.beans.*;

public class ItemUtil
{
    private ItemUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static String formatMaterial(Material material) {
        String name = material.toString();
        name = name.replace('_', ' ');
        String result = "" + name.charAt(0);
        for (int i = 1; i < name.length(); ++i) {
            if (name.charAt(i - 1) == ' ') {
                result += name.charAt(i);
            }
            else {
                result += Character.toLowerCase(name.charAt(i));
            }
        }
        return result;
    }
    
    public static ItemStack enchantItem(ItemStack itemStack, ItemEnchant... enchantments) {
        Arrays.asList(enchantments).forEach(enchantment -> itemStack.addUnsafeEnchantment(enchantment.enchantment, enchantment.level));
        return itemStack;
    }
    
    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(Material material, String name, int amount, short damage) {
        ItemStack item = new ItemStack(material, amount, damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack renameItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack reloreItem(ItemStack item, String... lores) {
        return reloreItem(ReloreType.OVERWRITE, item, lores);
    }
    
    public static ItemStack reloreItem(ReloreType type, ItemStack item, String... lores) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = (List<String>)meta.getLore();
        if (lore == null) {
            lore = new LinkedList<String>();
        }
        switch (type) {
            case APPEND: {
                lore.addAll(Arrays.asList(lores));
                meta.setLore((List)lore);
                break;
            }
            case PREPEND: {
                List<String> nLore = new LinkedList<String>(Arrays.asList(lores));
                nLore.addAll(lore);
                meta.setLore((List)nLore);
                break;
            }
            case OVERWRITE: {
                meta.setLore((List)Arrays.asList(lores));
                break;
            }
        }
        item.setItemMeta(meta);
        return item;
    }
    
    public enum ReloreType
    {
        OVERWRITE, 
        PREPEND, 
        APPEND;
    }
    
    public static class ItemEnchant
    {
        private Enchantment enchantment;
        private int level;
        
        @ConstructorProperties({ "enchantment", "level" })
        public ItemEnchant(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }
}
