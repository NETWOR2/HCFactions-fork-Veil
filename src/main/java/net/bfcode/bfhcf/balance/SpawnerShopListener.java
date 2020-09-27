package net.bfcode.bfhcf.balance;

import org.bukkit.scheduler.BukkitRunnable;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.Handler;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.Listener;

public class SpawnerShopListener extends Handler implements Listener {
    private String[] lines;
    private String[] error;
    private Inventory inventory;
    private ItemStack BLANK;
    private ImmutableMap<EntityType, Integer> spawners;
    private ImmutableMap<EntityType, Integer> SPAWNERITEMS;
    
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public SpawnerShopListener(HCFaction instance) {
        super(instance);
        this.lines = new String[] { "", CC.translate("&a[SpawnerShop]"), CC.translate("&0Click to open") };
        this.error = new String[] { "", CC.translate("&0SpawnerShop"),"" };
        this.BLANK = new ItemBuilder(Material.STAINED_GLASS_PANE).data(DyeColor.GRAY.getData()).displayName(" ").build();
        this.spawners = new ImmutableMap.Builder()
        		.put(EntityType.SKELETON, 20000)
        		.put(EntityType.SPIDER, 20000)
        		.put(EntityType.ZOMBIE, 20000)
        		.put(EntityType.CAVE_SPIDER, 20000)
        		.build();
        this.SPAWNERITEMS = new ImmutableMap.Builder()
        		.put(EntityType.SKELETON, 10)
        		.put(EntityType.SPIDER, 12)
        		.put(EntityType.ZOMBIE, 14)
        		.put(EntityType.CAVE_SPIDER, 16)
        		.build();
        this.inventory = Bukkit.createInventory(null, 27, CC.translate("&aSpawner Shop"));
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, this.BLANK);
        }
        for (Map.Entry<EntityType, Integer> entry : this.spawners.entrySet()) {
            EntityType entityType = entry.getKey();
            int value = entry.getValue();
            String name = CC.translate("&6&l" + this.capitalizeString(entityType.name()));
            String costline = CC.translate("&eCost: &f$" + value);
            ItemStack itemStack = new ItemBuilder(Material.MOB_SPAWNER).data(entityType.getTypeId()).displayName(name).loreLine(costline).build();
            Integer slot = (Integer)this.SPAWNERITEMS.get(entityType);
            if (slot != null) {
                this.inventory.setItem((int)slot, itemStack);
            }
        }
    }
    
    @Override
    public void enable() {
        this.getInstance().getServer().getPluginManager().registerEvents(this, HCFaction.getPlugin());
    }
    
    @Override
    public void disable() {
    }
    
    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if (event.getLine(1).equals("SpawnerShop")) {
            Player player = event.getPlayer();
            if (player.hasPermission("rank.admin")) {
                for (int i = 0; i < this.lines.length; ++i) {
                    event.setLine(i, this.lines[i]);
                }
            }
            else {
                for (int i = 0; i < this.error.length; ++i) {
                    event.setLine(i, this.error[i]);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.useInteractedBlock() == Event.Result.ALLOW && block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            for (int i = 0; i < this.lines.length; ++i) {
                if (!sign.getLine(i).equals(this.lines[i])) {
                    return;
                }
            }
            player.openInventory(this.inventory);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getView() != null && event.getView().getTopInventory() != null && event.getView().getTopInventory().equals(this.inventory)) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(this.inventory)) {
                Player player = (Player)event.getWhoClicked();
                int slot = event.getSlot();
                EntityType entityType = null;
                for (Map.Entry<EntityType, Integer> entry : this.SPAWNERITEMS.entrySet()) {
                    if (entry.getValue() == slot) {
                        entityType = entry.getKey();
                        break;
                    }
                }
                if (entityType != null) {
                    int cost = (int)this.spawners.get(entityType);
                    if (cost > HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId())) {
                        player.sendMessage(CC.translate("&cYou can't afford this."));
                        player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0f, 1.0f);
                    }
                    else {
                    	HCFaction.getPlugin().getEconomyManager().setBalance(player.getUniqueId(), HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) - cost);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        player.sendMessage(CC.translate("&aSuccessful purchase."));
                        ItemStack stack = new ItemBuilder(Material.MOB_SPAWNER).displayName(CC.translate("&aSpawner")).data(entityType.getTypeId()).loreLine(CC.translate("&f" + WordUtils.capitalizeFully(entityType.name()))).build();
                        for (ItemStack itemStack : player.getInventory().addItem(new ItemStack[] { stack }).values()) {
                            player.getWorld().dropItem(player.getLocation(), itemStack);
                        }
                    }
                }
            }
        }
    }
    
    public void closeInventory(Player player) {
        new BukkitRunnable() {
            public void run() {
                player.closeInventory();
            }
        }.runTask(HCFaction.getPlugin());
    }
    
    public String capitalizeString(String string) {
        string = string.replace("_", " ");
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; ++i) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            }
            else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }
}
