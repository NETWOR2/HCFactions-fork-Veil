package net.bfcode.bfhcf.command.tournament.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.file.TournamentFile;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.LocationUtils;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentSetArgument extends CommandArgument
{
    private TournamentFile file; 
    
    public TournamentSetArgument() {
        super("set", "Set locations of the tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.set";
        this.file = TournamentFile.getConfig();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <spawn|sumo|ffa|axe|archer|spleef|tnttag>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou must be player to execute this command."));
            return true;
        }
        Player player = (Player)sender;
        if (args.length < 2) {
            player.sendMessage(CC.translate("&cUsage: " + this.getUsage(label)));
        }
        else if (args[1].equalsIgnoreCase("spawn")) {
            this.file.set("Locations.Spawn", LocationUtils.getString(player.getLocation()));
            this.file.save();
            this.file.reload();
            player.sendMessage(CC.translate("&6&lTournament &8* &aSpawn location saved."));
        }
        else if (args[1].equalsIgnoreCase("sumo")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + label + ' ' + this.getName() + " sumo <spawn|first|second>"));
                return true;
            }
            if (args[2].equalsIgnoreCase("spawn")) {
                this.file.set("Locations.Sumo.Spawn", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aSumo Spawn location saved."));
            }
            else if (args[2].equalsIgnoreCase("first")) {
                this.file.set("Locations.Sumo.First", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aSumo First location saved."));
            }
            else if (args[2].equalsIgnoreCase("second")) {
                this.file.set("Locations.Sumo.Second", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aSumo Second location saved."));
            }
            else if(args[2].equalsIgnoreCase("spectate")) {
            	this.file.set("Locations.Sumo.Spectate", LocationUtils.getString(player.getLocation()));
            	this.file.save();
            	this.file.reload();
            	player.sendMessage(CC.translate("&6&lTournament &8* &aSumo Spectate zone location saved."));
            }
            else {
                player.sendMessage(CC.translate("&cTournament sub-command '" + args[2] + "' not found."));
            }
        }
        else if (args[1].equalsIgnoreCase("ffa")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + label + ' ' + this.getName() + " ffa <spawn>"));
                return true;
            }
            if (args[2].equalsIgnoreCase("spawn")) {
                this.file.set("Locations.FFA.Spawn", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aFFA Spawn location saved."));
            }
            else {
                player.sendMessage(CC.translate("&cTournament sub-command '" + args[2] + "' not found."));
            }
        }
        else if (args[1].equalsIgnoreCase("axe")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + label + ' ' + this.getName() + " axe <spawn>"));
                return true;
            }
            if (args[2].equalsIgnoreCase("spawn")) {
                this.file.set("Locations.Axe.Spawn", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aAxe Spawn location saved."));
            }
            else {
                player.sendMessage(CC.translate("&cTournament sub-command '" + args[2] + "' not found."));
            }
        }
        else if (args[1].equalsIgnoreCase("archer")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + label + ' ' + this.getName() + " archer <spawn>"));
                return true;
            }
            if (args[2].equalsIgnoreCase("spawn")) {
                this.file.set("Locations.Archer.Spawn", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aArcher Spawn location saved."));
            }
            else {
                player.sendMessage(CC.translate("&cTournament sub-command '" + args[2] + "' not found."));
            }
        }
        else if (args[1].equalsIgnoreCase("spleef")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + label + ' ' + this.getName() + " spleef <spawn>"));
                return true;
            }
            if (args[2].equalsIgnoreCase("spawn")) {
                this.file.set("Locations.Spleef.Spawn", LocationUtils.getString(player.getLocation()));
                this.file.save();
                this.file.reload();
                player.sendMessage(CC.translate("&6&lTournament &8* &aSpleef Spawn location saved."));
            }
            else {
                player.sendMessage(CC.translate("&cTournament sub-command '" + args[2] + "' not found."));
            }
        }
        else {
            player.sendMessage(CC.translate("&cTournament sub-command '" + args[1] + "' not found."));
        }
        return true;
    }
}
