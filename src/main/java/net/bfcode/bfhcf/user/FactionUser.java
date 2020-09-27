package net.bfcode.bfhcf.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.ArrayList;
import com.google.common.collect.Maps;

import net.bfcode.bfbase.util.GenericUtils;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class FactionUser implements ConfigurationSerializable
{
    private Set<UUID> factionChatSpying;
    private Set<String> shownScoreboardScores;
    private UUID userUUID;
    private boolean reclaimed;
    private boolean capzoneEntryAlerts;
    private boolean showClaimMap;
    private boolean showLightning;
    private Deathban deathban;
    private PlayerFaction playerFaction;
    private Faction faction;
    private long lastFactionLeaveMillis;
    private int kills;
    private int diamondsMined;
    private int deaths;
    private boolean fdalerts = true;
    private boolean lffalerts = true;
    private long lastPanic = 0;
    private int killStreaks;
    private boolean homeRange = true;
    
    public FactionUser(UUID userUUID) {
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        fdalerts = true;
        lffalerts = true;
        homeRange = true;
        this.userUUID = userUUID;
    }
    
    public FactionUser(Map<String, Object> map) {
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        fdalerts = (Boolean) map.getOrDefault("fdalerts", true);
        lffalerts = (Boolean) map.getOrDefault("lffalerts", true);
        homeRange = (Boolean) map.getOrDefault("homeRange", true);
        this.shownScoreboardScores.addAll(GenericUtils.createList(map.get("shownScoreboardScores"), String.class));
        this.factionChatSpying.addAll(GenericUtils.createList(map.get("faction-chat-spying"), (Class)String.class));
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.capzoneEntryAlerts = (boolean) map.get("capzoneEntryAlerts");
        this.showLightning = (boolean) map.get("showLightning");
        this.deathban = (Deathban) map.get("deathban");
        this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
        this.diamondsMined = (int) map.get("diamonds");
        this.kills = (int) map.get("kills");
        this.deaths = (int) map.get("deaths");
        this.killStreaks = (int) map.get("killstreaks");
        this.reclaimed = (boolean) map.getOrDefault("reclaimed", false);
        lastPanic = Long.parseLong((String) map.getOrDefault("lastPanic", "0"));
    }
    
    public Map<String, Object> serialize() {
        LinkedHashMap map = Maps.newLinkedHashMap();
        map.put("shownScoreboardScores", new ArrayList(this.shownScoreboardScores));
        map.put("faction-chat-spying", this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("userUUID", this.userUUID.toString());
        map.put("diamonds", this.diamondsMined);
        map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
        map.put("showClaimMap", this.showClaimMap);
        map.put("showLightning", this.showLightning);
        map.put("deathban", this.deathban);
        map.put("fdalerts", fdalerts);
        map.put("lffalerts", lffalerts);
        map.put("homeRange", homeRange);
        map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        map.put("killstreaks", this.killStreaks);
        map.put("reclaimed", this.reclaimed);
        map.put("lastPanic", Long.toString(lastPanic));
        return (Map<String, Object>)map;
    }
    
    public boolean isCapzoneEntryAlerts() {
        return this.capzoneEntryAlerts;
    }
    
    public boolean isShowClaimMap() {
        return this.showClaimMap;
    }

    public long getLastPanic() {
        return lastPanic;
    }

    public void setLastPanic(long lastPanic) {
        this.lastPanic = lastPanic;
    }
    
    public void setShowClaimMap(boolean showClaimMap) {
        this.showClaimMap = showClaimMap;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getDiamondsMined() {
        return this.diamondsMined;
    }
    
    public void setDiamondsMined(int diamondsMined) {
        this.diamondsMined = diamondsMined;
    }
    
    public Deathban getDeathban() {
        return this.deathban;
    }
    
    public void setDeathban(Deathban deathban) {
        this.deathban = deathban;
    }

    public boolean isFdalerts() {
        return fdalerts;
    }

    public void setFdalerts(boolean fdalerts) {
        this.fdalerts = fdalerts;
    }

    public boolean isLffalerts() {
        return lffalerts;
    }

    public void setLffalerts(boolean lffalerts) {
        this.lffalerts = lffalerts;
    }
    
    public void removeDeathban() {
        this.deathban = null;
    }
    
    public long getLastFactionLeaveMillis() {
        return this.lastFactionLeaveMillis;
    }
    
    public void setLastFactionLeaveMillis(long lastFactionLeaveMillis) {
        this.lastFactionLeaveMillis = lastFactionLeaveMillis;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }
    
    public int getKillStreaks() {
    	return killStreaks;
    }
    
    public void setKillStreaks(Integer killstreaks) {
    	this.killStreaks = killstreaks;
    }
    
    public boolean isShowLightning() {
        return this.showLightning;
    }
    
    public void setShowLightning(boolean showLightning) {
        this.showLightning = showLightning;
    }
    
    public Set<UUID> getFactionChatSpying() {
        return this.factionChatSpying;
    }
    
    public Set<String> getShownScoreboardScores() {
        return this.shownScoreboardScores;
    }
    
    public UUID getUserUUID() {
        return this.userUUID;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(this.userUUID);
    }
    
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.userUUID);
    }
    
    public boolean isReclaimed() {
        return this.reclaimed;
    }
    
    public void setReclaimed(boolean reclaimed) {
        this.reclaimed = reclaimed;
    }

	public PlayerFaction getPlayerFaction() {
		return this.playerFaction;
	}

	public Faction getFaction() {
		return this.faction;
	}
	
	public boolean getHomeRangeMode() {
		return homeRange;
	}
	
	public boolean setHomeRangeMode(boolean mode) {
		return this.homeRange = mode;
	}
}
