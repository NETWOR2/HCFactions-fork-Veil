package net.bfcode.bfhcf.command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FightCommand implements CommandExecutor
{
    private static long FIGHT_COOLDOWN_DELAY;
    public static TObjectLongMap<UUID> FIGHT_COOLDOWN;
    
    static {
        FIGHT_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(10L);
        FIGHT_COOLDOWN = (TObjectLongMap)new TObjectLongHashMap();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No Console");
            return true;
        }
        Player player = (Player)sender;
	    int x = player.getLocation().getBlockX();
	    int y = player.getLocation().getBlockY();
	    int z = player.getLocation().getBlockZ();
	    
        if (command.getName().equalsIgnoreCase("fight")) {
            if (!player.hasPermission("hcf.command.fight")) {
                sender.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
                return true;
            }
            if (args.length == 0) {
                    UUID uuid = player.getUniqueId();
                    long timestamp = FightCommand.FIGHT_COOLDOWN.get(uuid);
                    long millis = System.currentTimeMillis();
                    long remaining = (timestamp == FightCommand.FIGHT_COOLDOWN.getNoEntryValue()) ? -1L : (timestamp - millis);
                    if (remaining > 0L) {
                        player.sendMessage(ChatColor.RED + "You cannot use this command for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
                        return true;
                    }                  
	                  Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
	                  Bukkit.broadcastMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + " " + sender.getName() + ChatColor.AQUA + " is looking for a fight at §7(" + x + ", " + y + ", " + z + ")");
	                  Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
                  }
                  player.sendMessage(ChatColor.YELLOW + "You have announced that you are looking for a fight, you must wait 10 minutes before doing this again.");
                  FightCommand.FIGHT_COOLDOWN.put((UUID)player.getUniqueId(), System.currentTimeMillis() + FightCommand.FIGHT_COOLDOWN_DELAY);
                }
        return true;
    }
}