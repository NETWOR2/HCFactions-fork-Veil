package net.bfcode.bfhcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import java.util.HashSet;
import org.bukkit.inventory.Inventory;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;

public class MapKitCommand implements CommandExecutor, Listener
{
    private HCFaction mainPlugin;
    private File file;
    private FileConfiguration config;
    private Inventory viewKitInventory;
    private String invTitle;
    @SuppressWarnings("rawtypes")
	private HashSet editMode;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public MapKitCommand(HCFaction mainPlugin) {
        this.mainPlugin = mainPlugin;
        this.file = new File(this.mainPlugin.getDataFolder(), "viewkit.yml");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
        this.invTitle = ChatColor.DARK_GRAY + "Map Kit";
        this.viewKitInventory = Bukkit.createInventory((InventoryHolder)null, 18, this.invTitle);
        if (this.config.contains("invItems")) {
            this.viewKitInventory.setContents((ItemStack[])((List)this.config.get("invItems")).toArray(new ItemStack[this.viewKitInventory.getSize()]));
        }
        this.editMode = new HashSet();
        this.mainPlugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.mainPlugin);
    }
    
    private void saveConfig() {
        try {
            this.config.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals(this.invTitle)) {
            this.config.set("invItems", (Object)this.viewKitInventory.getContents());
            this.saveConfig();
        }
        this.editMode.remove(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player)e.getWhoClicked();
            if (e.getInventory().getTitle().equals(this.invTitle)) {
                if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                    return;
                }
                if (!this.editMode.contains(p.getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.editMode.remove(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.editMode.remove(e.getPlayer().getName());
    }
    
    @SuppressWarnings("unchecked")
	public boolean onCommand(CommandSender s, Command c, String alias, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player p = (Player)s;
        if (args.length == 0) {
            p.openInventory(this.viewKitInventory);
            return true;
        }
        if (args[0].equalsIgnoreCase("edit")) {
            if (!p.isOp()) {
                p.sendMessage(ChatColor.RED + "No permission.");
                return false;
            }
            this.editMode.add(p.getName());
            p.openInventory(this.viewKitInventory);
            p.sendMessage(ChatColor.GREEN + "Add in the items you want to include or take out items to remove them in the inventory.");
        }
        return true;
    }
}
