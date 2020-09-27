package net.bfcode.bfhcf.faction.argument;

import java.util.Collection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;

import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.MapSorting;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.JavaUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class FactionListArgument extends CommandArgument
{
    @SuppressWarnings("unused")
	private static int MAX_FACTIONS_PER_PAGE = 10;
    private HCFaction plugin;
    
    public FactionListArgument(HCFaction plugin) {
        super("list", "See a list of all factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "l" };
    }
        
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Integer page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            page = JavaUtil.tryParseInt(args[1]);
            if (page == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        new BukkitRunnable() {
            public void run() {
                FactionListArgument.this.showList(page, label, sender);
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    @SuppressWarnings("deprecation")
	private void showList(int pageNumber, String label, CommandSender sender) {
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
        Player senderPlayer = (Player)sender;
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            if (senderPlayer == null || senderPlayer.canSee(target)) {
                PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
                if (playerFaction == null) {
                    continue;
                }
                factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
            }
        }
        Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
        @SuppressWarnings({ "unchecked", "rawtypes" })
		List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>)MapSorting.sortedValues((Map)factionOnlineMap, (Comparator)Comparator.reverseOrder());
        for (Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();
            List<BaseComponent[]> results = pages.get(currentPage);
            if (results == null || results.size() >= 10) {
                pages.put(++currentPage, results = new ArrayList<BaseComponent[]>(10));
            }
            PlayerFaction playerFaction2 = entry.getKey();
            String displayName = playerFaction2.getName();
            int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * 10) : 0) + 1;
            ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(ChatColor.YELLOW + ChatColor.stripColor(displayName)).color(net.md_5.bungee.api.ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.WHITE + "Click to view " + displayName + ChatColor.WHITE + '.').create()));
            builder.append(" (" + ChatColor.WHITE + entry.getValue() + '/' + playerFaction2.getMembers().size() + ")" + ChatColor.GRAY + " [" + playerFaction2.getDtrColour() + JavaUtils.format((Number)playerFaction2.getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + playerFaction2.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "] ", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.WHITE);
            results.add(builder.create());
        }
        int maxPages = pages.size();
        if (pageNumber > maxPages) {
            sender.sendMessage(ChatColor.YELLOW + "There " + ((maxPages == 1) ? ("is only " + maxPages + " page") : "no factions to be displayed at this time") + ".");
            return;
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + " Faction List " + ChatColor.GRAY + "(Page " + pageNumber + " out of " + maxPages + " pages)");
        Player player = (Player)sender;
        Collection<BaseComponent[]> components = pages.get(pageNumber);
        for (BaseComponent[] component : components) {
            if (component == null) {
                continue;
            }
            if (player != null) {
                player.spigot().sendMessage(component);
            }
            else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }
        sender.sendMessage(ChatColor.YELLOW + " Use " + ChatColor.GRAY + '/' + label + ' ' + this.getName() + " <#>" + ChatColor.YELLOW + " to view the other pages.");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
}
