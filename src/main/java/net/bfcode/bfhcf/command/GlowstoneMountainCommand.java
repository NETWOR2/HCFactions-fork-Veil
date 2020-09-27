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
import net.bfcode.bfhcf.mountain.GlowstoneFaction;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class GlowstoneMountainCommand implements CommandExecutor {
	
    public static String locToCords(Location location) {
        World.Environment environment = location.getWorld().getEnvironment();
        String world = environment == World.Environment.NETHER ? "Nether" : "Overworld";
        return world + ", " + location.getBlockX() + " | " + location.getBlockZ();
    }

    private HCFaction hcf;

    public GlowstoneMountainCommand(HCFaction hcf) {
        this.hcf = hcf;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        boolean permission = sender.hasPermission("hcf.command.glowstone");
        if (args.length == 0 || !permission) {
            Cuboid cuboid = hcf.getGlowstoneMountainManager().getCuboid();
            if (cuboid != null) {
                long now = System.currentTimeMillis();
                sender.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Glowstone Mountain");
                sender.sendMessage(ChatColor.YELLOW + " Location: " + ChatColor.GRAY + locToCords(cuboid.getCenter()));
                sender.sendMessage(ChatColor.YELLOW + " Time: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(hcf.getGlowstoneMountainManager().getLasttime() - now + hcf.getGlowstoneMountainManager().getTime(), true, true));
                sender.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            } else {
                sender.sendMessage(ChatColor.RED + "There is no glowstone mountain configured");
            }
        } else if (args[0].equalsIgnoreCase("setarea")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                    WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    Selection selection = worldEditPlugin.getSelection(player);
                    if (selection != null) {
                        HCFaction.getPlugin().getGlowstoneMountainManager().constructCuboid(selection);
                        GlowstoneFaction glowstoneFaction = (GlowstoneFaction) hcf.getFactionManager().getFaction("Glowstone");
                        glowstoneFaction.reload();
                        sender.sendMessage(ChatColor.RED + "Location successfully set to " + ChatColor.YELLOW + locToCords(hcf.getGlowstoneMountainManager().getCuboid().getCenter()));
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
            hcf.getGlowstoneMountainManager().setCuboid(null);
            GlowstoneFaction glowstoneFaction = (GlowstoneFaction) hcf.getFactionManager().getFaction("Glowstone");
            glowstoneFaction.reload();
            sender.sendMessage(ChatColor.RED + "Successfully removed glowstone mountain setting");
        } else if (args[0].equalsIgnoreCase("settime")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Please enter the time as a number in minutes");
            } else {
                Integer time = Ints.tryParse(args[1]);
                if (time == null) {
                    sender.sendMessage(ChatColor.RED + "Please enter the time as a number in minutes");
                } else {
                    hcf.getGlowstoneMountainManager().setTime(TimeUnit.MINUTES.toMillis(time));
                    sender.sendMessage(ChatColor.RED + "Reset time set to " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(TimeUnit.MINUTES.toMillis(time), true, true));
                }
            }
        } else if (args[0].equalsIgnoreCase("updateschematic")) {
            boolean done = hcf.getGlowstoneMountainManager().updateSelection();
            if (done) {
                sender.sendMessage(ChatColor.YELLOW + "Schematic updated!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to setup schematic, check console for errors");
            }
        } else if (args[0].equalsIgnoreCase("reset")) {
            hcf.getGlowstoneMountainManager().setLasttime(0);
            sender.sendMessage(ChatColor.RED + "Time reset, glowstone mountain will now construct");
        } else {
            sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Glowstone Mountain");
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
        return ChatColor.YELLOW + "/glowstone" + s1 + ChatColor.GRAY + " (" + s + ")";
    }
}
