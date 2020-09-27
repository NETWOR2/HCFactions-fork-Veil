package net.bfcode.bfhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class StatsCommand implements CommandExecutor {
	
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou do not have to execute this command in Console."));
    		return true;
    	}
        Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found.");
            return true;
        }
        this.sendInformation(player, Bukkit.getOfflinePlayer(args[0]));
        return true;
    }
    
    public void sendInformation(Player player, OfflinePlayer target) {
    	
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        if(target.hasPlayedBefore()) {
            FactionUser hcf = HCFaction.getPlugin().getUserManager().getUser(target.getUniqueId());
            int targetKills = hcf.getKills();
            int targetDeaths = hcf.getDeaths();
            int targetLives = HCFaction.getPlugin().getDeathbanManager().getLives(target.getUniqueId());
            player.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Statistics");
            player.sendMessage("");
            if (HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()) != null) {
                player.sendMessage(HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getRelation((CommandSender)player).toChatColour() + ChatColor.YELLOW.toString() + " Player: " + ChatColor.WHITE + target.getName());
                new Text(ChatColor.AQUA + " Faction: " + HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getDisplayName((CommandSender)player)).setHoverText(ChatColor.YELLOW + "Click to view Faction").setClick(ClickAction.RUN_COMMAND, "/f who " + HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getName()).send((CommandSender)player);
            }
            else {
                player.sendMessage(ChatColor.AQUA + " Player: " + ChatColor.WHITE + target.getName());
            }
            player.sendMessage(ChatColor.AQUA + " Kills: " + ChatColor.WHITE + targetKills); 
            player.sendMessage(ChatColor.AQUA + " Deaths: " + ChatColor.WHITE + targetDeaths);
            player.sendMessage(ChatColor.AQUA + " Balance: " + ChatColor.LIGHT_PURPLE + "$" + HCFaction.getPlugin().getEconomyManager().getBalance(target.getUniqueId()));
            player.sendMessage(ChatColor.AQUA + " Available Lives: " + ChatColor.WHITE + targetLives);
            if (hcf.getDeathban() != null) {
                new Text(ChatColor.AQUA + " Deathbanned: " + (hcf.getDeathban().isActive() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "False")));
            }
            else if (!ConfigurationService.KIT_MAP) {
                player.sendMessage(ChatColor.AQUA + " Deathbanned: " + ChatColor.RED + "False");
            }
            player.sendMessage(ChatColor.AQUA + " Playtime: " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true));	
        } else {
        	player.sendMessage(CC.translate("&cThis players hasn´t played in the server!"));
        }
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
}
