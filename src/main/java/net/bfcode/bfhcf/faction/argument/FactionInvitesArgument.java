package net.bfcode.bfhcf.faction.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class FactionInvitesArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionInvitesArgument(HCFaction plugin) {
        super("invites", "View faction invitations.");
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have faction invites.");
            return true;
        }
        List<String> receivedInvites = new ArrayList<String>();
        for (Faction faction : this.plugin.getFactionManager().getFactions()) {
            if (faction instanceof PlayerFaction) {
                PlayerFaction targetPlayerFaction = (PlayerFaction)faction;
                if (!targetPlayerFaction.getInvitedPlayerNames().contains(sender.getName())) {
                    continue;
                }
                receivedInvites.add(targetPlayerFaction.getDisplayName(sender));
            }
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)sender).getUniqueId());
        String delimiter = ChatColor.WHITE + ", " + ChatColor.GRAY;
        if (playerFaction != null) {
            Set<String> sentInvites = playerFaction.getInvitedPlayerNames();
            sender.sendMessage(ChatColor.YELLOW + "Sent by " + playerFaction.getDisplayName(sender) + ChatColor.YELLOW + " (" + sentInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (sentInvites.isEmpty() ? "Your faction has not invited anyone." : (StringUtils.join(sentInvites, delimiter) + '.')));
        }
        sender.sendMessage(ChatColor.YELLOW + "Requested (" + receivedInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (receivedInvites.isEmpty() ? "No factions have invited you." : (StringUtils.join(receivedInvites, ChatColor.WHITE + delimiter) + '.')));
        return true;
    }
}
