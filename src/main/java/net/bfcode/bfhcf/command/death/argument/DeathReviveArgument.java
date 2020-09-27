package net.bfcode.bfhcf.command.death.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.user.FactionUser;

public class DeathReviveArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public DeathReviveArgument(HCFaction plugin) {
        super("revive", "Revive a deathbanned player");
        this.plugin = plugin;
        this.permission = "hcf.command.death.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return true;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = HCFaction.getPlugin().getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + "Player is not deathbanned.");
            return true;
        }
        factionTarget.removeDeathban();
        Command.broadcastCommandMessage(sender, ChatColor.translateAlternateColorCodes('&', "&eYou have revived " + target.getName() + "."));
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
