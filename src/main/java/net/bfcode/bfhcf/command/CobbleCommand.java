package net.bfcode.bfhcf.command;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CobbleCommand implements Listener, CommandExecutor
{
	private static ArrayList cobbletoggle;
    
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return false;
        }
        Player p = (Player)sender;
        if (args.length == 0) {
            if (!CobbleCommand.cobbletoggle.contains(p.getName())) {
                p.sendMessage(ConfigurationService.COBBLE_DISABLED);
                CobbleCommand.cobbletoggle.add(p.getName());
            }
            else if (CobbleCommand.cobbletoggle.contains(p.getName())) {
                CobbleCommand.cobbletoggle.remove(p.getName());
                p.sendMessage(ConfigurationService.COBBLE_ENABLED);
            }
        }
        return true;
    }
    
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Material type = event.getItem().getItemStack().getType();
        if ((type == Material.STONE || type == Material.COBBLESTONE) && CobbleCommand.cobbletoggle.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        CobbleCommand.cobbletoggle.remove(p.getName());
    }
    
    static {
        CobbleCommand.cobbletoggle = new ArrayList();
    }
}
