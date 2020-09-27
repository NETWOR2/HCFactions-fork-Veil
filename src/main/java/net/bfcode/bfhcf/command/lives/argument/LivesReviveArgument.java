package net.bfcode.bfhcf.command.lives.argument;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class LivesReviveArgument extends CommandArgument {
    private HCFaction plugin;
    
    public LivesReviveArgument(HCFaction plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)plugin, "BungeeCord");
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
    	if(ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in HCFaction."));
    		return true;
    	}
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found.");
            return true;
        }
        EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        if(eotwRunnable != null) {
        	sender.sendMessage(CC.translate("&cYou are not use this command in EOTW."));
        	return true;
        }
        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission("hcf.revive.bypass") && this.plugin.getEotwHandler().isEndOfTheWorld()) {
                sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
                return true;
            }
            if (!sender.hasPermission("hcf.revive.bypass")) {
                Player player = (Player)sender;
                UUID playerUUID = player.getUniqueId();
                int selfLives = this.plugin.getDeathbanManager().getLives(playerUUID);
                if (selfLives <= 0) {
                    sender.sendMessage(ChatColor.RED + "You do not have any lives.");
                    return true;
                }
                this.plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
                @SuppressWarnings("deprecation")
				PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
                sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
            else if (sender.hasPermission("hcf.revive.dtr")) {
                if (this.plugin.getFactionManager().getPlayerFaction(targetUUID) != null) {
                    this.plugin.getFactionManager().getPlayerFaction(targetUUID).setDeathsUntilRaidable(this.plugin.getFactionManager().getPlayerFaction(targetUUID).getDeathsUntilRaidable() + 1.0);
                }
                sender.sendMessage(ChatColor.YELLOW + "You have revived and added DTR to " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
            else {
                sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
        }
        else {
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
        }
        factionTarget.removeDeathban();
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<String>();
        Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
        for (FactionUser factionUser : factionUsers) {
            Deathban deathban = factionUser.getDeathban();
            if (deathban != null && deathban.isActive()) {
                OfflinePlayer offlinePlayer;
                @SuppressWarnings("unused")
				String offlineName;
                if ((offlineName = (offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID())).getName()) == null) {
                    continue;
                }
                results.add(offlinePlayer.getName());
            }
        }
        return results;
    }
}
