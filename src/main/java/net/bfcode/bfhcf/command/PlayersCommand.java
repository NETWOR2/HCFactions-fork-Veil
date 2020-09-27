package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

public class PlayersCommand implements CommandExecutor, Listener {
	
	private static boolean status = false;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		Player player = (Player) sender;
		if(args.length < 1) {
			sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			sender.sendMessage(CC.translate("&6&lPlease use:"));
			sender.sendMessage(CC.translate(""));
			sender.sendMessage(CC.translate(" &7- &e/players hide"));
			sender.sendMessage(CC.translate(" &7- &e/players show"));
			sender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			return true;
		}
		if(args[0].equalsIgnoreCase("hide")) {
			List<Player> test = new ArrayList<Player>();
			for(Player players : Bukkit.getOnlinePlayers()) {
				test.add(players);
			}
	        for(int i = 0; i < test.size(); i++) {
	            Player target = test.get(i);
				for(Player players : Bukkit.getOnlinePlayers()) {
					PlayerFaction members = HCFaction.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId());
					PlayerFaction othermembers = HCFaction.getPlugin().getFactionManager().getPlayerFaction(players.getUniqueId());
					target.hidePlayer(players);
					if(members == othermembers) {
						target.showPlayer(players);
					}
				}
	        }
	        player.sendMessage(CC.translate("&cAll players hide."));
			status = true;
			return true;
		}
		else if(args[0].equalsIgnoreCase("show")) {
			List<Player> test = new ArrayList<Player>();
			for(Player players : Bukkit.getOnlinePlayers()) {
				test.add(players);
			}
	        for(int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
	            Player target = test.get(i);
				for(Player players : Bukkit.getOnlinePlayers()) {
					target.showPlayer(players);
				}
	        }
			player.sendMessage(CC.translate("&aAll players show."));
			status = false;
			return true;
		}
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(status == true) {
			Player player = event.getPlayer();
			for(Player players : Bukkit.getOnlinePlayers()) {
				PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
				PlayerFaction targetFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(players.getUniqueId());
				player.hidePlayer(players);
				players.hidePlayer(player);
				if(playerFaction == targetFaction) {
					player.showPlayer(players);
					players.showPlayer(player);
				}
			}
		}
	}
	
//	@EventHandler
//	public void onFactionJoin(Faction event) {
//		if(status == true) {
//			Player player = event.getPlayer();
//			PlayerFaction playerFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player);
//			for(Player players : Bukkit.getOnlinePlayers()) {
//				PlayerFaction targetFaction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(players);
//				if(playerFaction == targetFaction) {
//					players.showPlayer(player);
//					player.showPlayer(players);
//				}
//			}
//		}
//    }
}
