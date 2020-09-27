package net.bfcode.bfhcf.faction.argument.staff;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableList;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.FactionChatEvent;
import net.bfcode.bfhcf.faction.event.FactionRemoveEvent;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class FactionChatSpyArgument extends CommandArgument implements Listener
{
    private static UUID ALL_UUID;
    private static ImmutableList<String> COMPLETIONS;
    private HCFaction plugin;
    
    public FactionChatSpyArgument(HCFaction plugin) {
        super("chatspy", "Spy on the chat of a faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "cs" };
        this.permission = "hcf.command.faction.argument." + this.getName();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <" + StringUtils.join((Iterable<?>)FactionChatSpyArgument.COMPLETIONS, '|') + "> [factionName]";
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        if (event.getFaction() instanceof PlayerFaction) {
            UUID factionUUID = event.getFaction().getUniqueID();
            for (FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.getFactionChatSpying().remove(factionUUID);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionChat(FactionChatEvent event) {
        Player player = event.getPlayer();
        Faction faction = event.getFaction();
        String format = ChatColor.GOLD + "[" + ChatColor.RED + event.getChatChannel().getDisplayName() + ": " + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + "] " + ChatColor.GRAY + event.getFactionMember().getRole().getAstrix() + player.getName() + ": " + ChatColor.YELLOW + event.getMessage();
        HashSet<Player> recipients = new HashSet<Player>();
        recipients.removeAll(event.getRecipients());
        for (CommandSender recipient : recipients) {
            if (!(recipient instanceof Player)) {
                continue;
            }
            Player target = (Player)recipient;
            FactionUser user = event.isAsynchronous() ? this.plugin.getUserManager().getUserAsync(target.getUniqueId()) : this.plugin.getUserManager().getUser(player.getUniqueId());
            Collection<UUID> spying = user.getFactionChatSpying();
            if (!spying.contains(FactionChatSpyArgument.ALL_UUID) && !spying.contains(faction.getUniqueID())) {
                continue;
            }
            recipient.sendMessage(format);
        }
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        Set<UUID> currentSpies = this.plugin.getUserManager().getUser(player.getUniqueId()).getFactionChatSpying();
        if (args[1].equalsIgnoreCase("list")) {
            if (currentSpies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the chat of any factions.");
                return true;
            }
            sender.sendMessage(ChatColor.GRAY + "You are currently spying on the chat of (" + currentSpies.size() + " factions): " + ChatColor.RED + StringUtils.join(currentSpies, ChatColor.GRAY + ", " + ChatColor.RED) + ChatColor.GRAY + '.');
            return true;
        }
        else if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[1].toLowerCase() + " <all|factionName|playerName>");
                return true;
            }
            Faction faction = this.plugin.getFactionManager().getFaction(args[2]);
            if (!(faction instanceof PlayerFaction)) {
                sender.sendMessage(ChatColor.RED + "Player based faction named or containing member with IGN or UUID " + args[2] + " not found.");
                return true;
            }
            if (currentSpies.contains(FactionChatSpyArgument.ALL_UUID) || currentSpies.contains(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the chat of " + (args[2].equalsIgnoreCase("all") ? "all factions" : args[2]) + '.');
                return true;
            }
            if (args[2].equalsIgnoreCase("all")) {
                currentSpies.clear();
                currentSpies.add(FactionChatSpyArgument.ALL_UUID);
                sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of all factions.");
                return true;
            }
            if (currentSpies.add(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + faction.getDisplayName(sender) + ChatColor.GREEN + '.');
            }
            else {
                sender.sendMessage(ChatColor.RED + "You are already spying on the chat of " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            return true;
        }
        else if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[1].toLowerCase() + " <playerName>");
                return true;
            }
            if (args[2].equalsIgnoreCase("all")) {
                currentSpies.remove(FactionChatSpyArgument.ALL_UUID);
                sender.sendMessage(ChatColor.RED + "No longer spying on the chat of all factions.");
                return true;
            }
            Faction faction = this.plugin.getFactionManager().getContainingFaction(args[2]);
            if (faction == null) {
                sender.sendMessage(ChatColor.GOLD + "Faction '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                return true;
            }
            if (currentSpies.remove(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.RED + "You are no longer spying on the chat of " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            else {
                sender.sendMessage(ChatColor.RED + "You will still not be spying on the chat of " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            return true;
        }
        else {
            if (args[1].equalsIgnoreCase("clear")) {
                currentSpies.clear();
                sender.sendMessage(ChatColor.YELLOW + "You are no longer spying the chat of any faction.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
    }
    
    static {
        ALL_UUID = UUID.fromString("5a3ed6d1-0239-4e24-b4a9-8cd5b3e5fc72");
        COMPLETIONS = ImmutableList.of("list", "add", "del", "clear");
    }
}
