package net.bfcode.bfhcf.listener;

import org.bukkit.event.EventHandler;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.Listener;

public class DonorOnlyListener implements Listener {
    
    @EventHandler
    public void onJoinServerWhileNotDonor(PlayerLoginEvent e) {
        if (HCFaction.getPlugin().getServerHandler().isDonorOnly() && !e.getPlayer().hasPermission("donoronly.bypass")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.translate(HCFaction.getPlugin().getConfig().getString("ONLY_VIPS_MESSAGE")));
        }
    }
}
