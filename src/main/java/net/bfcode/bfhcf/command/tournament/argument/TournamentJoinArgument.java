package net.bfcode.bfhcf.command.tournament.argument;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfbase.listener.VanishListener;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentState;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Messager;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentJoinArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public TournamentJoinArgument() {
        super("join", "Join to a tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.join";
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
        Tournament tournament = this.plugin.getTournamentManager().getTournament();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        for(ItemStack item : player.getInventory().getContents()) {
		    if (item != null) {
		    	player.sendMessage(CC.translate("&cYou inventory is void for join event!"));
		    	return true;
		    }
		}
        if(factionAt instanceof EventFaction) {
        	sender.sendMessage(CC.translate("&cYou only join this event in Spawn!"));
        	return false;
        }
        if(tournament == null) {
        	return true;
        }

        int countdown = this.plugin.getTournamentManager().getTournament().getDesecrentAnn();
        if(StaffModeCommand.isMod(player) == true || VanishListener.isVanished(player) ==  true) {
        	Messager.player(player, "&cYou cant join the event in StaffMode!");
        	return true;
        }
        if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in SOTW"));
        	return true;
        }
        if (tournament != null) {
            if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                player.sendMessage(CC.translate("&cNo puedes entrar al evento con Combat-Tag!"));
            }
            else if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player) > 0L) {
            	player.sendMessage(CC.translate("&cYou cant join with PvPTimer enable!"));
            }
            else if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cYou already on event!"));
            }
            else if(tournament.getPlayers().size() == tournament.getSize()) {
            	player.sendMessage(CC.translate("&cThis event is full!"));
            }
            else if(countdown <= 0) {
            	player.sendMessage(CC.translate("&cThe event already started!"));
            }
            else if (tournament.getTournamentState() == TournamentState.FIGHTING) {
                player.sendMessage(CC.translate("&cThe event already started!"));
            }
            else {
                this.plugin.getTournamentManager().joinTournament(player);
                tournament.saveInventory(player);
                if (player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }
                if (player.isFlying()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
                tournament.teleport(player, "Spawn");
                tournament.giveItemWait(player);
            }
        }
     
        return true;
    }
}
