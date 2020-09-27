package net.bfcode.bfhcf.wrench;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class WrenchGiveCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player)sender;
        if (!cmd.getName().equalsIgnoreCase("crowgive")) {
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /wrenchgive <playerName>");
            return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatColor.RED + "That player is offline.");
            return true;
        }
        ItemStack stack = new Wrench().getItemIfPresent();
        target.getInventory().addItem(new ItemStack[] { stack });
        target.sendMessage(ChatColor.GREEN + "You were given a " + ChatColor.RED + "Wrench" + ChatColor.GREEN + " from " + sender.getName() + ".");
        p.sendMessage(ChatColor.GREEN + "You have given " + target.getName() + " a " + ChatColor.RED + "Wrench" + ChatColor.GREEN + ".");
        return true;
    }
}
