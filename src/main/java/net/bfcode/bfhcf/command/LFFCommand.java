package net.bfcode.bfhcf.command;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

public class LFFCommand implements CommandExecutor {
    private HCFaction plugin;

    private static TimeUnit UNIT = TimeUnit.HOURS;
    private static int TIME = 1;
    private static long MILLIS = UNIT.toMillis(TIME);
    private static String WORDS = DurationFormatUtils.formatDurationWords(MILLIS, true, true);

    @SuppressWarnings("rawtypes")
	private Map timestampMap = (Map) CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(TIME, UNIT).build().asMap();

    public LFFCommand(HCFaction plugin) {
        this.plugin = plugin;
    }

    private static String LFFCOMMAND = "lff", LFFTOGGLE = "lffalerts";
    public static String LFF_META = "LFF_NOALERTS";

    @SuppressWarnings({ "deprecation", "unchecked" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        if(eotwRunnable != null) {
        	sender.sendMessage(CC.translate("&cYou are not use this command in EOTW"));
        	return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equals(LFFCOMMAND)) {
				Faction faction = plugin.getFactionManager().getPlayerFaction(player);
                if(faction == null) {
                    Long timeStamp = (Long) timestampMap.get(player.getUniqueId());
                    long now = System.currentTimeMillis();
                    long diff;
                    if (timeStamp == null || (diff = now - timeStamp) > MILLIS) {
                        String bar = CC.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 40);
                        String message = " " + CC.GOLD + ConfigurationService.DOUBLEARROW + " " + CC.GREEN + ChatColor.BOLD.toString() + player.getName() + CC.YELLOW + " is looking to join a " + ChatColor.RED.toString() + ChatColor.BOLD + "faction";
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            if (!other.hasMetadata(LFF_META)) {
                                other.sendMessage(bar);
                                other.sendMessage(message);
                                other.sendMessage(bar);
                            }
                        }
                        timestampMap.put(player.getUniqueId(), now);
                        player.sendMessage(CC.AQUA + "You have announced that you are looking for a faction, you must wait " + WORDS + " before doing this again.");
                    } else {
                        player.sendMessage(CC.RED + "You may not use this for " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(MILLIS - diff, true, true));
                    }
                }
                else{
                    player.sendMessage(ChatColor.RED + "You are already in a faction.");
                }
            } else if (cmd.getName().equals(LFFTOGGLE)) {
                FactionUser factionUser = plugin.getUserManager().getUser(player.getUniqueId());
                if(factionUser != null) {
                    factionUser.setLffalerts(!factionUser.isLffalerts());
                    sender.sendMessage((factionUser.isLffalerts() ? ChatColor.GREEN + "LFF alerts are now enabled." : ChatColor.RED + "LFF alerts are now disabled."));
                    if(factionUser.isFdalerts()){
                        player.removeMetadata(LFF_META, plugin);
                    }
                    else{
                        player.setMetadata(LFF_META, new FixedMetadataValue(plugin, true));
                    }
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this.");
        }
        return false;
    }
}

