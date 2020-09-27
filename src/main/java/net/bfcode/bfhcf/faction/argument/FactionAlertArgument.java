package net.bfcode.bfhcf.faction.argument;

import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.HashMap;

public class FactionAlertArgument extends CommandArgument
{
    private HCFaction plugin;
    public HashMap<String, Long> cooldowns;
    
    public FactionAlertArgument(HCFaction plugin) {
    	super("alert", "Send a message to your members.");
        this.cooldowns = new HashMap<String, Long>();
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <message>";
    }
    
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
    	if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
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
        if (faction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a leader to send a alert.");
            return true;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        playerFaction.broadcast(ChatColor.translateAlternateColorCodes('&', "&4(Faction Alert) " + player.getName() + "&7: &e" + StringUtils.join(args, ' ', 1, args.length)));
        return true;
    }
}
