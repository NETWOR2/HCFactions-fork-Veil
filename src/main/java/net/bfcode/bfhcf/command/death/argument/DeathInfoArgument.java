package net.bfcode.bfhcf.command.death.argument;

import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.FancyMessage;

public class DeathInfoArgument extends CommandArgument
{
    @SuppressWarnings("unused")
	private HCFaction plugin;
    
    public DeathInfoArgument(HCFaction plugin) {
        super("info", "Check Deathban Reason");
        this.plugin = plugin;
        this.permission = "hcf.command.death.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return false;
        }
        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        FactionUser player = HCFaction.getPlugin().getUserManager().getUser(target.getUniqueId());
        Deathban deathban = player.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + "This player is not deathbanned.");
            return true;
        }
        Double x = deathban.getDeathPoint().getX();
        Double y = deathban.getDeathPoint().getY();
        Double z = deathban.getDeathPoint().getZ();
        String remaining = HCFaction.getRemaining(deathban.getRemaining(), true, false);
        DecimalFormat df = new DecimalFormat("##");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lDeath Info"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eStatus: &fDeathbanned."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eReason: &f" + deathban.getReason() + "."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eRemaining: &f" + remaining + "."));
        new FancyMessage(" Location: ").color(ChatColor.YELLOW).then(df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ".").color(ChatColor.WHITE).command("/tp " + df.format(x) + " " + df.format(y) + " " + df.format(z)).tooltip(ChatColor.YELLOW + "Click to teleport to " + df.format(x) + ", " + df.format(y) + ", " + df.format(z)).send((Player) sender);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        return true;
    }
}
