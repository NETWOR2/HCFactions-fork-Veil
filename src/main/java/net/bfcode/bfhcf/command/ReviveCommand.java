package net.bfcode.bfhcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Cooldowns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ReviveCommand implements CommandExecutor
{
    private HCFaction plugin;
    
    public ReviveCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;
        if (ConfigurationService.KIT_MAP) {
            sender.sendMessage(ChatColor.RED + "To use this command you must be in some HCF's");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }
        if (Cooldowns.isOnCooldown("REVIVE_COOLDOWN", p)) {
            p.sendMessage("§cNo puedes revivir a usuaris durante §l" + Cooldowns.getCooldownForPlayerInt("REVIVE_COOLDOWN", p) / 60 + " §cminutos.");
            return true;
        }
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = HCFaction.getPlugin().getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " no tiene deathban.");
            return true;
        }
        factionTarget.removeDeathban();
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§7[§aRevive§7] §a" + sender.getName() + " §fha sido revivido §a" + target.getName());
        Bukkit.broadcastMessage(ConfigurationService.REVIVE_MESSAGE.replace("%store%", ConfigurationService.STORE));
        Bukkit.broadcastMessage(" ");
        Cooldowns.addCooldown("REVIVE_COOLDOWN", p, 1800);
        return false;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        List<String> results = new ArrayList<String>();
        for (FactionUser factionUser : this.plugin.getUserManager().getUsers().values()) {
            Deathban deathban = factionUser.getDeathban();
            if (deathban != null && deathban.isActive()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                String name = offlinePlayer.getName();
                if (name == null) {
                    continue;
                }
                results.add(name);
            }
        }
        return (List<String>)BukkitUtils.getCompletions(args, (List)results);
    }
}
