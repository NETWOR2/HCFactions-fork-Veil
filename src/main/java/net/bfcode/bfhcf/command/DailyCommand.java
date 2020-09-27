package net.bfcode.bfhcf.command;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dev.hatsur.library.Library;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.config.DailyFile;
import net.bfcode.bfhcf.config.UserDataFile;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class DailyCommand implements CommandExecutor {
	
    public static HashMap<String, BukkitRunnable> COOLDOWN_TASK;
    public static long HOUR;
	
    static {
        HOUR = TimeUnit.HOURS.toMillis(1L);
    }
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CC.translate("&cNo console."));
			return true;
		}
		Player player = (Player) sender;
		UserDataFile user = UserDataFile.getConfig();
		DailyFile daily = DailyFile.getConfig();
		if (args.length <= 0) {
			String cmdsString = null;
		    for (String id : getGroup()) {
		    	if (player.hasPermission("command.reclaim." + id)) {
		    		cmdsString = id;
		      	}
		    }
			if (user.get("users." + player.getName()) != null) {
	            long remainingTicks = user.getInt("users." + player.getName() + ".time") * 20;
	            long remainingMillis = remainingTicks * 50L;
	            player.sendMessage(CC.translate(daily.getString("messages.already_reclaim")
	            	.replace("%duration%", DurationFormatUtils.formatDuration(remainingMillis, ((remainingMillis >= HOUR) ? "HH:" : "") + "mm:ss"))));
	            return true;
	        }
			if(cmdsString == null) {
		        player.sendMessage(CC.translate(daily.getString("messages.no_rank").replace("%store%", ConfigurationService.STORE)));
		        return true;
			}
			for (String cmds : daily.getStringList("reclaim." + cmdsString + ".commands")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replaceAll("%player%", Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getColoredUsername()));
			}
			user.set("users." + player.getName() + ".time", daily.getInt("reclaim_cooldown"));
		    COOLDOWN_TASK.put(player.getName(), new BukkitRunnable() {
		        public void run() {
		        	user.set("users." + player.getName() + ".time", user.getInt("users." + player.getName() + ".time") - 1);
	                if (user.getInt("users." + player.getName() + ".time") == 0) {
	                	user.set("users." + player.getName(), null);
	                    COOLDOWN_TASK.remove(player.getName());
	                    cancel();
	                }
		        }
			});
		    
		    COOLDOWN_TASK.get(player.getName()).runTaskTimer(HCFaction.getPlugin(), 20, 20);
			user.save();
			user.reload();
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			if (!player.hasPermission("hcf.command.daily.argument.reload")) {
				player.sendMessage(CC.translate("&cYou don't have permission to execute this command."));
				return true;
			}
			daily.reload();
			player.sendMessage(CC.translate("&aDaily configuration has been successfully reloaded."));
		}
		else {
			player.sendMessage(CC.translate("&cDaily sub-command '" + args[0] + "' don't exist."));
		}
		return true;
	}
	
	public static Set<String> getGroup() {
		DailyFile daily = DailyFile.getConfig();
		if (daily.get("reclaim") != null) {
	      Object object = daily.get("reclaim");
	      if (object instanceof MemorySection) {
	    	  MemorySection section = (MemorySection)object;
	    	  Set<String> keys = section.getKeys(false);
	    	  return keys;  
	      }
		}
	    return null;
	}
	
	static {
		DailyCommand.COOLDOWN_TASK = new HashMap<String, BukkitRunnable>();
	}
}
