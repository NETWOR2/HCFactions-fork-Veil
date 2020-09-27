package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.google.common.base.Optional;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.GuavaCompat;
import net.bfcode.bfhcf.utils.JavaUtil;

public class SetBorderCommand implements CommandExecutor, TabCompleter
{
    private static int MIN_SET_SIZE = 50;
    private static int MAX_SET_SIZE = 25000;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <worldType> <amount>");
            return true;
        }
		Optional<World.Environment> optional = (Optional<World.Environment>)GuavaCompat.getIfPresent((Class)World.Environment.class, args[0]);
        if (!optional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Environment '" + args[0] + "' not found.");
            return true;
        }
        Integer amount = JavaUtil.tryParseInt(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if (amount < MIN_SET_SIZE) {
            sender.sendMessage(ChatColor.RED + "Minimum border size is " + MIN_SET_SIZE + 100 + '.');
            return true;
        }
        if (amount > MAX_SET_SIZE) {
            sender.sendMessage(ChatColor.RED + "Maximum border size is " + MAX_SET_SIZE + '.');
            return true;
        }
        World.Environment environment = (World.Environment)optional.get();
        HCFaction.getPlugin().getServerHandler().setServerBorder(environment, amount);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set border size of environment " + environment.name() + " to " + amount + '.');
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        World.Environment[] values = World.Environment.values();
        List<String> results = new ArrayList<String>(values.length);
        for (World.Environment environment : values) {
            results.add(environment.name());
        }
        return (List<String>)BukkitUtils.getCompletions(args, (List)results);
    }
}
