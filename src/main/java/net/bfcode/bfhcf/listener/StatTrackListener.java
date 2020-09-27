package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;

import org.bukkit.event.Listener;

public class StatTrackListener implements Listener
{
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = e.getEntity();
            FactionUser hcf = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId());
            if (hcf.getKills() > 0) {
                hcf.setKills(0);
            }
        }
    }
}
