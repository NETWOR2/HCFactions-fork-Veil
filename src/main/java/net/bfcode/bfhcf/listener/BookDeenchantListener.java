package net.bfcode.bfhcf.listener;

import java.util.HashSet;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import java.util.Random;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.enchantments.Enchantment;

import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfbase.util.ParticleEffect;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

public class BookDeenchantListener implements Listener
{
    private static ItemStack EMPTY_BOOK;
    private static String PERMISSION_1 = "hcf.deenchant.1";
    private static String PERMISSION_2 = "hcf.deenchant.2";
    private static Set<Inventory> tracked;
    
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (BookDeenchantListener.tracked.contains(e.getInventory())) {
            ParticleEffect.CRIT.display((Player)e.getPlayer(), e.getPlayer().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
            e.getInventory().clear();
            BookDeenchantListener.tracked.remove(e.getInventory());
        }
    }
    
    @EventHandler
    public void onClickBook(InventoryClickEvent e) {
        if (BookDeenchantListener.tracked.contains(e.getInventory())) {
            e.setCancelled(true);
            String levels = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0).replace("To remove this enchant it will cost ", "").replace(" levels", ""));
            Integer level = (JavaUtil.tryParseInt(levels) == null) ? 0 : JavaUtil.tryParseInt(levels);
            if (((Player)e.getWhoClicked()).getLevel() < level) {
                ((Player)e.getWhoClicked()).sendMessage(ChatColor.RED + "You do not have enough levels");
                e.setCancelled(true);
                return;
            }
            ((Player)e.getWhoClicked()).setLevel(((Player)e.getWhoClicked()).getLevel() - level);
            e.setCancelled(true);
            for (Enchantment enchantment : e.getCurrentItem().getEnchantments().keySet()) {
                e.getWhoClicked().getItemInHand().removeEnchantment(enchantment);
            }
            ParticleEffect.CRIT.display((Player)e.getWhoClicked(), e.getWhoClicked().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
            e.getWhoClicked().closeInventory();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem()) {
            Player player = event.getPlayer();
            if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && player.getGameMode() != GameMode.CREATIVE) {
                ItemStack stack = event.getItem();
                if (stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
                    ItemMeta meta = stack.getItemMeta();
                    if (meta instanceof EnchantmentStorageMeta) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                        for (Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                        event.setCancelled(true);
                        player.setItemInHand(BookDeenchantListener.EMPTY_BOOK);
                    }
                }
                else if (stack != null && stack.getItemMeta().hasEnchants()) {
                    if (event.getPlayer().hasPermission(PERMISSION_1) && !event.getPlayer().hasPermission(PERMISSION_2)) {
                        Random random = new Random();
                        Integer randomNumber = random.nextInt(stack.getEnchantments().keySet().size()) + 1;
                        Integer enchant = 0;
                        for (Enchantment enchantment2 : stack.getEnchantments().keySet()) {
                            @SuppressWarnings("unused")
							Integer n = enchant;
                            @SuppressWarnings("unused")
							Integer n2;
                            enchant = (n2 = enchant + 1);
                            if (!enchant.equals(randomNumber)) {
                                continue;
                            }
                            stack.removeEnchantment(enchantment2);
                        }
                        short durability = stack.getDurability();
                        durability /= 5;
                        short afterMath = stack.getDurability();
                        afterMath -= durability;
                        stack.setDurability(afterMath);
                        event.setCancelled(true);
                        ParticleEffect.CRIT.display(event.getPlayer(), event.getClickedBlock().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
                    }
                    else if (event.getPlayer().hasPermission("hcf.deenchant.2") && event.getPlayer().hasPermission("hcf.deenchant.1")) {
                        Inventory trackedInv = Bukkit.createInventory((InventoryHolder)event.getPlayer(), 9, ChatColor.GREEN + "Item-DeEnchanter");
                        BookDeenchantListener.tracked.add(trackedInv);
                        for (Enchantment enchantment3 : stack.getEnchantments().keySet()) {
                            trackedInv.addItem(new ItemStack[] { new ItemBuilder(stack.getType()).enchant(enchantment3, stack.getEnchantmentLevel(enchantment3)).lore(new String[] { ChatColor.GREEN + "To remove this enchant it will cost " + ChatColor.YELLOW + stack.getEnchantmentLevel(enchantment3) * 5 + ChatColor.GREEN + " levels" }).build() });
                        }
                        event.getPlayer().openInventory(trackedInv);
                    }
                }
            }
        }
    }
    
    static {
        EMPTY_BOOK = new ItemStack(Material.BOOK, 1);
        tracked = new HashSet<Inventory>();
    }
}
