package net.bfcode.bfhcf.faction.type;

import net.minecraft.util.com.google.common.collect.Sets;
import java.util.Collections;
import com.google.common.base.Preconditions;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.GenericUtils;
import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfbase.util.PersistableLocation;
import net.bfcode.bfbase.util.chat.ClickAction;
import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.event.FactionDtrChangeEvent;
import net.bfcode.bfhcf.faction.event.PlayerJoinedFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeaveFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeftFactionEvent;
import net.bfcode.bfhcf.faction.event.cause.FactionLeaveCause;
import net.bfcode.bfhcf.faction.struct.Raidable;
import net.bfcode.bfhcf.faction.struct.RegenStatus;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.timer.type.TeleportTimer;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.StringUtils;
import com.google.common.base.Objects;
import org.bukkit.Location;
import java.util.HashSet;
import org.bukkit.command.CommandSender;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.bukkit.ChatColor;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

public class PlayerFaction extends ClaimableFaction implements Raidable {
    private static final UUID[] EMPTY_UUID_ARRAY;
    protected Map<UUID, Relation> requestedRelations;
    protected Map<UUID, Relation> relations;
    protected Map<UUID, FactionMember> members;
    protected final Set<String> invitedPlayerNames;
    protected PersistableLocation home;
    protected String announcement;
    protected UUID focus;
    protected boolean open;
    protected int balance;
    private String tempcoords;
    private int kothCaptures;
    private int conquestCaptures;
    protected double deathsUntilRaidable;
    protected long regenCooldownTimestamp;
    private long lastDtrUpdateTimestamp;
    private transient String focused;
    private int points;
    private int totalKills;
    private boolean friendyFire;
    
    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }
    
    public PlayerFaction(final String name) {
        super(name);
        this.totalKills = 0;
        this.requestedRelations = new HashMap<UUID, Relation>();
        this.relations = new HashMap<UUID, Relation>();
        this.members = new HashMap<UUID, FactionMember>();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public PlayerFaction(final Map map) {
        super(map);
        this.totalKills = 0;
        this.requestedRelations = new HashMap<UUID, Relation>();
        this.relations = new HashMap<UUID, Relation>();
        this.members = new HashMap<UUID, FactionMember>();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
        for (final Map.Entry entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
            this.members.put(UUID.fromString((String) entry.getKey()), (FactionMember) entry.getValue());
        }
        this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));
        Object object2 = map.get("home");
        if (object2 != null) {
            this.home = (PersistableLocation)object2;
        }
        object2 = map.get("announcement");
        if (object2 != null) {
            this.announcement = (String)object2;
        }
        for (final Map.Entry entry2 : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
            this.relations.put(UUID.fromString((String) entry2.getKey()), Relation.valueOf((String) entry2.getValue()));
        }
        for (final Map.Entry entry2 : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
            this.requestedRelations.put(UUID.fromString((String) entry2.getKey()), Relation.valueOf((String) entry2.getValue()));
        }
        this.open = (boolean)map.get("open");
        this.balance = (int)map.get("balance");
        this.deathsUntilRaidable = (double)map.get("deathsUntilRaidable");
        this.tempcoords = (String) map.get("temporalCoords");
        this.points = (int)map.get("points");
        this.totalKills = (int)map.get("totalKills");
        this.kothCaptures = (int)map.get("kothCaptures");
        this.conquestCaptures = (int)map.get("conquestCaptures");
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
    }
    
    public int getTotalKillsFaction() {
        return this.totalKills;
    }
    
    public int setTotalKillsFaction(final int kills) {
        return this.totalKills = kills;
    }
    
    public static String format(final String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
    
    public String getTemporalCoords() {
    	return tempcoords;
    }
    
    public String setTemporalCoords(String coords) {
    	return tempcoords = coords;
    }
    
    public String getFocused() {
        return this.focused;
    }
    
    public void setFocused(final String focused) {
        this.focused = focused;
    }
    
    public boolean isFriendlyFire() {
    	return this.friendyFire;
    }
    
    public void setFriendlyFire(final boolean newFriendlyFire) {
        this.friendyFire = newFriendlyFire;
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        final Map<String, String> relationSaveMap = new HashMap<String, String>(this.relations.size());
        for (final Map.Entry<UUID, Relation> entry : this.relations.entrySet()) {
            relationSaveMap.put(entry.getKey().toString(), entry.getValue().name());
        }
        map.put("relations", relationSaveMap);
        final Map<String, String> requestedRelationsSaveMap = new HashMap<String, String>(this.requestedRelations.size());
        for (final Map.Entry<UUID, Relation> entry2 : this.requestedRelations.entrySet()) {
            requestedRelationsSaveMap.put(entry2.getKey().toString(), entry2.getValue().name());
        }
        map.put("requestedRelations", requestedRelationsSaveMap);
        final Set<Map.Entry<UUID, FactionMember>> entrySet = this.members.entrySet();
        final Map<String, FactionMember> saveMap = new LinkedHashMap<String, FactionMember>(this.members.size());
        for (final Map.Entry<UUID, FactionMember> entry3 : entrySet) {
            saveMap.put(entry3.getKey().toString(), entry3.getValue());
        }
        map.put("members", saveMap);
        map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));
        if (this.home != null) {
            map.put("home", this.home);
        }
        if (this.announcement != null) {
            map.put("announcement", this.announcement);
        }
        map.put("open", this.open);
        map.put("balance", this.balance);
        map.put("deathsUntilRaidable", this.deathsUntilRaidable);
        map.put("points", this.points);
        map.put("totalKills", this.totalKills);
        map.put("temporalCoords", tempcoords);
        map.put("kothCaptures", this.kothCaptures);
        map.put("conquestCaptures", this.conquestCaptures);
        map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
        return map;
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember, final boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }
    
    private boolean setMember(final Player player, final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        if (factionMember == null) {
            if (!force) {
                final PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            final PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent((Event)event2);
            return true;
        }
        final PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent((Event)eventPre);
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }
    
	public Collection<UUID> getAllied() {
        return (Collection<UUID>)Maps.filterValues((Map<UUID, Relation>)this.relations, (Predicate<Relation>)new Predicate<Relation>() {
            public boolean apply(@Nullable final Relation relation) {
                return relation == Relation.ALLY;
            }
        }).keySet();
    }
    
    public List<PlayerFaction> getAlliedFactions() {
        final Collection<UUID> allied = this.getAllied();
        final Iterator<UUID> iterator = allied.iterator();
        final List<PlayerFaction> results = new ArrayList<PlayerFaction>(allied.size());
        while (iterator.hasNext()) {
            final Faction faction = HCFaction.getPlugin().getFactionManager().getFaction(iterator.next());
            if (faction instanceof PlayerFaction) {
                results.add((PlayerFaction)faction);
            }
            else {
                iterator.remove();
            }
        }
        return results;
    }
    
    public Map<UUID, Relation> getRequestedRelations() {
        return this.requestedRelations;
    }
    
    public Map<UUID, Relation> getRelations() {
        return this.relations;
    }
    
	public Map<UUID, FactionMember> getMembers() {
        return (Map<UUID, FactionMember>)ImmutableMap.copyOf((Map<UUID, FactionMember>)this.members);
    }
    
    public Set<Player> getOnlinePlayers() {
        return this.getOnlinePlayers(null);
    }
    
    public Set<Player> getOnlinePlayers(final CommandSender sender) {
        final Set<Map.Entry<UUID, FactionMember>> entrySet = this.getOnlineMembers(sender).entrySet();
        final Set<Player> results = new HashSet<Player>(entrySet.size());
        for (final Map.Entry<UUID, FactionMember> entry : entrySet) {
            results.add(Bukkit.getPlayer((UUID)entry.getKey()));
        }
        return results;
    }
    
    public Map<?, ?> getOnlineMembers() {
        return this.getOnlineMembers(null);
    }
    
    public Map<UUID, FactionMember> getOnlineMembers(final CommandSender sender) {
        final Player senderPlayer = (sender instanceof Player) ? ((Player) sender) : null;
        final HashMap<UUID, FactionMember> results = new HashMap<UUID, FactionMember>();
        for (final Map.Entry<UUID, FactionMember> entry : this.members.entrySet()) {
            final Player target = Bukkit.getPlayer((UUID)entry.getKey());
            if (target != null && (senderPlayer == null || senderPlayer.canSee(target))) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
    
    public FactionMember getLeader() {
        final Map<UUID, FactionMember> members = this.members;
        final Iterator<Map.Entry<UUID, FactionMember>> iterator = members.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, FactionMember> entry;
            if ((entry = iterator.next()).getValue().getRole() == Role.LEADER) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Deprecated
    public FactionMember getMember(final String memberName) {
        final UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        final FactionMember factionMember = this.members.get(uuid);
        return factionMember;
    }
    
    public FactionMember getMember(final Player player) {
        return this.getMember(player.getUniqueId());
    }
    
    public FactionMember getMember(final UUID memberUUID) {
        return this.members.get(memberUUID);
    }
    
    public Set<String> getInvitedPlayerNames() {
        return this.invitedPlayerNames;
    }
    
    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }
    
    public void setHome(final Location home) {
        if (home == null && this.home != null) {
            final TeleportTimer timer = HCFaction.getPlugin().getTimerManager().teleportTimer;
            for (final Player player : this.getOnlinePlayers()) {
                final Location destination = (Location)timer.getDestination(player);
                if (Objects.equal(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getDisplayName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }
        this.home = ((home == null) ? null : new PersistableLocation(home));
    }
    
    public String getAnnouncement() {
        return this.announcement;
    }
    
    public void setAnnouncement(@Nullable final String announcement) {
        this.announcement = announcement;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public int getBalance() {
        return this.balance;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public int getKothCaptures() {
        return this.kothCaptures;
    }
    
    public void setKothCaptures(final int kothCaptures) {
        this.kothCaptures = kothCaptures;
    }
    
    public int getConquestCaptures() {
        return this.conquestCaptures;
    }
    
    public void setConquestCaptures(final int conquestCaptures) {
        this.conquestCaptures = conquestCaptures;
    }
    
    public void setBalance(final int balance) {
        this.balance = balance;
    }
    
    public void setPoints(final int points) {
        this.points = points;
    }
    
    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }
    
    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }
    
    @Override
    public double getMaximumDeathsUntilRaidable() {
        if (this.members.size() == 1) {
            return 1.1;
        }
        return Math.min(5.5, this.members.size() * 1.0 + 0.1);
    }
    
    public double getDeathsUntilRaidable(final boolean updateLastCheck) {
        if (updateLastCheck) {
            this.updateDeathsUntilRaidable();
        }
        return this.deathsUntilRaidable;
    }
    
    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();
        if (this.deathsUntilRaidable < 0.0) {
            return ChatColor.RED;
        }
        if (this.deathsUntilRaidable < 1.0) {
            return ChatColor.GREEN;
        }
        return ChatColor.GREEN;
    }
    
    private void updateDeathsUntilRaidable() {
        if (this.getRegenStatus() == RegenStatus.REGENERATING) {
            final long now = System.currentTimeMillis();
            final long millisPassed = now - this.lastDtrUpdateTimestamp;
            if (millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES) {
                final long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES;
                final int multiplier = (int)((millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
                final double increase = multiplier * 1.0;
                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }
    
    @Override
    public double setDeathsUntilRaidable(final double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }
    
    private double setDeathsUntilRaidable(double deathsUntilRaidable, final boolean limit) {
        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if (limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if (deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            final FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if (deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    HCFaction.getPlugin().getLogger().info("Faction " + this.getName() + " is now raidable.");
                }
                this.lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }
    
    protected long getRegenCooldownTimestamp() {
        return this.regenCooldownTimestamp;
    }
    
    @Override
    public long getRemainingRegenerationTime() {
        return (this.regenCooldownTimestamp == 0L) ? 0L : (this.regenCooldownTimestamp - System.currentTimeMillis());
    }
    
    @Override
    public void setRemainingRegenerationTime(final long millis) {
        final long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;
        this.lastDtrUpdateTimestamp = systemMillis + ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2L;
    }
    
    @Override
    public RegenStatus getRegenStatus() {
        if (this.getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        }
        if (this.getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        }
        return RegenStatus.FULL;
    }
    
    public void regenProccess() {
    }
    
    @SuppressWarnings({ "unused", "rawtypes", "deprecation" })
	@Override
    public void printDetails(final CommandSender sender) {
        String leaderName = null;
        final HashSet<String> coleaderName = new HashSet<String>();
        final HashSet<String> allyNames = new HashSet<String>(1);
        for (final Map.Entry<UUID, Relation> memberNames : this.relations.entrySet()) {
            final Faction captainNames = HCFaction.getPlugin().getFactionManager().getFaction(memberNames.getKey());
            if (captainNames instanceof PlayerFaction) {
                final PlayerFaction playerFaction = (PlayerFaction)captainNames;
                allyNames.add(String.valueOf(playerFaction.getDisplayName(sender)) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
            }
        }
        final HashSet<String> memberNames2 = new HashSet<String>();
        final int combinedKills2 = this.getTotalKillsFaction();
        final HashSet<String> captainNames2 = new HashSet<String>();
        for (final Map.Entry entry : this.members.entrySet()) {
            final FactionMember factionMember = (FactionMember) entry.getValue();
            final Player target = factionMember.toOnlinePlayer();
            final FactionUser user = HCFaction.getPlugin().getUserManager().getUser((UUID) entry.getKey());
            final Deathban deathban = user.getDeathban();
            final int kills = user.getKills();
            ChatColor colour;
            if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
                colour = ChatColor.GRAY;
            }
            else {
                colour = ChatColor.GREEN;
            }
            if (deathban != null && deathban.isActive()) {
                colour = ChatColor.RED;
            }
            final String memberName = colour + factionMember.getName() + ChatColor.GRAY + '[' + ChatColor.WHITE + kills + ChatColor.GRAY + ']';
            switch (factionMember.getRole()) {
                default: {
                    continue;
                }
                case LEADER: {
                    leaderName = memberName;
                    continue;
                }
                case COLEADER: {
                    coleaderName.add(memberName);
                    continue;
                }
                case CAPTAIN: {
                    captainNames2.add(memberName);
                    continue;
                }
                case MEMBER: {
                    memberNames2.add(memberName);
                    continue;
                }
            }
        }
        final long dtrRegenRemaining = this.getRemainingRegenerationTime();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + ChatColor.stripColor(this.getDisplayName(sender)) + ChatColor.WHITE + " (" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + " Online)" + ChatColor.AQUA + " Status" + ChatColor.GRAY + ": " + (this.isOpen() ? (ChatColor.GREEN + "Open") : (ChatColor.RED + "Closed")));
        sender.sendMessage(ChatColor.AQUA + "  Home" + ChatColor.GRAY + ": " + ((this.home == null) ? (ChatColor.WHITE + "Not Set") : (String.valueOf(ChatColor.WHITE.toString()) + this.home.getLocation().getBlockX() + ", " + this.home.getLocation().getBlockZ())));
        if (!allyNames.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "  Allies" + ChatColor.GRAY + ": " + StringUtils.join((Collection)allyNames, ChatColor.GRAY + ", "));
        }
        if (leaderName != null) {
            sender.sendMessage(ChatColor.AQUA + "  Leader" + ChatColor.GRAY + ": " + leaderName);
        }
        if (!coleaderName.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "  Co-Leader" + ChatColor.GRAY + ": " + StringUtils.join((Collection)coleaderName, ChatColor.GRAY + ", "));
        }
        if (!captainNames2.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "  Captains" + ChatColor.GRAY + ": " + StringUtils.join((Collection)captainNames2, ChatColor.GRAY + ", "));
        }
        if (!memberNames2.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "  Members" + ChatColor.GRAY + ": " + StringUtils.join((Collection)memberNames2, ChatColor.GRAY + ", "));
        }
        sender.sendMessage(ChatColor.AQUA + "  Balance" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + '$' + this.balance);
        sender.sendMessage(CC.translate("  &bPoints&7: &f" + this.points));
        if (this.totalKills > 0) {
            sender.sendMessage(CC.translate("  &bTotal Kills&7: &f" + this.totalKills));
        }
        if (this.kothCaptures > 0) {
            sender.sendMessage(CC.translate("  &bKOTH Captured&7: &f" + this.kothCaptures));
        }
        if (this.conquestCaptures > 0) {
            sender.sendMessage(CC.translate("  &bConquest Captured&7: &f" + this.conquestCaptures));
        }
        if (!sender.hasPermission("rank.staff")) {
            sender.sendMessage(ChatColor.AQUA + "  Deaths Until Raidable§7: " + this.getDtrColour() + JavaUtils.format((Number)this.getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + this.getMaximumDeathsUntilRaidable() + this.getRegenStatus().getSymbol());
        }
        else {
            sender.sendMessage(ChatColor.AQUA + "  Deaths Until Raidable§7: " + this.getDtrColour() + JavaUtils.format((Number)this.getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + this.getMaximumDeathsUntilRaidable() + this.getRegenStatus().getSymbol());
        }
        if (dtrRegenRemaining > 0L) {
            if (sender.hasPermission("rank.staff")) {
                final Text beforeRegen = new Text(ChatColor.AQUA + "  Time Until Regen" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true) + " ");
                final Text regen = new Text(ChatColor.GRAY + "(Remove)");
                regen.setHoverText(ChatColor.GRAY + "Click to remove regen delay.");
                regen.setClick(ClickAction.RUN_COMMAND, "/f setdtrregen " + ChatColor.stripColor(String.valueOf(this.getDisplayName(sender)) + " 0s"));
                beforeRegen.append((IChatBaseComponent)regen).send(sender);
            }
            else {
                sender.sendMessage(ChatColor.AQUA + "  Time Until Regen" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
            }
        }
        if (sender instanceof Player) {
            final Faction playerFaction2 = HCFaction.getPlugin().getFactionManager().getPlayerFaction((Player)sender);
            if (playerFaction2 != null && this.announcement != null && playerFaction2.equals(this)) {
                sender.sendMessage(ChatColor.AQUA + "  Announcement" + ChatColor.GRAY + ": " + ChatColor.WHITE + this.announcement);
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
    }
    
    public void broadcast(final String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String message, @Nullable final UUID... ignore) {
        this.broadcast(new String[] { message }, ignore);
    }
    
    public void broadcast(final String[] messages, final UUID... ignore) {
        Preconditions.checkNotNull(messages, "Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, "Message array cannot be empty");
        final Collection<Player> players = this.getOnlinePlayers();
        final Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        for (final Player player : players) {
            if (!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }
}
