package net.bfcode.bfhcf.command.reclaim;

import java.util.Set;

import dev.hatsur.library.Library;
import net.bfcode.bfbase.util.Config;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

public class Reclaim implements ReclaimManager {
	private HCFaction plugin;
	private Config config;

	public Reclaim(HCFaction plugin) {
		this.plugin = plugin;
		this.config = new Config(plugin, "reclaim");
	}

	public Set<String> getGroup() {
		if (this.config.get("Reclaim") != null) {
			Object object = this.config.get("Reclaim");
			if (object instanceof MemorySection) {
				MemorySection section = (MemorySection) object;
				Set<String> keys = section.getKeys(false);
				return keys;
			}
		}
		return null;
	}

	public void getReclaim(Player player) {
		FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());

		if (user.isReclaimed() == true) {
			player.sendMessage(ChatColor.RED + "You have already reclaimed this map.");
			return;
		}

		String cmdsString = null;

		for (String id : getGroup()) {
			if (player.hasPermission("command.reclaim." + id)) {
				cmdsString = id;
			}
		}

		if (cmdsString == null) {
			player.sendMessage(ChatColor.RED + "Nothing to reclaim.");
			return;
		}

		for (String cmds : this.config.getStringList("Reclaim." + cmdsString + ".commands")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replaceAll("%player%", Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getColoredUsername()));
			user.setReclaimed(true);
		}
	}
}
