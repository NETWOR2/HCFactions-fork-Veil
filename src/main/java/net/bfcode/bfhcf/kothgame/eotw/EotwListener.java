package net.bfcode.bfhcf.kothgame.eotw;

import org.bukkit.event.EventPriority;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.bfcode.bfbase.kit.event.KitApplyEvent;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.FactionClaimChangeEvent;
import net.bfcode.bfhcf.faction.event.cause.ClaimChangeCause;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.event.Listener;

public class EotwListener implements Listener
{
    private HCFaction plugin;
    
    public EotwListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMobSpawnFromSpawner(CreatureSpawnEvent e) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            switch (e.getSpawnReason()) {
                case SPAWNER: {
                    if (e.getEntity().getType() != EntityType.PIG) {
                        e.setCancelled(true);
                    }
                }
                case SPAWNER_EGG: {
                    if (e.getEntity().getType() != EntityType.PIG) {
                        e.setCancelled(true);
                    }
                }
                case DISPENSE_EGG: {
                    if (e.getEntity().getType() == EntityType.PIG) {
                        break;
                    }
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(FactionClaimChangeEvent event) {
        ClaimableFaction faction;
        if (this.plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM && (faction = event.getClaimableFaction()) instanceof PlayerFaction) {
            event.setCancelled(true);
            event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
        }
    }
}
