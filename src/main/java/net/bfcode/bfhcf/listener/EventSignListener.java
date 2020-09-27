package net.bfcode.bfhcf.listener;

import java.util.Iterator;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import java.util.Arrays;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import com.google.common.collect.Lists;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.DateTimeFormats;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

public class EventSignListener implements Listener
{
    private static String EVENT_SIGN_ITEM_NAME;
    
    public static ItemStack getEventSign(String playerName, String kothName) {
        ItemStack stack = new ItemStack(Material.SIGN, 1);
        ItemMeta meta = stack.getItemMeta();
        String name = ChatColor.AQUA + kothName;
        meta.setDisplayName(EventSignListener.EVENT_SIGN_ITEM_NAME);
        meta.setLore((List)Lists.newArrayList(new String[] { ChatColor.AQUA + HCFaction.getPlugin().getFactionManager().getFaction(playerName).getName(), ChatColor.GRAY + "captured by", ChatColor.AQUA + ChatColor.stripColor(name), ChatColor.GRAY + DateTimeFormats.DAY_MTH_HR_MIN.format(System.currentTimeMillis()) }));
        stack.setItemMeta(meta);
        return stack;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        if (this.isEventSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (this.isEventSign(block)) {
            BlockState state = block.getState();
            Sign sign = (Sign)state;
            ItemStack stack = new ItemStack(Material.SIGN, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(EventSignListener.EVENT_SIGN_ITEM_NAME);
            meta.setLore(Arrays.asList(sign.getLines()));
            stack.setItemMeta(meta);
            Player player = event.getPlayer();
            World world = player.getWorld();
            if (player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
                world.dropItemNaturally(block.getLocation(), stack);
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
            state.update();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        BlockState state = event.getBlock().getState();
        if (state instanceof Sign && stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().equals(EventSignListener.EVENT_SIGN_ITEM_NAME)) {
                Sign sign = (Sign)state;
                List<String> lore = (List<String>)meta.getLore();
                int count = 0;
                for (String loreLine : lore) {
                    sign.setLine(count++, loreLine);
                    if (count == 4) {
                        break;
                    }
                }
                sign.update();
            }
        }
    }
    
    private boolean isEventSign(Block block) {
        BlockState state = block.getState();
        if (state instanceof Sign) {
            String[] lines = ((Sign)state).getLines();
            return lines.length > 0 && lines[1] != null && lines[1].equals(ChatColor.GRAY + "captured by");
        }
        return false;
    }
    
    static {
        EVENT_SIGN_ITEM_NAME = ChatColor.GOLD + "Event Sign";
    }
}
