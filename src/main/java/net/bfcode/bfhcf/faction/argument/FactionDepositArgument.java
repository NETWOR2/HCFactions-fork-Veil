package net.bfcode.bfhcf.faction.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.JavaUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class FactionDepositArgument extends CommandArgument
{
    private static ImmutableList<String> COMPLETIONS;
    private HCFaction plugin;
    
    public FactionDepositArgument(HCFaction plugin) {
        super("deposit", "Deposits money to the faction balance.", new String[] { "d" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <all|amount>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
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
        int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        }
        else {
            amount = JavaUtil.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }
        if (playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You need at least " + '$' + JavaUtils.format((Number)amount) + " to do this, you only have " + '$' + JavaUtils.format((Number)playerBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().subtractBalance(uuid, amount);
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GREEN + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " into the faction balance.");
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (List<String>)((args.length == 2) ? FactionDepositArgument.COMPLETIONS : Collections.emptyList());
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
