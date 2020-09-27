package net.bfcode.bfhcf.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionCreateArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionCreateArgument(HCFaction plugin) {
        super("create", "Create a faction.", new String[] { "make", "define" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }
        EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        if(eotwRunnable != null) {
        	sender.sendMessage(CC.translate("&cYou are not use this command in EOTW"));
        	return true;
        }
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        String name = args[1];
        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
            return true;
        }
        if (name.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }
        if (this.plugin.getFactionManager().getPlayerFaction((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        this.plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
