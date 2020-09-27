package net.bfcode.bfhcf.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class FactionInviteArgument extends CommandArgument
{
    private static Pattern USERNAME_REGEX;
    private HCFaction plugin;
    
    public FactionInviteArgument(HCFaction plugin) {
        super("invite", "Invite a player to the faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "inv", "invitemember", "inviteplayer" };
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite to a faction.");
            return true;
        }
        if (args.length < 2) {
        	sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (!FactionInviteArgument.USERNAME_REGEX.matcher(args[1]).matches()) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is an invalid username.");
            return true;
        }
        Player player = (Player)sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must a faction officer to invite members.");
            return true;
        }
        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        String name = args[1];
        if (playerFaction.getMember(name) != null) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is already in your faction.");
            return true;
        }
        if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players whilst your faction is raidable.");
            return true;
        }
        if (!invitedPlayerNames.add(name)) {
            sender.sendMessage(ChatColor.RED + name + " has already been invited.");
            return true;
        }
        Player target = Bukkit.getPlayer(name);
        if (target != null) {
            name = target.getName();
            Text text = new Text(sender.getName()).setColor(Relation.ENEMY.toChatColour()).append((IChatBaseComponent)new Text(" has invited you to join ").setColor(ChatColor.YELLOW));
            text.append((IChatBaseComponent)new Text(playerFaction.getName()).setColor(Relation.ENEMY.toChatColour())).append((IChatBaseComponent)new Text(". ").setColor(ChatColor.YELLOW));
            text.append((IChatBaseComponent)new Text("Click here").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/" + label + " accept " + playerFaction.getName()).setHoverText(ChatColor.AQUA + "Click to join " + playerFaction.getDisplayName((CommandSender)target) + ChatColor.AQUA + '.')).append((IChatBaseComponent)new Text(" to accept this invitation.").setColor(ChatColor.YELLOW));
            text.send((CommandSender)target);
        }
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has invited " + Relation.ENEMY.toChatColour() + name + ChatColor.YELLOW + " to the faction.");
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player)sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<String>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                PlayerFaction targetFaction;
                if ((targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) != null && targetFaction.equals(playerFaction)) {
                    continue;
                }
                results.add(target.getName());
            }
        }
        return results;
    }
    
    static {
        USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
    }
}
