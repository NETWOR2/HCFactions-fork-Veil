package net.bfcode.bfhcf.kothgame;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.faction.event.CaptureZoneEnterEvent;
import net.bfcode.bfhcf.faction.event.CaptureZoneLeaveEvent;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.timer.GlobalTimer;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.DateTimeFormats;
import net.minecraft.util.com.google.common.base.Objects;
import net.minecraft.util.com.google.common.base.Preconditions;
import net.minecraft.util.com.google.common.collect.Iterables;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class EventTimer extends GlobalTimer implements Listener {
	
    private static long RESCHEDULE_FREEZE_MILLIS;
    private static String RESCHEDULE_FREEZE_WORDS;
	public static long EVENT_FREQUENCY;
    private HCFaction plugin;
    private long startStamp;
    private Long lastEvent = null;
    private Long nextEvent = null;
    private long lastContestedEventMillis;
    private EventFaction eventFaction;
    private EventFaction lastEventFaction;
    private EventFaction nextEventFaction;
    
    public EventTimer(HCFaction plugin) {
        super(ConfigurationService.EVENT_TIMER, 0L);
        this.plugin = plugin;
        long now = System.currentTimeMillis();
        new BukkitRunnable() {
            public void run() {
                if (EventTimer.this.eventFaction != null) {
                    EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
                    return;
                }
                LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
                int day = now.getDayOfYear();
                int hour = now.getHour();
                int minute = now.getMinute();
                Iterator<Map.Entry<LocalDateTime, String>> iterator = plugin.eventScheduler.getScheduleMap().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<LocalDateTime, String> entry;
                    LocalDateTime scheduledTime;
                    Faction faction;
                    if (day == (scheduledTime = (entry = iterator.next()).getKey()).getDayOfYear() && hour == scheduledTime.getHour() && minute == scheduledTime.getMinute() && (faction = plugin.getFactionManager().getFaction(entry.getValue())) instanceof EventFaction) {
                        if (!EventTimer.this.tryContesting((EventFaction)faction, (CommandSender)Bukkit.getConsoleSender())) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }.runTaskTimer((Plugin)plugin, 20L, 20L);
        Calendar calendar = new GregorianCalendar();
        nextEventFaction = null;
        nextEvent = null;
        if (calendar.getTimeInMillis() < now) {
            nextEvent = calendar.getTimeInMillis();
            List<KothFaction> factions = plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction && !faction.getName().equalsIgnoreCase("palace") && !faction.getName().equalsIgnoreCase("hell") && faction != lastEventFaction).map(faction -> (KothFaction) faction).collect(Collectors.toList());
            int size = factions.size();
            if (size == 0) {
                plugin.getLogger().warning("No available KOTH factions found");
            } else {
                if (size == 1) {
                    nextEventFaction = factions.iterator().next();
                } else {
                    nextEventFaction = factions.get(ThreadLocalRandom.current().nextInt(size));
                }
                return;
            }
        }
    }
    
    public EventFaction getEventFaction() {
        return this.eventFaction;
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.EVENT_COLOUR;
    }
    
    public String getName() {
        return (this.eventFaction == null) ? "Event" : (ChatColor.BOLD.toString() + this.eventFaction.getName());
    }
    
    @Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if (this.eventFaction != null) {
            for (CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }
            this.eventFaction.setDeathban(true);
            this.eventFaction.getEventType().getEventTracker().stopTiming();
            this.eventFaction = null;
            this.eventFaction = null;
            this.startStamp = -1L;
            result = true;
        }
        return result;
    }
    
    @EventHandler
    public void onDecay(LeavesDecayEvent e) {
        if (this.plugin.getFactionManager().getFactionAt(e.getBlock()) != null) {
            e.setCancelled(true);
        }
    }
    
    @Override
    public long getRemaining() {
        if (this.eventFaction == null) {
            return 0L;
        }
        if (this.eventFaction instanceof KothFaction) {
            return ((KothFaction)this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }
    
    @Override
    public long getRemaining1() {
        if (this.eventFaction == null) {
            return 0L;
        }
        if (this.eventFaction instanceof CitadelFaction) {
            return ((CitadelFaction)this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }
    
    public void handleWinnerKoth(Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
        if(this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId()) != null) {
        	playerFaction.setPoints(playerFaction.getPoints() + HCFaction.getPlugin().getConfig().getInt("Points.win-per-koth"));
        	playerFaction.broadcast(CC.translate("&a" + winner.getName() + " &eHas gotten &6" + HCFaction.getPlugin().getConfig().getInt("Points.win-per-koth") + " &epoint &efor your faction by capping a KoTH"));
        }
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD + ((playerFaction == null) ? winner.getName() : (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]")) + ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.YELLOW + " of up-time");
        winner.getWorld();
        winner.getLocation();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_KOTH").replace("%player%", winner.getName()));
    }
    
    public void handleWinnerConquest(Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD + ((playerFaction == null) ? winner.getName() : (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]")) + ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.YELLOW + " of up-time");
        winner.getWorld();
        winner.getLocation();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");
    	playerFaction.setPoints(playerFaction.getPoints() + HCFaction.getPlugin().getConfig().getInt("Points.win-per-conquest"));
    	playerFaction.broadcast(CC.translate("&a" + winner.getName() + " &eHas gotten &6" + HCFaction.getPlugin().getConfig().getInt("Points.win-per-conquest") + " &epoint &efor your faction by capping the Conquest"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_CONQUEST").replace("%player%", winner.getName()));
    }
    
    public void handleWinnerCitadel(Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD + ((playerFaction == null) ? winner.getName() : (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]")) + ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.YELLOW + " of up-time");
        winner.getWorld();
        winner.getLocation();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");
    	playerFaction.setPoints(playerFaction.getPoints() + HCFaction.getPlugin().getConfig().getInt("Points.win-per-citadel"));
    	playerFaction.broadcast(CC.translate("&a" + winner.getName() + " &eHas gotten &6" + HCFaction.getPlugin().getConfig().getInt("Points.win-per-citadel") + " &epoint &efor your faction by capping the Citadel"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_CITADEL").replace("%player%", winner.getName()));
    }
    
    public void handleWinner1(Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
        Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + ((playerFaction == null) ? winner.getName() : (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]")) + ChatColor.GOLD + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + '.');
        winner.getWorld();
        winner.getLocation();
        this.clearCooldown();
    }
    
    public boolean tryContesting(EventFaction eventFaction, CommandSender sender) {
        if (this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
            return false;
        }
        if (eventFaction instanceof KothFaction) {
            KothFaction kothFaction = (KothFaction)eventFaction;
            if (kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        }
        if (eventFaction instanceof CitadelFaction) {
            CitadelFaction citadelFaction = (CitadelFaction)eventFaction;
            if (citadelFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        }
        else if (eventFaction instanceof ConquestFaction) {
            ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
            Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
            for (ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
                if (!zones.contains(zone)) {
                    sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
                    return false;
                }
            }
        }
        long millis = System.currentTimeMillis();
        if (this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
            sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
            return false;
        }
        this.lastContestedEventMillis = millis;
        this.startStamp = millis;
        this.eventFaction = eventFaction;
        eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
        if (eventFaction instanceof ConquestFaction) {
            this.setRemaining(1000L, true);
            this.setPaused(true);
        }
        Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            if (captureZone.isActive()) {
                Player player = (Player)Iterables.getFirst((Iterable)captureZone.getCuboid().getPlayers(), (Object)null);
                if (player == null) {
                    continue;
                }
                if (!eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
                    continue;
                }
                captureZone.setCappingPlayer(player);
            }
        }
        eventFaction.setDeathban(true);
        return true;
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - this.startStamp;
    }
    
    public long getStartStamp() {
        return this.startStamp;
    }

    public EventFaction getNextEventFaction() {
        return nextEventFaction;
    }

    public Long getNextEvent() {
        return nextEvent;
    }

    public EventFaction getLastEventFaction() {
        return lastEventFaction;
    }
    
    private void handleDisconnect(Player player) {
        Preconditions.checkNotNull((Object)player);
        if (this.eventFaction == null) {
            return;
        }
        Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            if (Objects.equal((Object)captureZone.getCappingPlayer(), (Object)player)) {
                this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
                captureZone.setCappingPlayer(null);
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEnderpearl(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl && (((EnderPearl)projectile).getShooter()) instanceof Player) {
            this.handleDisconnect((Player)projectile.getShooter());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        if (this.eventFaction == null) {
            return;
        }
        CaptureZone captureZone = event.getCaptureZone();
        if (!this.eventFaction.getCaptureZones().contains(captureZone)) {
            return;
        }
        Player player = event.getPlayer();
        if (captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
        if (Objects.equal((Object)event.getFaction(), (Object)this.eventFaction)) {
            Player player = event.getPlayer();
            CaptureZone captureZone = event.getCaptureZone();
            if (Objects.equal((Object)player, (Object)captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
                captureZone.setCappingPlayer(null);
                for (Player target : captureZone.getCuboid().getPlayers()) {
                    if (target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
    
    static {
        RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(EventTimer.RESCHEDULE_FREEZE_MILLIS, true, true);
    }
}
