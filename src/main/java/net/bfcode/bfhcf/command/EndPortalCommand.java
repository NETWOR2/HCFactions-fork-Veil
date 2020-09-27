package net.bfcode.bfhcf.command;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import org.bukkit.ChatColor;
import java.util.UUID;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;

public class EndPortalCommand implements CommandExecutor, Listener
{
    private String ITEM_DISPLAYNAME;
    private HCFaction plugin;
    private Map<UUID, LocationPair> playerSelections;
    
    public EndPortalCommand(HCFaction plugin) {
        this.plugin = plugin;
        this.ITEM_DISPLAYNAME = ChatColor.GREEN + "Endportal Maker";
        this.playerSelections = new HashMap<UUID, LocationPair>();
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getClickedBlock() != null) {
            ItemStack itemStack = e.getItem();
            Block b = e.getClickedBlock();
            if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)) {
                LocationPair locationPair = this.playerSelections.computeIfAbsent(e.getPlayer().getUniqueId(), func -> this.playerSelections.put(p.getUniqueId(), new LocationPair(null, null)));
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (b.getType() != Material.ENDER_PORTAL_FRAME) {
                        e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
                        return;
                    }
                    locationPair.setFirstLoc(b.getLocation());
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the first location.");
                }
                else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (b.getType() != Material.ENDER_PORTAL_FRAME) {
                        e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
                        return;
                    }
                    if (locationPair.getFirstLoc() == null) {
                        e.getPlayer().sendMessage(ChatColor.RED + "Please set the first location (by left clicking the end portal frame).");
                        return;
                    }
                    locationPair.setSecondLoc(b.getLocation());
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the second location.");
                    Location firstLoc = locationPair.getFirstLoc();
                    Location secondLoc = locationPair.getSecondLoc();
                    if (firstLoc.distance(secondLoc) > 6.0) {
                        e.getPlayer().sendMessage(ChatColor.RED + "You cannot create an end portal that big.");
                        return;
                    }
                    if (firstLoc.getBlockY() != secondLoc.getBlockY()) {
                        e.getPlayer().sendMessage(ChatColor.RED + "Make sure that the portals have the same elevation.");
                        return;
                    }
                    int minX = Math.min(firstLoc.getBlockX(), secondLoc.getBlockX());
                    int minY = Math.min(firstLoc.getBlockY(), secondLoc.getBlockY());
                    int minZ = Math.min(firstLoc.getBlockZ(), secondLoc.getBlockZ());
                    int maxX = Math.max(firstLoc.getBlockX(), secondLoc.getBlockX());
                    int maxY = Math.max(firstLoc.getBlockY(), secondLoc.getBlockY());
                    int maxZ = Math.max(firstLoc.getBlockZ(), secondLoc.getBlockZ());
                    for (int x = minX; x <= maxX; ++x) {
                        for (int y = minY; y <= maxY; ++y) {
                            for (int z = minZ; z <= maxZ; ++z) {
                                Block block = b.getWorld().getBlockAt(x, y, z);
                                if (block.isEmpty()) {
                                    block.setType(Material.ENDER_PORTAL);
                                }
                            }
                        }
                    }
                    e.setCancelled(true);
                    new BukkitRunnable() {
                        public void run() {
                            e.getPlayer().setItemInHand((ItemStack)null);
                            e.getPlayer().updateInventory();
                        }
                    }.runTask((Plugin)this.plugin);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Created an end portal");
                    this.playerSelections.remove(p.getUniqueId());
                }
            }
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack itemStack = e.getItemDrop().getItemStack();
        if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)) {
            e.getItemDrop().remove();
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.playerSelections.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.playerSelections.remove(e.getPlayer().getUniqueId());
    }
    
    public boolean onCommand(CommandSender s, Command c, String alias, String[] args) {
        if (!(s instanceof Player)) {
            return true;
        }
        Player p = (Player)s;
        if (p.getInventory().firstEmpty() == -1) {
            return true;
        }
        if (!p.hasPermission("hcf.command.endportal")) {
            s.sendMessage(ChatColor.RED + "You do not have acces to this command.");
            return true;
        }
        ItemStack portalMaker = new ItemStack(Material.GOLD_SWORD);
        ItemMeta itemMeta = portalMaker.getItemMeta();
        itemMeta.setDisplayName(this.ITEM_DISPLAYNAME);
        portalMaker.setItemMeta(itemMeta);
        p.getInventory().addItem(new ItemStack[] { portalMaker });
        return true;
    }
    
    private class LocationPair
    {
        private Location firstLoc;
        private Location secondLoc;
        
        public LocationPair(Location firstLoc, Location secondLoc) {
            this.firstLoc = firstLoc;
            this.secondLoc = secondLoc;
        }
        
        public Location getFirstLoc() {
            return this.firstLoc;
        }
        
        public void setFirstLoc(Location firstLoc) {
            this.firstLoc = firstLoc;
        }
        
        public Location getSecondLoc() {
            return this.secondLoc;
        }
        
        public void setSecondLoc(Location secondLoc) {
            this.secondLoc = secondLoc;
        }
    }
}
