package net.bfcode.bfhcf.wrench;

import java.util.Collections;

import com.google.common.base.Optional;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class WrenchCommand implements CommandExecutor, TabCompleter
{
    private List<String> completions;
    
    public WrenchCommand() {
        this.completions = Arrays.asList("spawn", "setspawners", "setendframes");
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }
        Player player = (Player)sender;
        if (args[0].equalsIgnoreCase("spawn")) {
            ItemStack stack = new Wrench().getItemIfPresent();
            player.getInventory().addItem(new ItemStack[] { stack });
            sender.sendMessage(ChatColor.YELLOW + "You have given yourself a " + stack.getItemMeta().getDisplayName() + ChatColor.YELLOW + '.');
            return true;
        }
        Optional<Wrench> crowbarOptional = Wrench.fromStack(player.getItemInHand());
        if (!crowbarOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "You are not holding a Wrench.");
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawners")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            Integer amount = JavaUtil.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Wrench have maximum Spawner uses of " + 1 + '.');
                return true;
            }
            Wrench wrench = (Wrench)crowbarOptional.get();
            wrench.setSpawnerUses(amount);
            player.setItemInHand(wrench.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set Spawner uses of held Wrench to " + amount + '.');
            return true;
        }
        else {
            if (!args[0].equalsIgnoreCase("setendframes")) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            Integer amount = JavaUtil.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 6) {
                sender.sendMessage(ChatColor.RED + "Wrench have maximum End Frame uses of " + 1 + '.');
                return true;
            }
            Wrench wrench = (Wrench)crowbarOptional.get();
            wrench.setEndFrameUses(amount);
            player.setItemInHand(wrench.getItemIfPresent());
            sender.sendMessage(ChatColor.GREEN + "Set End Frame uses of held Wrench to " + amount + '.');
            return true;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)this.completions) : Collections.emptyList();
    }
}
