package net.bfcode.bfhcf.command.tournament.argument;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentState;
import net.bfcode.bfhcf.tournaments.TournamentType;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentStatusArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public TournamentStatusArgument() {
        super("status", "Status of a tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.status";
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Tournament tournament = this.plugin.getTournamentManager().getTournament();
        if(tournament.getPlayers().size() == 0) {
        	sender.sendMessage(CC.translate("&cDo not have events running."));
        }
        if(tournament.getTournamentState() == TournamentState.FIGHTING) {
        	sender.sendMessage(CC.translate("&7&m----------------------------------------"));
            sender.sendMessage(CC.translate("&6Type&7: &f" + WordUtils.capitalizeFully(tournament.getType().name())));
            sender.sendMessage(CC.translate("&6Round&7: &f" + tournament.getCurrentRound()));
            if (tournament.getType() == TournamentType.SUMO) {
                sender.sendMessage(CC.translate("&6Current Fight:"));
                sender.sendMessage(CC.translate("   &2" + tournament.getFirstPlayer().getName() + " &avs &2" + tournament.getSecondPlayer().getName()));
            }
            sender.sendMessage(CC.translate("&6Next Round&7: &f" + (tournament.getCurrentRound() + 1)));
            sender.sendMessage(CC.translate("&6Players&7: &f" + tournament.getPlayers().size() + "&7/&f" + tournament.getSize()));
            sender.sendMessage(CC.translate("&6Hoster&7: &f" + tournament.getHoster().getName()));
        	sender.sendMessage(CC.translate("&7&m----------------------------------------"));
        }
        else if(tournament.getTournamentState() == TournamentState.STARTING) {
        	sender.sendMessage(CC.translate("&7&m----------------------------------------"));
        	sender.sendMessage(CC.translate("&6The event is Starting..."));
        	sender.sendMessage("");
        	sender.sendMessage(CC.translate("&6Event&7: &7(&e" + tournament.getType().toString() + "&7)"));
        	sender.sendMessage(CC.translate("&6Players&7: &7" + tournament.getPlayers().size() + "/" + tournament.getSize()));
        	sender.sendMessage(CC.translate("&7&m----------------------------------------"));
        }
        return true;
    }
}
