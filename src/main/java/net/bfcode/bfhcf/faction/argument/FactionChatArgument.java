package net.bfcode.bfhcf.faction.argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class FactionChatArgument extends CommandArgument
{
    private HCFaction plugin;
    
    public FactionChatArgument(HCFaction plugin) {
        super("chat", "Toggle faction chat only mode on or off.", new String[] { "c" });
        this.plugin = plugin;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " [fac|public|ally] [message]";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        FactionMember member = playerFaction.getMember(player.getUniqueId());
        ChatChannel currentChannel = member.getChatChannel();
        ChatChannel parsed = (args.length >= 2) ? ChatChannel.parse(args[1], null) : currentChannel.getRotation();
        if (parsed == null && currentChannel != ChatChannel.PUBLIC) {
            Collection<Player> recipients = playerFaction.getOnlinePlayers();
            if (currentChannel == ChatChannel.ALLIANCE) {
                for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
                    recipients.addAll(ally.getOnlinePlayers());
                }
            }
            String format = String.format(currentChannel.getRawFormat(player), "", StringUtils.join(args, ' ', 1, args.length));
            for (Player recipient : recipients) {
                recipient.sendMessage(format);
            }
            return true;
        }
        ChatChannel newChannel = (parsed == null) ? currentChannel.getRotation() : parsed;
        member.setChatChannel(newChannel);
        sender.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.AQUA + newChannel.getDisplayName().toLowerCase() + ChatColor.YELLOW + " chat mode.");
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        ChatChannel[] values = ChatChannel.values();
        ArrayList<String> results = new ArrayList<String>(values.length);
        for (ChatChannel type : values) {
            results.add(type.getName());
        }
        return results;
    }
}
