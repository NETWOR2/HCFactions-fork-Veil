package net.bfcode.bfhcf.faction.argument.staff;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.entity.Player;
import java.util.List;

import org.bukkit.conversations.Conversable;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;

import org.bukkit.conversations.ConversationFactory;

public class FactionRemoveArgument extends CommandArgument
{
    private ConversationFactory factory;
    private HCFaction plugin;
    
    public FactionRemoveArgument(HCFaction plugin) {
        super("remove", "Remove a faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "delete", "forcedisband", "forceremove" };
        this.permission = "hcf.command.faction.argument." + this.getName();
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new RemoveAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <all|factionName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
                return true;
            }
            Conversable conversable = (Conversable)sender;
            conversable.beginConversation(this.factory.buildConversation(conversable));
            return true;
        }
        else {
            Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            if (this.plugin.getFactionManager().removeFaction(faction, sender)) {
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Disbanded faction " + faction.getName() + ChatColor.YELLOW + '.');
            }
            return true;
        }
    }
    
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        Player player = (Player)sender;
        ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                if (results.contains(target.getName())) {
                    continue;
                }
                results.add(target.getName());
            }
        }
        return results;
    }
    
    private static class RemoveAllPrompt extends StringPrompt
    {
        private HCFaction plugin;
        
        public RemoveAllPrompt(HCFaction plugin) {
            this.plugin = plugin;
        }
        
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All factions" + ChatColor.YELLOW + " will be cleared. Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }
        
        public Prompt acceptInput(ConversationContext context, String string) {
            switch (string.toLowerCase()) {
                case "no": {
                    context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of disbanding all factions.");
                    return Prompt.END_OF_CONVERSATION;
                }
                case "yes": {
                    for (Faction faction : this.plugin.getFactionManager().getFactions()) {
                        this.plugin.getFactionManager().removeFaction(faction, (CommandSender)Bukkit.getConsoleSender());
                    }
                    Conversable conversable = context.getForWhom();
                    Bukkit.broadcastMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + "All factions have been disbanded" + ((conversable instanceof CommandSender) ? (" by " + ((CommandSender)conversable).getName()) : "") + '.');
                    return Prompt.END_OF_CONVERSATION;
                }
                default:
                    break;
            }
            context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of disbanding all factions cancelled.");
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
