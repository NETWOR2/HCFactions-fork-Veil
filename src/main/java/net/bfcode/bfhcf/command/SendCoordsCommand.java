package net.bfcode.bfhcf.command;

import java.text.DecimalFormat;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.HashMap;

import org.bukkit.command.CommandExecutor;

public class SendCoordsCommand implements CommandExecutor
{
    private HCFaction plugin;
    public HashMap<String, Long> cooldowns;
    
    public SendCoordsCommand(HCFaction plugin) {
        this.cooldowns = new HashMap<String, Long>();
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int cooldownTime = 15;
        if (this.cooldowns.containsKey(sender.getName())) {
            long sLeft = this.cooldowns.get(sender.getName()) / 1000L + cooldownTime - System.currentTimeMillis() / 1000L;
            if (sLeft > 0L) {
                sender.sendMessage(ChatColor.RED + "You must wait " + sLeft + " more seconds to use this command again.");
                return true;
            }
        }
        this.cooldowns.put(sender.getName(), System.currentTimeMillis());
        Player player = (Player)sender;
		PlayerFaction faction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return true;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        DecimalFormat df = new DecimalFormat("#");
        playerFaction.broadcast(ChatColor.translateAlternateColorCodes('&', "&2(Faction) " + player.getName() + "&7: &e[" + df.format(player.getLocation().getX()) + ", " + df.format(player.getLocation().getY()) + ", " + df.format(player.getLocation().getZ()) + "]"));
        return true;
    }
}
