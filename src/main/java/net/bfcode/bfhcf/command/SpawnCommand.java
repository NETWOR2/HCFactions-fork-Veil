package net.bfcode.bfhcf.command;

import org.bukkit.Location;
import org.bukkit.World;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Messager;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SpawnCommand implements CommandExecutor
{
    private HCFaction plugin;
    
    public SpawnCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation().clone();
        Location entityLoc = player.getLocation().clone();
        Faction factionAt = HCFaction.getPlugin().getFactionManager().getFactionAt(entityLoc);
        if (factionAt instanceof KothFaction && !player.hasPermission("rank.staff")) {
        	Messager.player(player, "&cYou cant execute this command in Event zone!");
            return true;
        }
        if (factionAt instanceof CitadelFaction && !player.hasPermission("rank.staff")) {
        	Messager.player(player, "&cYou cant execute this command in Event zone!");
            return true;
        }
        if (factionAt instanceof ConquestFaction && !player.hasPermission("rank.staff")) {
        	Messager.player(player, "&cYou cant execute this command in Event zone!");
            return true;
        }
        if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
        	if(factionAt instanceof PlayerFaction) {
                if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                    player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
                    return false;
                }
                this.plugin.getTimerManager().teleportTimer.teleport(player, spawn, TimeUnit.SECONDS.toMillis(45L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "45 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
        		return true;
        	}
        } 
        else {
        	PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        	if(factionAt.equals(playerFaction)) {
        		if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                    player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
                    return false;
                }
                this.plugin.getTimerManager().teleportTimer.teleport(player, spawn, TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "15 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
        		return true;
        	} else {
        		if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                    player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
                    return false;
                }
                this.plugin.getTimerManager().teleportTimer.teleport(player, spawn, TimeUnit.SECONDS.toMillis(45L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "45 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
        	}
        }
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.hasPermission("rank.staff")) {
            player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            player.sendMessage(ChatColor.YELLOW + "You have been teleported to spawn.");
            return true;
        }
        if(plugin.getEotwHandler().isEndOfTheWorld()) {
        	player.sendMessage(CC.translate("&cYou cant use this command in EOTW!"));
        	return true;
        }
        if (ConfigurationService.KIT_MAP) {
            if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
                return false;
            }
            this.plugin.getTimerManager().teleportTimer.teleport(player, spawn, TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "15 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }
        return true;
    }
}
