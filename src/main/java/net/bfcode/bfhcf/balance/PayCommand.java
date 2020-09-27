package net.bfcode.bfhcf.balance;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.BaseConstants;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class PayCommand implements CommandExecutor, TabCompleter
{
    private HCFaction plugin;
    
    public PayCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unlikely-arg-type")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
            return true;
        }
        
        Integer amount = JavaUtil.tryParseInt(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
            return true;
        }
        
        Player senderPlayer = (Player)sender;
        @SuppressWarnings("unused")
		int n;
        int senderBalance = n = ((senderPlayer != null) ? this.plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024);
        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "Insufficient funds.");
            return true;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
            return true;
        }
        Player targetPlayer = target.getPlayer();
        if (!target.hasPlayedBefore() && targetPlayer == null) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        if (targetPlayer == null) {
            return false;
        }
        if (senderPlayer != null) {
            this.plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount);
        }
        this.plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
        targetPlayer.sendMessage(ChatColor.YELLOW + sender.getName() + " has sent you " + ChatColor.LIGHT_PURPLE + '$' + amount + ChatColor.YELLOW + '.');
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.LIGHT_PURPLE + '$' + amount + ChatColor.YELLOW + " to " + target.getName() + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
