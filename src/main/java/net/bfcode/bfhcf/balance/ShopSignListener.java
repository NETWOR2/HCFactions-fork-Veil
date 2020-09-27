package net.bfcode.bfhcf.balance;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.ChatColor;
import java.util.Arrays;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.util.InventoryUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.JavaUtil;
import net.bfcode.bfhcf.wrench.Wrench;

import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.regex.Pattern;
import org.bukkit.event.Listener;

public class ShopSignListener implements Listener {
    @SuppressWarnings("unused")
	private static long SIGN_TEXT_REVERT_TICKS = 100L;
    private static Pattern ALPHANUMERIC_REMOVER;
    private HCFaction plugin;
    
    public ShopSignListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (state instanceof Sign) {
                Sign sign = (Sign)state;
                String[] lines = sign.getLines();
                Integer quantity = JavaUtil.tryParseInt(lines[2]);
                if (quantity == null) {
                    return;
                }
                Integer price = JavaUtil.tryParseInt(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
                if (price == null) {
                    return;
                }
                ItemStack stack;
                if (lines[1].equalsIgnoreCase("Crowbar")) {
                    stack = new Wrench().getItemIfPresent();
                }
                else if ((stack = BasePlugin.getPlugin().getItemDb().getItem(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), (int)quantity)) == null) {
                    return;
                }
                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                if ((lines[0].contains("Sell") && lines[0].contains(ChatColor.RED.toString())) || lines[0].contains(ChatColor.AQUA.toString())) {
                    int sellQuantity = Math.min(quantity, InventoryUtils.countAmount((Inventory)player.getInventory(), stack.getType(), stack.getDurability()));
                    if (sellQuantity <= 0) {
                        fakeLines[0] = ChatColor.RED + "Not carrying any";
                        fakeLines[2] = ChatColor.RED + "on you.";
                        fakeLines[3] = "";
                    }
                    else {
                        int newPrice = price / quantity * sellQuantity;
                        fakeLines[0] = ChatColor.GREEN + "Sold " + sellQuantity;
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + newPrice;
                        this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                        InventoryUtils.removeItem((Inventory)player.getInventory(), stack.getType(), (short)stack.getData().getData(), sellQuantity);
                        player.updateInventory();
                    }
                }
                else {
                    if (!lines[0].contains("Buy") || !lines[0].contains(ChatColor.GREEN.toString())) {
                        return;
                    }
                    if (price > this.plugin.getEconomyManager().getBalance(player.getUniqueId())) {
                        fakeLines[0] = ChatColor.RED + "Cannot afford";
                    }
                    else {
                        fakeLines[0] = ChatColor.GREEN + "Item bought";
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + price;
                        this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price);
                        World world = player.getWorld();
                        Location location = player.getLocation();
                        Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { stack });
                        for (Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                            world.dropItemNaturally(location, (ItemStack)excessItemStack.getValue());
                        }
                        player.setItemInHand(player.getItemInHand());
                        player.updateInventory();
                    }
                }
                event.setCancelled(true);
                BasePlugin.getPlugin().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
            }
        }
    }
    
    static {
        ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    }
}
