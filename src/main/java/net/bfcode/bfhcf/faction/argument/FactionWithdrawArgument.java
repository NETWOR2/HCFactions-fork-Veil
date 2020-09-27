package net.bfcode.bfhcf.faction.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class FactionWithdrawArgument extends CommandArgument
{
    private static ImmutableList<String> COMPLETIONS;
    private HCFaction plugin;
    
    public FactionWithdrawArgument(HCFaction plugin) {
        super("withdraw", "Withdraws money from the faction balance.", new String[] { "w" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <all|amount>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can update the faction balance.");
            return true;
        }
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        FactionMember factionMember = playerFaction.getMember(uuid);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to withdraw money.");
            return true;
        }
        int factionBalance = playerFaction.getBalance();
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = factionBalance;
        }
        else {
            amount = JavaUtil.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "Error: '" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }
        if (amount > factionBalance) {
            sender.sendMessage(ChatColor.RED + "Your faction need at least " + '$' + JavaUtils.format((Number)amount) + " to do this, whilst it only has " + '$' + JavaUtils.format((Number)factionBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().addBalance(uuid, amount);
        playerFaction.setBalance(factionBalance - amount);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has withdrew " + ChatColor.BOLD + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " from the faction balance.");
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (List<String>)((args.length == 2) ? FactionWithdrawArgument.COMPLETIONS : Collections.emptyList());
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
