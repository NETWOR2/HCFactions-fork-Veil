package net.bfcode.bfhcf.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.BukkitUtils;

import org.bukkit.command.CommandExecutor;

public class OresCommand implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lOre Statistics"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPlayer&7: &r" + target.getName()));
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.GREEN + "Emerald Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
        sender.sendMessage(ChatColor.AQUA + "Diamonds Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
        sender.sendMessage(ChatColor.GOLD + "Gold Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
        sender.sendMessage(ChatColor.RED + "Redstone Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
        sender.sendMessage(ChatColor.BLUE + "Lapis Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
        sender.sendMessage(ChatColor.GRAY + "Iron Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}
