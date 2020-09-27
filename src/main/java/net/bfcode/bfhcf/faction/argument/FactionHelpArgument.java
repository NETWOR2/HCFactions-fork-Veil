package net.bfcode.bfhcf.faction.argument;

import com.google.common.collect.Multimap;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.faction.FactionExecutor;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.JavaUtil;

import com.google.common.collect.ArrayListMultimap;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableMultimap;

public class FactionHelpArgument extends CommandArgument
{
    @SuppressWarnings("unused")
	private static int HELP_PER_PAGE = 10;
    private FactionExecutor executor;
    private ImmutableMultimap<Integer, Text> pages;
    
    public FactionHelpArgument(FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }
    
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        Integer page = JavaUtil.tryParseInt(args[1]);
        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void showPage(CommandSender sender, String label, int pageNumber) {
        if (this.pages == null) {
            boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
			ArrayListMultimap pages = ArrayListMultimap.create();
            for (CommandArgument argument : this.executor.getArguments()) {
                String permission;
                if (!argument.equals(this) && ((permission = argument.getPermission()) == null || sender.hasPermission(permission))) {
                    if (argument.isPlayerOnly() && !isPlayer) {
                        continue;
                    }
                    pages.get(val).add(new Text(ChatColor.YELLOW + " /" + label + ' ' + argument.getName() + ChatColor.GOLD + " " + ConfigurationService.DOUBLEARROW + " " + ChatColor.WHITE + argument.getDescription()).setColor(ChatColor.WHITE).setClick(ClickAction.SUGGEST_COMMAND, "/" + label + " " + argument.getName()));
                    if (++count % 100 != 0) {
                        continue;
                    }
                    ++val;
                }
            }
            this.pages = (ImmutableMultimap<Integer, Text>)ImmutableMultimap.copyOf((Multimap)pages);
        }
        int totalPageCount = this.pages.size() / 100 + 1;
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " Faction Help ");
        sender.sendMessage(" ");
        for (Text message : this.pages.get(pageNumber)) {
            message.send(sender);
        }
        sender.sendMessage(" ");
        @SuppressWarnings("unused")
		Text text = new Text();
        if (pageNumber == totalPageCount) {
        }
        else {
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
