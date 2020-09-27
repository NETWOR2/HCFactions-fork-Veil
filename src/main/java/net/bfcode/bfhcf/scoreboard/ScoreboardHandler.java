package net.bfcode.bfhcf.scoreboard;

import java.util.List;
import com.google.common.base.Optional;

import java.util.Collections;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.FactionRelationCreateEvent;
import net.bfcode.bfhcf.faction.event.FactionRelationRemoveEvent;
import net.bfcode.bfhcf.faction.event.PlayerJoinedFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeftFactionEvent;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.providers.ScoreboardProvider;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;

public class ScoreboardHandler implements Listener, Runnable
{
    private Map<UUID, PlayerBoard> playerBoards;
    private ScoreboardProvider timerSidebarProvider;
    private HCFaction plugin;
    private boolean threadBased = true;
    private int TICK_IN_MS = 100;
    private long TICK_IN_NANOS;
    private long NANO;
    private boolean running;
    private Thread scoreboardThread;
    
    public ScoreboardHandler(HCFaction plugin) {
        this.running = true;
        this.TICK_IN_NANOS = TimeUnit.MILLISECONDS.toNanos(100L);
        this.NANO = TimeUnit.MILLISECONDS.toNanos(1L);
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.timerSidebarProvider = new ScoreboardProvider(plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.setup();
        (this.scoreboardThread = new Thread(this)).start();
    }
    
    @Deprecated
    public void setup () {
    	for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerBoard playerBoard = new PlayerBoard(plugin, player);
            playerBoard.init(player);
            this.setPlayerBoard(player.getUniqueId(), playerBoard);
        }
    }
    
    @Deprecated
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        synchronized (this.playerBoards) {
            for (PlayerBoard playerBoard : this.playerBoards.values()) {
                playerBoard.init(player);
            }
        }
        PlayerBoard playerBoard2 = new PlayerBoard(this.plugin, player);
        for(Player players : Bukkit.getOnlinePlayers()) {
        	playerBoard2.init(players);
        }
        this.setPlayerBoard(uuid, playerBoard2);
        if (PlayerBoard.INVISIBILITYFIX) {
            new BukkitRunnable() {
                public void run() {
                    if (player.isOnline()) {
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            if (other.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)other).getHandle()));
                            }
                        }
                    }
                }
            }.runTask((Plugin)this.plugin);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        synchronized (this.playerBoards) {
            this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = (Player)optional.get();
            Collection<Player> players = event.getFaction().getOnlinePlayers();
            PlayerBoard playerBoard = this.getPlayerBoard(((Player)event.getPlayer().get()).getUniqueId());
            if (playerBoard != null) {
                playerBoard.setMembers(players);
                List<PlayerFaction> alliedFactions = event.getFaction().getAlliedFactions();
                for (PlayerFaction playerFaction : alliedFactions) {
                    playerBoard.setAllies(playerFaction.getOnlinePlayers());
                }
            }
            for (Player other : players) {
                PlayerBoard otherBoard = this.getPlayerBoard(other.getUniqueId());
                otherBoard.setMembers(Collections.singleton(player));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = (Player)optional.get();
            Collection<Player> players = event.getFaction().getOnlinePlayers();
            PlayerBoard playerBoard = this.getPlayerBoard(event.getUniqueID());
            if (playerBoard != null) {
                playerBoard.setNeutrals(players);
                List<PlayerFaction> alliedFactions = event.getFaction().getAlliedFactions();
                for (PlayerFaction playerFaction : alliedFactions) {
                    playerBoard.setNeutrals(playerFaction.getOnlinePlayers());
                }
            }
            for (Player other : players) {
                PlayerBoard otherBoard = this.getPlayerBoard(other.getUniqueId());
                otherBoard.setNeutrals(Collections.singleton(player));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(FactionRelationCreateEvent event) {
        for (Player player : event.getSenderFaction().getOnlinePlayers()) {
            PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
            playerBoard.setAllies(event.getTargetFaction().getOnlinePlayers());
        }
        for (Player player : event.getTargetFaction().getOnlinePlayers()) {
            PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
            playerBoard.setAllies(event.getSenderFaction().getOnlinePlayers());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(FactionRelationRemoveEvent event) {
        for (Player player : event.getSenderFaction().getOnlinePlayers()) {
            PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
            playerBoard.setNeutrals(event.getTargetFaction().getOnlinePlayers());
        }
        for (Player player : event.getTargetFaction().getOnlinePlayers()) {
            PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
            playerBoard.setNeutrals(event.getSenderFaction().getOnlinePlayers());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInvisibilityExpire(PotionEffectRemoveEvent event) {
        if (PlayerBoard.INVISIBILITYFIX && event.getEntity() instanceof Player && event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId()) {
            Player player = (Player)event.getEntity();
            new BukkitRunnable() {
                public void run() {
                    if (player.isOnline() && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        synchronized (ScoreboardHandler.this.playerBoards) {
                            for (PlayerBoard playerBoard : ScoreboardHandler.this.playerBoards.values()) {
                                playerBoard.init(player);
                            }
                        }
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer)other).getHandle().playerConnection.sendPacket((Packet)PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer)player).getHandle()));
                        }
                    }
                }
            }.runTask((Plugin)this.plugin);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInvisibleDrink(PotionEffectAddEvent event) {
        if (PlayerBoard.INVISIBILITYFIX && event.getEntity() instanceof Player && event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId()) {
            Player player = (Player)event.getEntity();
            new BukkitRunnable() {
                public void run() {
                    if (player.isOnline() && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        synchronized (ScoreboardHandler.this.playerBoards) {
                            for (PlayerBoard playerBoard : ScoreboardHandler.this.playerBoards.values()) {
                                playerBoard.removeAll(player);
                            }
                        }
                    }
                }
            }.runTask((Plugin)this.plugin);
        }
    }
    
    public PlayerBoard getPlayerBoard(UUID uuid) {
        synchronized (this.playerBoards) {
            return this.playerBoards.get(uuid);
        }
    }
    
    public void setPlayerBoard(UUID uuid, PlayerBoard board) {
        synchronized (this.playerBoards) {
            this.playerBoards.put(uuid, board);
        }
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider);
    }
    
    public void tick() {
        long now = System.currentTimeMillis();
        synchronized (this.playerBoards) {
            for (PlayerBoard board : this.playerBoards.values()) {
                if (board.getPlayer().isOnline() && !board.isRemoved()) {
                    try {
                        board.updateObjective(now);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void disable() {
        this.running = false;
        try {
            this.scoreboardThread.join();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        this.clearBoards();
    }
    
    public void run() {
        long start = System.nanoTime();
        while (this.plugin.isEnabled() && this.running) {
            this.tick();
            long finish = System.nanoTime();
            long diff = finish - start;
            if (diff < this.TICK_IN_NANOS) {
                long sleep = (this.TICK_IN_NANOS - diff) / this.NANO;
                if (sleep > 0L) {
                    try {
                        Thread.sleep(sleep);
                    }
                    catch (InterruptedException exception) {
                        exception.printStackTrace();
                        break;
                    }
                }
            }
            start = System.nanoTime();
        }
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public void clearBoards() {
        synchronized (this.playerBoards) {
            Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().remove();
                iterator.remove();
            }
        }
    }
    
    public Map<UUID, PlayerBoard> getPlayerBoards() {
        return this.playerBoards;
    }
}
