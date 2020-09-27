package net.bfcode.bfhcf.command;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import java.util.Collections;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.type.SotwTimer;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

public class SotwCommand implements CommandExecutor, TabCompleter {
	
    private static List<String> COMPLETIONS;
    private HCFaction plugin;
    
    public SotwCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
            	if(!sender.hasPermission("hcf.command.sotw.argument.start") || !sender.isOp()) {
            		sender.sendMessage(CC.translate("&cYou do not have permissions to use this command"));
            		return true;
            	}
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " start h:m:s ");
                    sender.sendMessage(ChatColor.RED + "Example: /sotw start 3h3m");
                    return true;
                }
                long duration = JavaUtils.parse(args[1]);
                if (duration == -1L) {
                    sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                    return true;
                }
                if (duration < 1000L) {
                    sender.sendMessage(ChatColor.RED + "SOTW protection time must last for at least 20 ticks.");
                    return true;
                }
                SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
                if (sotwRunnable != null) {
                    sender.sendMessage(ChatColor.RED + "SOTW protection is already enabled, use /" + label + " cancel to end it.");
                    return true;
                }
                this.plugin.getSotwTimer().start(duration);
                Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
                Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GREEN + "\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588" + ChatColor.GREEN + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.YELLOW + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
                Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
                return true;
            }
            else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
            	if(!sender.hasPermission("hcf.command.sotw.argument.cancel") || !sender.isOp()) {
            		sender.sendMessage(CC.translate("&cYou do not have permissions to use this command"));
            		return true;
            	}
                if (this.plugin.getSotwTimer().cancel()) {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588");
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
                    Bukkit.broadcastMessage(ChatColor.RESET + "");
                    Bukkit.broadcastMessage(ChatColor.RED + "You are no longer Invincible");
                    return true;
                }
                sender.sendMessage(ConfigurationService.SOTW_NOT_ACTIVE);
                return true;
            }
            else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("pvp")) {
				if (HCFaction.getPlugin().getSotwTimer().getSotwRunnable() == null) {
					sender.sendMessage(ChatColor.RED + "The SOTW isn't enabled.");
					return true;
				}
				if (HCFaction.getPlugin().getSotwTimer().getSotwRunnable().isSotwEnabled((Player) sender)) {
					sender.sendMessage(ChatColor.RED + "You have already enabled your SOTW enable.");
					return true;
				}
				if(HCFaction.getPlugin().getEotwHandler().isEndOfTheWorld()) {
					sender.sendMessage(CC.translate("&cYou cant execute this command in EOTW"));
					return true;
				}
				HCFaction.getPlugin().getSotwTimer().enabledSotw.add(((Player) sender).getUniqueId());
				sender.sendMessage(CC.translate("&aYou have enabled your sotw timer, now you can fight other players!"));
            	return true;
            }
        }
        sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(CC.translate("&3&lSOTW Commands"));
        sender.sendMessage(CC.translate(""));
        if(sender.isOp() || sender.hasPermission("hcf.command.sotw.argument.start") || sender.hasPermission("hcf.command.sotw.argument.cancel")) {
            sender.sendMessage(CC.translate("&7- &b/sotw start 3h30m &7(format is hr:ms:s)"));
            sender.sendMessage(CC.translate("&7- &b/sotw cancel|end"));
        }
        sender.sendMessage(CC.translate("&7- &b/sotw enable|pvp"));
        sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)SotwCommand.COMPLETIONS) : Collections.emptyList();
    }
    
    static {
        COMPLETIONS = ImmutableList.of("start", "end", "cancel", "enable", "pvp");
    }
}
