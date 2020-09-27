 package net.bfcode.bfhcf.faction.argument;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ActionMessage;
import net.bfcode.bfhcf.utils.CC;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FactionPointsCommand extends CommandArgument {
    @SuppressWarnings("unused")
	private List<PlayerFaction> topFactions = new ArrayList<>();
    @SuppressWarnings("unused")
	private Long lastSort = null;
    @SuppressWarnings("unused")
	private HCFaction plugin;
    public static Comparator<PlayerFaction> POINTS_COMPARATOR = Comparator.comparingInt(PlayerFaction::getPoints);
    
    public FactionPointsCommand(HCFaction plugin) {
        super("top", "See a list of all factions.");
        this.plugin = plugin;
        this.isPlayerOnly = true;
    }

    public String getUsage(String label) {
        return "/" + label + " " + getName();

    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<PlayerFaction> data = new ArrayList<>(HCFaction.getPlugin().getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getPoints() > 0).collect(Collectors.toSet()));
        Collections.sort(data, POINTS_COMPARATOR);
        Collections.reverse(data);

        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(CC.translate("&6&lFaction Top Points"));

        if(!data.isEmpty()) {
		    for(int i = 0; i < 8; i++) {
		        if (i >= data.size()) {
		            break;
		        }
		
		        PlayerFaction next = data.get(i);
		
		        ActionMessage actionMessage = new ActionMessage();
		        actionMessage.addText("&7" + (i + 1) + ") &e" + next.getName() + "&7: &f" + next.getPoints()).setClickEvent(ActionMessage.ClickableType.RunCommand, "/f show " + next.getName()).addHoverText(CC.translate("&eClick view &6" + next.getName() + " faction."));
		        actionMessage.sendToPlayer(((Player) sender));
		    }
        } else {
        	sender.sendMessage(CC.translate("&7- &eNone"));
        }

        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		return true;
    }
}