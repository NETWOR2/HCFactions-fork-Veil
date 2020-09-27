package net.bfcode.bfhcf.command;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.command.CommandExecutor;

public class FFACommand implements CommandExecutor {
	
	public static boolean mode;
	private static BukkitTask runnable;
	
	static {
		mode = false;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(!ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in KitMap."));
    		return true;
    	}
		
    	if(mode == false) {
    		mode = true;
    		runnable = new BukkitRunnable() {
    			
    			@Override
    			public void run() {
    				for(Player players : Bukkit.getOnlinePlayers()) {
    					if(!players.hasPermission("rank.staff")) {
    						players.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    						players.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    						players.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
    					}
    				}
    			}
    		}.runTaskTimer(HCFaction.getPlugin(), 0L, 600L);
    		sender.sendMessage(CC.translate("&aFFA Mode activated!"));
    	} else {
    		mode = false;
    		sender.sendMessage(CC.translate("&cFFA Mode disabled!"));
    		runnable.cancel();
    		for(Player players : Bukkit.getOnlinePlayers()) {
    			if(!players.hasPermission("rank.staff")) {
        			players.removePotionEffect(PotionEffectType.SPEED);
        			players.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        			players.removePotionEffect(PotionEffectType.INVISIBILITY);
    			}
    		}
    	}
    	return true;
    }
	
}
