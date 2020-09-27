package net.bfcode.bfhcf.command.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import net.bfcode.bfbase.BaseConstants;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesGiveArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public LivesGiveArgument(HCFaction plugin) {
        super("give", "Help someone out by giving them live(s)");
        this.plugin = plugin;
        this.aliases = new String[] { "transfer", "send", "pay", "add" };
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <playerName> <amount>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        if(eotwRunnable != null) {
        	sender.sendMessage(CC.translate("&cYou are not use this command in EOTW."));
        	return true;
        }
        Integer amount = JavaUtil.tryParseInt(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
            return true;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        Player onlineTarget = target.getPlayer();
        if (sender instanceof Player && !sender.hasPermission("hcf.command.lives.argument.give.bypass")) {
            Player player = (Player)sender;
            int ownedLives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
            if (amount > ownedLives) {
                sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount + " lives, but you only have " + ownedLives + '.');
                return true;
            }
            this.plugin.getDeathbanManager().takeLives(player.getUniqueId(), amount);
        }
        int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
        this.plugin.getDeathbanManager().addLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ' ' + amount + ' ' + ((amount == 1) ? "life" : "lives") + '.');
        sender.sendMessage(ChatColor.GREEN + "Remaining Lives: " + ChatColor.RED + targetLives + ChatColor.RED + ' ' + ((targetLives == 1) ? "life" : "lives") + '.');
        if (onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.GOLD + amount + ' ' + ((amount == 1) ? "life" : "lives") + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
