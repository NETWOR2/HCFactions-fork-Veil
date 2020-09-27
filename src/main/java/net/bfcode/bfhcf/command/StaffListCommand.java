package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
        List<String> staff = this.getStaff(p);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lStaff Conectados &7(&f" + staff.size() + "&7)"));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7[&9Staff&7] " + (staff.isEmpty() ? "None" : staff.toString().replace("[", "").replace("]", ""))));
        return true;
	}
	
	private List<String> getStaff(Player player) {
        List<String> players = new ArrayList<String>();
        List<Player> online = new ArrayList<Player>();
        for(Player players1 : Bukkit.getOnlinePlayers()) {
        	online.add(players1);
        }
        for (Player entity : online) {
            Player target = (Player) entity;
            if (!target.hasPermission("rank.staff")) {
                continue;
            }
            players.add(target.getName());
        }
        return players;
    }
}
