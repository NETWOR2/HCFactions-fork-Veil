package net.bfcode.bfhcf.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.util.GenericUtils;

public abstract class ServerParticipator implements ConfigurationSerializable
{
    private UUID uniqueId;
    private Set<String> ignoring;
    private Set<String> messageSpying;
    private UUID lastRepliedTo;
    private boolean messagesVisible;
    private long lastSpeakTimeMillis;
    private long lastReceivedMessageMillis;
    private long lastSentMessageMillis;
    
    public ServerParticipator(UUID uniqueId) {
        this.ignoring = (Set<String>)Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        this.messageSpying = Sets.newHashSet();
        this.messagesVisible = true;
        this.uniqueId = uniqueId;
    }
    
    public ServerParticipator(Map<String, Object> map) {
        this.ignoring = (Set<String>)Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        this.messageSpying = Sets.newHashSet();
        this.messagesVisible = true;
        this.uniqueId = UUID.fromString((String) map.get("uniqueID"));
        this.ignoring.addAll(GenericUtils.createList(map.get("ignoring"), String.class));
        this.messageSpying.addAll(GenericUtils.createList(map.get("messageSpying"), String.class));
        Object object = map.get("lastRepliedTo");
        if (object instanceof String) {
            this.lastRepliedTo = UUID.fromString((String)object);
        }
        if ((object = map.get("messagesVisible")) instanceof Boolean) {
            this.messagesVisible = (boolean)object;
        }
        if ((object = map.get("lastSpeakTimeMillis")) instanceof String) {
            this.lastSpeakTimeMillis = Long.parseLong((String)object);
        }
        if ((object = map.get("lastReceivedMessageMillis")) instanceof String) {
            this.lastReceivedMessageMillis = Long.parseLong((String)object);
        }
        if ((object = map.get("lastSentMessageMillis")) instanceof String) {
            this.lastSentMessageMillis = Long.parseLong((String)object);
        }
    }
    
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("uniqueID", this.uniqueId.toString());
        map.put("ignoring", new ArrayList(this.ignoring));
        map.put("messageSpying", new ArrayList(this.messageSpying));
        if (this.lastRepliedTo != null) {
            map.put("lastRepliedTo", this.lastRepliedTo.toString());
        }
        map.put("messagesVisible", this.messagesVisible);
        map.put("lastSpeakTimeMillis", Long.toString(this.lastSpeakTimeMillis));
        map.put("lastReceivedMessageMillis", Long.toString(this.lastReceivedMessageMillis));
        map.put("lastSentMessageMillis", Long.toString(this.lastSentMessageMillis));
        return map;
    }
    
    public abstract String getName();
    
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    
    public Set<String> getIgnoring() {
        return this.ignoring;
    }
    
    public Set<String> getMessageSpying() {
        return this.messageSpying;
    }
    
    public UUID getLastRepliedTo() {
        return this.lastRepliedTo;
    }
    
    public void setLastRepliedTo(UUID lastRepliedTo) {
        this.lastRepliedTo = lastRepliedTo;
    }
    
    public Player getLastRepliedToPlayer() {
        return Bukkit.getPlayer(this.lastRepliedTo);
    }
    
    public boolean isMessagesVisible() {
        return this.messagesVisible;
    }
    
    public void setMessagesVisible(boolean messagesVisible) {
        this.messagesVisible = messagesVisible;
    }
    
    public long getLastSpeakTimeRemaining() {
        if (this.lastSpeakTimeMillis > 0L) {
            return this.lastSpeakTimeMillis - System.currentTimeMillis();
        }
        return 0L;
    }
    
    public long getLastSpeakTimeMillis() {
        return this.lastSpeakTimeMillis;
    }
    
    public void setLastSpeakTimeMillis(long lastSpeakTimeMillis) {
        this.lastSpeakTimeMillis = lastSpeakTimeMillis;
    }
    
    public void updateLastSpeakTime() {
        long slowChatDelay = BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() * 1000L;
        this.lastSpeakTimeMillis = System.currentTimeMillis() + slowChatDelay;
    }
    
    public long getLastReceivedMessageMillis() {
        return this.lastReceivedMessageMillis;
    }
    
    public void setLastReceivedMessageMillis(long lastReceivedMessageMillis) {
        this.lastReceivedMessageMillis = lastReceivedMessageMillis;
    }
    
    public long getLastSentMessageMillis() {
        return this.lastSentMessageMillis;
    }
    
    public void setLastSentMessageMillis(long lastSentMessageMillis) {
        this.lastSentMessageMillis = lastSentMessageMillis;
    }
}
