package net.bfcode.bfhcf.command;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;

import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class NetherCommand implements CommandExecutor
{
    private HCFaction plugin;
    
    public NetherCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player s = (Player)sender;
        Location netherSpawn = Bukkit.getWorld("world_nether").getSpawnLocation();
        if (((Player)sender).getGameMode().equals((Object)GameMode.CREATIVE)) {
            s.teleport(netherSpawn);
            return true;
        }
        Location location = s.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        PlayerFaction playerFaction;
        if (!factionAt.isSafezone() && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(s)) == null || !playerFaction.equals(factionAt))) {
            s.sendMessage(ChatColor.RED + "You can only teleport to the nether when in safe-zones or your own claim.");
            return false;
        }
        PlayerTimer sptimer = this.plugin.getTimerManager().spawnTagTimer;
        @SuppressWarnings("unused")
		long sremaining = sptimer.getRemaining(s);
        if ((sremaining = (sptimer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(s)) > 0L) {
            s.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
            return false;
        }
        PlayerTimer etimer = this.plugin.getTimerManager().enderPearlTimer;
        @SuppressWarnings("unused")
		long eremaining = etimer.getRemaining(s);
        if ((eremaining = (sptimer = this.plugin.getTimerManager().enderPearlTimer).getRemaining(s)) > 0L) {
            s.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Enderpearl Timer" + ChatColor.RED + " is active.");
            return false;
        }
        this.plugin.getTimerManager().teleportTimer.teleport(s, netherSpawn, TimeUnit.SECONDS.toMillis(30L), ChatColor.YELLOW + "Teleporting to nether spawn in " + ChatColor.LIGHT_PURPLE + "30 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }
}
