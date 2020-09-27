package net.bfcode.bfhcf.utils;

import java.io.IOException;
import java.io.InputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import java.io.OutputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;

public class InventoryMaker
{
    private Inventory Inventory;
    
    public InventoryMaker(InventoryHolder owner, int rows) {
        this.Inventory = Bukkit.createInventory(owner, rows * 9);
    }
    
    public InventoryMaker(InventoryHolder owner, InventoryType type) {
        this.Inventory = Bukkit.createInventory(owner, type);
    }
    
    public InventoryMaker(InventoryHolder owner, int rows, String title) {
        this.Inventory = Bukkit.createInventory(owner, rows * 9, CC.translate(title));
    }
    
    public InventoryMaker(InventoryHolder owner, InventoryType type, String title) {
        this.Inventory = Bukkit.createInventory(owner, type, CC.translate(title));
    }
    
    public InventoryMaker setItem(int slot, ItemStack stack) {
        this.Inventory.setItem(slot, stack);
        return this;
    }
    
    public InventoryMaker setItems(Map<Integer, ItemStack> items) {
        int is = 0;
        for (ItemStack is2 : items.values()) {
            if (is2 != null) {
                this.Inventory.setItem(is, is2);
            }
            ++is;
        }
        return this;
    }
    
    public InventoryMaker addItem(ItemStack stack) {
        this.Inventory.addItem(new ItemStack[] { stack });
        return this;
    }
    
    public String toStack() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream((OutputStream)outputStream);
            dataOutput.writeInt(this.Inventory.getSize());
            for (int i = 0; i < this.Inventory.getSize(); ++i) {
                dataOutput.writeObject((Object)this.Inventory.getItem(i));
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            throw new IllegalStateException("Error: Unable to save item stacks!", e);
        }
    }
    
    public Map<Integer, ItemStack> fromStack(String f) {
        Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(f));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream((InputStream)inputStream);
            for (int size = dataInput.readInt(), i = 0; i < size; ++i) {
                items.put(i, (ItemStack)dataInput.readObject());
            }
            dataInput.close();
        }
        catch (IOException | ClassNotFoundException ex3) {
            Exception ex2 = null;
            Exception ex = ex2;
            ex.printStackTrace();
        }
        return items;
    }
    
    public Inventory create() {
        return this.Inventory;
    }
}