package net.bfcode.bfhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.minecraft.util.com.google.common.primitives.Ints;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class StatsManagerCommand implements CommandExecutor
{
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cThis command only execute by players."));
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("hcf.command.statsmanager")) {
            player.sendMessage(CC.translate("&cYou don't have permission to execute this command."));
            return true;
        }
        if (args.length < 4) {
            this.getUsage((CommandSender)player);
        }
        else if (args[0].equalsIgnoreCase("kills")) {
            if (args[1].equalsIgnoreCase("add")) {
            	OfflinePlayer targ = Bukkit.getOfflinePlayer(args[2]);
                FactionUser target = HCFaction.getPlugin().getUserManager().getUser(targ.getUniqueId());
                if(target == null) {
                	player.sendMessage(CC.translate("This player doesn´t exist."));
                	return true;
                }
                Integer amount = Ints.tryParse(args[3]);
                if (amount == null) {
                    player.sendMessage(CC.translate("&c'" + args[1] + "' is not a valid number."));
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage(CC.translate("&cThe amount must be positive."));
                    return true;
                }
                target.setKills(target.getKills() + amount);
                player.sendMessage(CC.translate("&aAdded " + amount + " kills to " + target.getOfflinePlayer().getName() + " account."));
            }
            else if (args[1].equalsIgnoreCase("remove")) {
                OfflinePlayer targ = Bukkit.getOfflinePlayer(args[2]);
                FactionUser target = HCFaction.getPlugin().getUserManager().getUser(targ.getUniqueId());
                Integer amount = Ints.tryParse(args[3]);
                if (amount == null) {
                    player.sendMessage(CC.translate("&c'" + args[1] + "' is not a valid number."));
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage(CC.translate("&cThe amount must be positive."));
                    return true;
                }
                if (target.getKills() < amount) {
                    player.sendMessage(CC.translate("&c" + targ.getName() + " don't have " + amount + " kills."));
                    return true;
                }
                target.setKills(target.getKills() - amount);
                player.sendMessage(CC.translate("&aRemoved " + amount + " kills from " + targ.getName() + " account."));
            }
            else {
                player.sendMessage(CC.translate("&cStatsManager sub-command '" + args[1] + "' not found."));
            }
        }
        else if (args[0].equalsIgnoreCase("deaths")) {
            if (args[1].equalsIgnoreCase("add")) {
                OfflinePlayer targ = Bukkit.getOfflinePlayer(args[2]);
                FactionUser target = HCFaction.getPlugin().getUserManager().getUser(targ.getUniqueId());
                Integer amount = Ints.tryParse(args[3]);
                if (amount == null) {
                    player.sendMessage(CC.translate("&c'" + args[1] + "' is not a valid number."));
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage(CC.translate("&cThe amount must be positive."));
                    return true;
                }
                target.setDeaths(target.getDeaths() + amount);
                player.sendMessage(CC.translate("&aAdded " + amount + " deaths to " + targ.getName() + " account."));
            }
            else if (args[1].equalsIgnoreCase("remove")) {
                OfflinePlayer targ = Bukkit.getOfflinePlayer(args[2]);
                FactionUser target = HCFaction.getPlugin().getUserManager().getUser(targ.getUniqueId());
                Integer amount = Ints.tryParse(args[3]);
                if (amount == null) {
                    player.sendMessage(CC.translate("&c'" + args[1] + "' is not a valid number."));
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage(CC.translate("&cThe amount must be positive."));
                    return true;
                }
                int deaths = target.getDeaths();
                if (deaths < amount) {
                    player.sendMessage(CC.translate("&c" + targ.getName() + " don't have " + amount + " deaths."));
                    return true;
                }
                target.setDeaths(target.getDeaths() - amount);
                player.sendMessage(CC.translate("&aRemoved " + amount + " deaths from " + targ.getName() + " account."));
            }
            else {
                player.sendMessage(CC.translate("&cStatsManager sub-command '" + args[1] + "' not found."));
            }
        }
        else {
            player.sendMessage(CC.translate("&cStatsManager sub-command '" + args[0] + "' not found."));
        }
        return true;
    }
    
    private void getUsage(CommandSender sender) {
        sender.sendMessage(CC.translate("&7&m------------------------------"));
        sender.sendMessage(CC.translate("&3&lStatsManager Command"));
        sender.sendMessage("");
        sender.sendMessage(CC.translate("&b/statsmanager kills <add/remove> <player> <amount>"));
        sender.sendMessage(CC.translate("&7Add or remove kills to/from player"));
        sender.sendMessage("");
        sender.sendMessage(CC.translate("&b/statsmanager deaths <add/remove> <player> <amount>"));
        sender.sendMessage(CC.translate("&7Add or remove deaths to/from player"));
        sender.sendMessage(CC.translate("&7&m------------------------------"));
    }
}
