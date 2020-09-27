package net.bfcode.bfhcf.timer.type;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.Listener;

public class GoldenAppleTimer extends PlayerTimer implements Listener
{
    public GoldenAppleTimer(JavaPlugin plugin) {
        super(ConfigurationService.APPLE_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.apple-time")));
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.APPLE_COLOUR;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 0) {
            Player player = event.getPlayer();
            if (!this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are still on &eGolden Apple &ccooldown"));
            }
        }
    }
}
