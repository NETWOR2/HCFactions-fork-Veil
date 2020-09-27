package net.bfcode.bfhcf.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class FactionMessageArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionMessageArgument(HCFaction plugin) {
        super("message", "Sends a message to your faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "msg" };
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <message>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", StringUtils.join(args, ' ', 1, args.length));
        for (Player target : playerFaction.getOnlinePlayers()) {
            target.sendMessage(format);
        }
        return true;
    }
}
