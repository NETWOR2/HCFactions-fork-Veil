package net.bfcode.bfhcf.kothgame.eotw;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import java.util.Collections;
import java.util.List;
import org.bukkit.conversations.Conversable;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.conversations.ConversationFactory;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class EotwCommand implements CommandExecutor, TabCompleter
{
    private ConversationFactory factory;
    
    public EotwCommand(HCFaction plugin) {
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Command Console only.");
            return true;
        }
        Conversable conversable = (Conversable)sender;
        conversable.beginConversation(this.factory.buildConversation(conversable));
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
    
    private static class EotwPrompt extends StringPrompt
    {
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? The server will be in EOTW mode, If EOTW mode is active, all claims whilst making Spawn a KOTH. You will still have " + EOTWHandler.EOTW_WARMUP_WAIT_SECONDS + " seconds to cancel this using the same command though. Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }
        
        public Prompt acceptInput(ConversationContext context, String string) {
            if (string.equalsIgnoreCase("yes")) {
                boolean newStatus = !HCFaction.getPlugin().getEotwHandler().isEndOfTheWorld(false);
                Conversable conversable = context.getForWhom();
                Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "              End Of The World");
                Bukkit.broadcastMessage(ChatColor.RED + "                 has been activated!");
                Bukkit.broadcastMessage(ChatColor.GRAY + "");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "              All Faction claims will be");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "               unclaimed in 5 minutes!");
                Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                if (conversable instanceof CommandSender) {
                    Command.broadcastCommandMessage((CommandSender)conversable, ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                }
                else {
                    conversable.sendRawMessage(ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                }
                HCFaction.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
            }
            else if (string.equalsIgnoreCase("no")) {
                context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of setting EOTW mode.");
            }
            else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of toggling EOTW mode has been cancelled.");
            }
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
