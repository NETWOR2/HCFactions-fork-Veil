package net.bfcode.bfhcf.timer.type;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.Listener;

public class NotchAppleTimer extends PlayerTimer implements Listener {
	
    public NotchAppleTimer(JavaPlugin plugin) {
        super(ConfigurationService.NOTCH_APPLE_TIMER,TimeUnit.MINUTES.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.notch-apple-time")));
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.NOTCH_APPLE_COLOUR;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
            Player player = event.getPlayer();
            if (this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l " + this.name + ": "));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  " + HCFaction.getRemaining(this.defaultCooldown, true, true)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
            }
            else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You still have a " + this.getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
            }
        }
    }
}
