package net.bfcode.bfhcf.command.tournament.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Messager;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentLeaveArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public TournamentLeaveArgument() {
        super("leave", "Leave from the tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.leave";
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou must be player to execute this command."));
            return true;
        }
        if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
        	sender.sendMessage(CC.translate("&cYou cant execute this command in SOTW"));
        	return true;
        }
        Player player = (Player)sender;
        Tournament tournament = this.plugin.getTournamentManager().getTournament();
        if (tournament != null) {
            if (!this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cNo estas participando en ningún evento!"));
            }
            else if (tournament.getHoster().getName().equalsIgnoreCase(player.getName())) {
                Messager.player(player, "&cNo puedes salirte de tu propio evento!");
            }else{
            	this.plugin.getTournamentManager().leaveTournament(player);
            }
        }
        else {
            player.sendMessage(CC.translate("&cEste evento no existe!"));
        }
        return true;
    }
}
