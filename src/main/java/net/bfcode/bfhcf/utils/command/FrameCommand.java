package net.bfcode.bfhcf.utils.command;

import org.bukkit.command.*;
import org.bukkit.*;
import org.apache.commons.lang.*;
import com.google.common.collect.*;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

import java.util.*;

public abstract class FrameCommand implements CommandExecutor, TabCompleter
{
    private String name;
    private String description;
    private String[] aliases;
    private List<CommandArgument> arguments;
    public HCFaction plugin;
    
    public FrameCommand(String name) {
        this(name, null);
    }
    
    public FrameCommand(String name, String description) {
        this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }
    
    public FrameCommand(String name, String description, String... aliases) {
        this.arguments = new ArrayList<CommandArgument>();
        this.plugin = HCFaction.getPlugin();
        this.name = name;
        this.description = description;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(CC.translate("&6&m" + StringUtils.repeat("-", 50)));
            sender.sendMessage(CC.translate("&aAvailable sub-command(s) for " + command.getName() + '.'));
            sender.sendMessage("");
            for (CommandArgument arguments : this.arguments) {
                if (arguments.getPermission() != null && !sender.hasPermission(arguments.getPermission())) {
                    continue;
                }
                sender.sendMessage(CC.translate("&e" + arguments.getUsage(label) + " &7- &d" + arguments.getDescription()));
            }
            sender.sendMessage(CC.translate("&6&m" + StringUtils.repeat("-", 50)));
            return true;
        }
        CommandArgument argument = this.getArgument(args[0]);
        String permission = (argument == null) ? null : argument.getPermission((PluginCommand)command);
        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(this.name) + " sub-command '" + args[0] + "' not found.");
        }
        else {
            argument.onCommand(sender, command, label, args);
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<String>();
        if (args.length < 2) {
            for (CommandArgument argument : this.arguments) {
                String permission = argument.getPermission((PluginCommand)command);
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }
            if (results.isEmpty()) {
                return null;
            }
        }
        else {
            CommandArgument argument2 = this.getArgument(args[0]);
            if (argument2 == null) {
                return results;
            }
            String permission2 = argument2.getPermission((PluginCommand)command);
            if (permission2 == null || sender.hasPermission(permission2)) {
                results = argument2.onTabComplete(sender, command, label, args);
                if (results == null) {
                    return null;
                }
            }
        }
        return Utils.getCompletions(args, results);
    }
    
    public List<CommandArgument> getArguments() {
        return (List<CommandArgument>)ImmutableList.copyOf((Collection)this.arguments);
    }
    
    public void addArgument(CommandArgument argument) {
        this.arguments.add(argument);
    }
    
    public void removeArgument(CommandArgument argument) {
        this.arguments.remove(argument);
    }
    
    public CommandArgument getArgument(String name) {
        return this.arguments.stream().filter(argument -> argument.getName().equalsIgnoreCase(name) || Arrays.asList(argument.getAliases()).contains(name.toLowerCase())).findFirst().orElse(null);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
    
    public String getDescription() {
        return this.description;
    }
}
