package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import java.util.UUID;

import org.bukkit.event.Listener;

import net.bfcode.bfbase.kit.event.KitApplyEvent;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.timer.event.TimerStartEvent;
import net.bfcode.bfhcf.timer.type.PvpProtectionTimer;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class KitListener implements Listener
{
    private HCFaction plugin;
    
    public KitListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onTimer(TimerStartEvent e) {
        if (ConfigurationService.KIT_MAP && e.getTimer() instanceof PvpProtectionTimer) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown((UUID)e.getUserUUID().get());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKitApply(KitApplyEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        PlayerFaction playerFaction;
        if (ConfigurationService.KIT_MAP && !plugin.getEotwHandler().isEndOfTheWorld() && !factionAt.isSafezone() && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) == null || !playerFaction.equals(factionAt))) {
            player.sendMessage(ChatColor.RED + "Kits can only be applied in safe-zones or your own claims.");
            event.setCancelled(true);
        }
    }
}
