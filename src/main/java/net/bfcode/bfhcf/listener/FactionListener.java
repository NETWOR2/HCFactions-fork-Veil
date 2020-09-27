package net.bfcode.bfhcf.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.hatsur.library.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Optional;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.event.CaptureZoneEnterEvent;
import net.bfcode.bfhcf.faction.event.CaptureZoneLeaveEvent;
import net.bfcode.bfhcf.faction.event.FactionCreateEvent;
import net.bfcode.bfhcf.faction.event.FactionRemoveEvent;
import net.bfcode.bfhcf.faction.event.FactionRenameEvent;
import net.bfcode.bfhcf.faction.event.PlayerClaimEnterEvent;
import net.bfcode.bfhcf.faction.event.PlayerJoinFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeaveFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeftFactionEvent;
import net.bfcode.bfhcf.faction.struct.RegenStatus;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Messager;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class FactionListener implements Listener
{
    private static long FACTION_JOIN_WAIT_MILLIS;
    private static String FACTION_JOIN_WAIT_WORDS;
    private HCFaction plugin;
    
    public FactionListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[KOTH]") && this.plugin.getFactionManager().getFaction(e.getLine(1)) instanceof KothFaction) {
            KothFaction kothFaction = (KothFaction)this.plugin.getFactionManager().getFaction(e.getLine(1));
            e.setLine(0, ChatColor.LIGHT_PURPLE + "KOTH");
            e.setLine(1, ChatColor.GOLD + kothFaction.getName());
            for (Claim claim : kothFaction.getClaims()) {
                Location location = claim.getCenter();
                e.setLine(2, ChatColor.RED.toString() + location.getBlockX() + " | " + location.getBlockZ());
            }
            e.setLine(3, ChatColor.RED + kothFaction.getCaptureZone().getDefaultCaptureWords());
        }
    }
    
    private String getDisplayName(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player)sender).getDisplayName();
        }
        return sender.getName();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionCreate(FactionCreateEvent event) {
        CommandSender sender = event.getSender();
        Faction faction = event.getFaction();
        String NameFaction = event.getFaction().getName();
        String player = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(((Player)sender).getUniqueId()).getColoredUsername();
        if (faction instanceof PlayerFaction) {
//            Bukkit.broadcastMessage(CC.translate(ChatColor.DARK_GRAY + " \u2a20 " + ChatColor.GOLD.toString() + ChatColor.BOLD + "PhonexMC " + ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created " + ChatColor.YELLOW + "by " + ChatColor.WHITE + "" + this.plugin.getChat().getPlayerPrefix((Player) sender) + this.getDisplayName(sender) + ChatColor.YELLOW + "."));
            Bukkit.broadcastMessage(CC.translate(HCFaction.getPlugin().getConfig().getString("FACTION_MESSAGES.CREATE")
            		.replace("%player%", player)
            		.replace("%faction%", NameFaction)
            		.replace("%arrow%", "\u2a20")));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        CommandSender sender = event.getSender();
        Faction faction = event.getFaction();
        String player = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(((Player)sender).getUniqueId()).getColoredUsername();
        String NameFaction = event.getFaction().getName();
        if (faction instanceof PlayerFaction) {
//            Bukkit.broadcastMessage(CC.translate(ChatColor.DARK_GRAY + " \u2a20 " + ChatColor.GOLD.toString() + ChatColor.BOLD + "PhonexMC " + ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " has been " + ChatColor.RED + "disbanded " + ChatColor.YELLOW + "by " + ChatColor.WHITE + this.plugin.getChat().getPlayerPrefix((Player) sender) + this.getDisplayName(sender) + ChatColor.YELLOW + "."));
            Bukkit.broadcastMessage(CC.translate(HCFaction.getPlugin().getConfig().getString("FACTION_MESSAGES.REMOVE")
            		.replace("%player%", player)
            		.replace("%faction%", NameFaction)
            		.replace("%arrow%", "\u2a20")));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
//            ((PlayerFaction)faction).broadcast(ChatColor.translateAlternateColorCodes('&', "&aYour Team has been renamed to &b" + event.getNewName() + "&a by " + event.getSender().getName()));
            ((PlayerFaction)faction).broadcast(CC.translate(HCFaction.getPlugin().getConfig().getString("FACTION_MESSAGES.RENAME")
            		.replace("%newname%", event.getNewName())
            		.replace("%player%", event.getSender().getName())
            		.replace("%arrow%", "\u2a20")));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRenameMonitor(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            ((KothFaction)faction).getCaptureZone().setName(event.getNewName());
        }
    }
    
    private long getLastLandChangedMeta(Player player) {
        List<MetadataValue> value = (List<MetadataValue>)player.getMetadata("landChangedMessage");
        long millis = System.currentTimeMillis();
        long remaining = (value == null || value.isEmpty()) ? 0L : (value.get(0).asLong() - millis);
        if (remaining <= 0L) {
            player.setMetadata("landChangedMessage", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (millis + 225L)));
        }
        return remaining;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.AQUA + "Now entering capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.AQUA + '(' + event.getFaction().getName() + ChatColor.AQUA + ')');
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	Faction faction = this.plugin.getFactionManager().getFactionAt(player.getLocation());
    	if(faction instanceof EventFaction && !player.hasPermission("rank.staff")) {
    		if(event.getMessage().equalsIgnoreCase("/spawn") 
    				|| event.getMessage().equalsIgnoreCase("/f home")
    				|| event.getMessage().equalsIgnoreCase("/fac home")
    				|| event.getMessage().equalsIgnoreCase("/faction home")
    				|| event.getMessage().equalsIgnoreCase("/f stuck")
    				|| event.getMessage().equalsIgnoreCase("/fac stuck")
    				|| event.getMessage().equalsIgnoreCase("/faction stuck")) {
    			event.setCancelled(true);
    			Messager.player(player, "&3&lEvent &7\u00BB &cNo tienes permisos para usar este comando.");
    		}
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
        Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.AQUA + "Now leaving capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.AQUA + '(' + event.getFaction().getName() + ChatColor.AQUA + ')');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Faction toFaction = event.getToFaction();
        Player player = event.getPlayer();
        if (toFaction.isSafezone()) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setSaturation(4.0f);
        }
        if (getLastLandChangedMeta(player) <= 0L) {
            if (ConfigurationService.KIT_MAP) {
                Faction fromFaction = event.getFromFaction();
                player.sendMessage(ChatColor.AQUA + "Leaving " + ChatColor.GRAY + "» " + fromFaction.getDisplayName(player));
                player.sendMessage(ChatColor.AQUA + "Entering " + ChatColor.GRAY + "» " + toFaction.getDisplayName(player));
            }
            else {
                Faction fromFaction = event.getFromFaction();
                player.sendMessage(ChatColor.AQUA + "Now leaving " + ChatColor.GRAY + "» " + fromFaction.getDisplayName(player) + ChatColor.AQUA + " (" + (fromFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.AQUA + ')');
                player.sendMessage(ChatColor.AQUA + "Now entering " + ChatColor.GRAY + "» " + toFaction.getDisplayName(player) + ChatColor.AQUA + " (" + (toFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.AQUA + ')');
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawnFoodChange(FoodLevelChangeEvent event) {
    	Player player = (Player) event.getEntity();
    	Faction faction = this.plugin.getFactionManager().getFactionAt(player.getLocation());
    	if(faction instanceof SpawnFaction) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optionalPlayer = event.getPlayer();
        if (optionalPlayer.isPresent()) {
            this.plugin.getUserManager().getUser(((Player)optionalPlayer.get()).getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
        Faction faction = event.getFaction();
        Optional<Player> optionalPlayer = event.getPlayer();
        if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            Player player = (Player)optionalPlayer.get();
            PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
                return;
            }
            long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FactionListener.FACTION_JOIN_WAIT_MILLIS;
            if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within " + FactionListener.FACTION_JOIN_WAIT_WORDS + ". You have to wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        Faction faction = event.getFaction();
        Optional<Player> optional;
        if (faction instanceof PlayerFaction && (optional = event.getPlayer()).isPresent()) {
            Player player = (Player)optional.get();
            if (this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst you remain in its' territory.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction != null) {
            playerFaction.printDetails((CommandSender)player);
//            playerFaction.broadcast(ChatColor.YELLOW + "Member Online" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.', player.getUniqueId());
            playerFaction.broadcast(CC.translate(HCFaction.getPlugin().getConfig().getString("FACTION_MESSAGES.ON_JOIN")
            		.replace("%player%", playerFaction.getMember(player).getRole().getAstrix() + player.getName())
            		.replace("%normalarrow%", "»")), player.getUniqueId());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction != null) {
//            playerFaction.broadcast(ChatColor.YELLOW + "Member Offline" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
            playerFaction.broadcast(CC.translate(HCFaction.getPlugin().getConfig().getString("FACTION_MESSAGES.ON_LEAVE")
            		.replace("%player%", playerFaction.getMember(player).getRole().getAstrix() + player.getName())
            		.replace("%normalarrow%", "»")));
        }
    }

    static {
        FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("faction-settings.WAIT-FOR-JOIN"));
        FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FactionListener.FACTION_JOIN_WAIT_MILLIS, true, true);
    }
}
