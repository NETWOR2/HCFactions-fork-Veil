package net.bfcode.bfhcf.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.LandMap;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.GuavaCompat;
import net.bfcode.bfhcf.visualise.VisualType;

public class FactionMapArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionMapArgument(HCFaction plugin) {
        super("map", "View all claims around your chunk.");
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " [factionName]";
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        UUID uuid = player.getUniqueId();
        FactionUser factionUser = this.plugin.getUserManager().getUser(uuid);
        VisualType visualType;
        if (args.length <= 1) {
            visualType = VisualType.CLAIM_MAP;
        }
        else {
            visualType = (VisualType)GuavaCompat.getIfPresent((Class)VisualType.class, args[1]).orNull();
            if (visualType == null) {
                player.sendMessage(ChatColor.RED + "Visual type " + args[1] + " not found.");
                return true;
            }
        }
        @SuppressWarnings("unused")
		boolean bl;
        boolean newShowingMap = bl = !factionUser.isShowClaimMap();
        if (newShowingMap) {
            if (!LandMap.updateMap(player, this.plugin, visualType, true)) {
                return true;
            }
        }
        else {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
            sender.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
        }
        factionUser.setShowClaimMap(newShowingMap);
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        VisualType[] values = VisualType.values();
        ArrayList<String> results = new ArrayList<String>(values.length);
        for (VisualType visualType : values) {
            results.add(visualType.name());
        }
        return results;
    }
}
