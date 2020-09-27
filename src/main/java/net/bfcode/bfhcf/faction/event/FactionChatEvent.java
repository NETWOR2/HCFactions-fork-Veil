package net.bfcode.bfhcf.faction.event;

import org.bukkit.command.CommandSender;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.struct.ChatChannel;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import org.bukkit.event.Cancellable;

public class FactionChatEvent extends FactionEvent implements Cancellable
{
    private static HandlerList handlers;
    private Player player;
    private FactionMember factionMember;
    private ChatChannel chatChannel;
    private String message;
    private Collection<? extends CommandSender> recipients;
    private String fallbackFormat;
    private boolean cancelled;
    
    public FactionChatEvent(boolean async, PlayerFaction faction, Player player, ChatChannel chatChannel, Collection<? extends CommandSender> recipients, String message) {
        super(faction, async);
        this.player = player;
        this.factionMember = faction.getMember(player.getUniqueId());
        this.chatChannel = chatChannel;
        this.recipients = recipients;
        this.message = message;
        this.fallbackFormat = chatChannel.getRawFormat(player);
    }
    
    public static HandlerList getHandlerList() {
        return FactionChatEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public FactionMember getFactionMember() {
        return this.factionMember;
    }
    
    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }
    
    public Collection<? extends CommandSender> getRecipients() {
        return this.recipients;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getFallbackFormat() {
        return this.fallbackFormat;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    public HandlerList getHandlers() {
        return FactionChatEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
