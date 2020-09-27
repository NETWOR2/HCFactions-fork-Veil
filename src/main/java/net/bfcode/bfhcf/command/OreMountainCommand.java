package net.bfcode.bfhcf.command;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.mountain.OreFaction;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class OreMountainCommand implements CommandExecutor {
    public static String locToCords(Location location) {
        World.Environment environment = location.getWorld().getEnvironment();
        String world = environment == World.Environment.NETHER ? "Nether" : "Overworld";
        return world + ", " + location.getBlockX() + " | " + location.getBlockZ();
    }

    private HCFaction hcf;

    public OreMountainCommand(HCFaction hcf) {
        this.hcf = hcf;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        boolean permission = sender.hasPermission("hcf.command." + cmd.getName());
        if (args.length == 0 || !permission) {
            Cuboid cuboid = hcf.getOreMountainManager().getCuboid();
            if (cuboid != null) {
                long now = System.currentTimeMillis();
                sender.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                sender.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "Ore Mountain");
                sender.sendMessage(ChatColor.GRAY + " Location: " + ChatColor.GRAY + locToCords(cuboid.getCenter()));
                sender.sendMessage(ChatColor.GRAY + " Time: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(hcf.getOreMountainManager().getLasttime() - now + hcf.getOreMountainManager().getTime(), true, true));
                sender.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            } else {
                sender.sendMessage(ChatColor.RED + "There is no ore mountain configured");
            }
        } else if (args[0].equalsIgnoreCase("setarea")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                    WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    Selection selection = worldEditPlugin.getSelection(player);
                    if (selection != null) {
                        hcf.getOreMountainManager().constructCuboid(selection);
                        OreFaction oreFaction = (OreFaction) hcf.getFactionManager().getFaction("OreFaction");
                        oreFaction.reload();
                        sender.sendMessage(ChatColor.RED + "Location successfully set to " + ChatColor.GRAY + locToCords(hcf.getOreMountainManager().getCuboid().getCenter()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have a worldedit selection");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "WorldEdit is not enabled");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to do this");
            }
        } else if (args[0].equalsIgnoreCase("removearea")) {
            hcf.getOreMountainManager().setCuboid(null);
            OreFaction oreFaction = (OreFaction) hcf.getFactionManager().getFaction("OreFaction");
            oreFaction.reload();
            sender.sendMessage(ChatColor.RED + "Successfully removed ore mountain setting");
        } else if (args[0].equalsIgnoreCase("settime")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Please enter the time as a number in minutes");
            } else {
                Integer time = Ints.tryParse(args[1]);
                if (time == null) {
                    sender.sendMessage(ChatColor.RED + "Please enter the time as a number in minutes");
                } else {
                    hcf.getOreMountainManager().setTime(TimeUnit.MINUTES.toMillis(time));
                    sender.sendMessage(ChatColor.RED + "Reset time set to " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(TimeUnit.MINUTES.toMillis(time), true, true));
                }
            }
        } else if (args[0].equalsIgnoreCase("updateschematic")) {
            boolean done = hcf.getOreMountainManager().updateSelection();
            if (done) {
                sender.sendMessage(ChatColor.GRAY + "Schematic updated!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to setup schematic, check console for errors");
            }
        } else if (args[0].equalsIgnoreCase("reset")) {
            hcf.getOreMountainManager().setLasttime(0);
            sender.sendMessage(ChatColor.RED + "Time reset, the ore mountain will now construct");
        } else {
            sender.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "Ore Mountain");
            sender.sendMessage(help("", "Displays information"));
            sender.sendMessage(help(" help", "Command help"));
            sender.sendMessage(help(" setarea", "Sets the cuboid for the glowstone mountain to set"));
            sender.sendMessage(help(" removearea", "Disables the glowstone mountain"));
            sender.sendMessage(help(" settime", "Sets the reset time for the mountain"));
            sender.sendMessage(help(" reset", "Sets the glowstone mountain"));
            sender.sendMessage(help(" updateschematic", "Updates the schematic to the one in the file system"));
        }
        return true;
    }

    public String help(String s1, String s) {
        return ChatColor.GRAY + "/oremountain" + s1 + ChatColor.AQUA + " (" + s + ")";
    }
}
