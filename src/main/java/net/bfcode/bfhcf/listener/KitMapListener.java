package net.bfcode.bfhcf.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import net.bfcode.bfbase.kit.event.KitApplyEvent;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.event.TimerStartEvent;
import net.bfcode.bfhcf.timer.type.PvpProtectionTimer;

public class KitMapListener implements Listener
{
    HCFaction plugin;
    
    public KitMapListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onTimer(TimerStartEvent e) {
        if (e.getTimer() instanceof PvpProtectionTimer) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getUserUUID().get());
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(e.getPlayer()) >= 0L) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.getInventory().setArmorContents((ItemStack[])null);
    }
}
