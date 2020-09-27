package net.bfcode.bfhcf.command.tournament.argument;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentCancelArgument extends CommandArgument {
	
    private HCFaction plugin;
    
    public TournamentCancelArgument() {
        super("cancel", "Cancel the current tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.cancel";
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou must be player to execute this command."));
            return true;
        }
        Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: " + this.getUsage(label)));
        }
        if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in SOTW"));
        	return true;
        }
        else {
            if (!this.plugin.getTournamentManager().isCreated()) {
                player.sendMessage(CC.translate("&cThis event doesnt exist!"));
                return true;
            }
            if (this.plugin.getTournamentManager().getTournament().getHoster() != player) {
                player.sendMessage(CC.translate("&cYou need a hoster por execute this command!"));
                return true;
            }
            Tournament tournament = this.plugin.getTournamentManager().getTournament();
            if (tournament != null) {
                this.plugin.getTournamentManager().setCreated(false);
                for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                    if (this.plugin.getTournamentManager().isInTournament(online.getUniqueId())) {
                        tournament.rollbackInventory(online);
                        this.plugin.getTournamentManager().kickPlayer(online.getUniqueId());
                        online.sendMessage(CC.translate("&cYou have been kicked for this event."));
                        online.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    }
                }
            }
            else {
                player.sendMessage(CC.translate("&cThis event doesnt exist!"));
            }
        }
        return true;
    }
}
