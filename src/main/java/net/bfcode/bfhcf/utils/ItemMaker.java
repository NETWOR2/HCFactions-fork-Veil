package net.bfcode.bfhcf.utils;

import org.bukkit.enchantments.Enchantment;
import java.util.List;
import java.util.ArrayList;
import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;

public class ItemMaker
{
    private ItemStack stack;
    private ItemMeta meta;
    
    public ItemMaker(Material material) {
        this(material, 1);
    }
    
    public ItemMaker(Material material, int amount) {
        this(material, amount, (byte)0);
    }
    
    public ItemMaker(ItemStack stack) {
        Preconditions.checkNotNull((Object)stack, (Object)"ItemStack not found.");
        this.stack = stack;
    }
    
    public ItemMaker(Material material, int amount, byte data) {
        Preconditions.checkNotNull((Object)material, (Object)"Material not found.");
        Preconditions.checkArgument(amount > 0, (Object)"Amount must be positive");
        this.stack = new ItemStack(material, amount, (short)data);
    }
    
    public ItemMaker displayName(String name) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setDisplayName(CC.translate(name));
        return this;
    }
    
    public ItemMaker lore(String... strings) {
        List<String> loreArray = new ArrayList<String>();
        for (String loreBit : strings) {
            loreArray.add(loreBit.replace("&", "§"));
        }
        this.meta.setLore((List)loreArray);
        return this;
    }
    
    public ItemMaker lore(List<String> strings) {
        List<String> loreArray = new ArrayList<String>();
        for (String loreBit : strings) {
            loreArray.add(CC.translate(loreBit));
        }
        this.meta.setLore((List)loreArray);
        return this;
    }
    
    public ItemMaker addlore(String... strings) {
        List<String> loreArray = new ArrayList<String>();
        for (String loreBit : strings) {
            loreArray.add(loreBit.replace("&", "§"));
        }
        this.meta.setLore((List)loreArray);
        return this;
    }
    
    public ItemMaker enchant(Enchantment enchantment, int level) {
        return this.setEnchant(enchantment, level, true);
    }
    
    public ItemMaker setEnchant(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            this.stack.addUnsafeEnchantment(enchantment, level);
        }
        else {
            this.stack.addEnchantment(enchantment, level);
        }
        return this;
    }
    
    public ItemMaker data(short data) {
        this.stack.setDurability(data);
        return this;
    }
    
    public ItemStack create() {
        if (this.meta != null) {
            this.stack.setItemMeta(this.meta);
        }
        return this.stack;
    }
}
