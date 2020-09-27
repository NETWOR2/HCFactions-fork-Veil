package net.bfcode.bfhcf.tournaments;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.file.TournamentFile;
import net.bfcode.bfhcf.utils.LocationUtils;

import java.util.*;
import org.bukkit.*;

public class Tournament {
	
    private HCFaction plugin;
    private Set<UUID> players;
    private HashMap<Player, ItemStack[]> playerArmor;
    private HashMap<Player, ItemStack[]> playerInventory;
    public Set<UUID> matches;
    private int id;
    private int size;
    private TournamentState tournamentState;
    private int currentRound;
    private int countdown;
    private int pregame;
    private int announceCountdown;
    private TournamentType type;
    private Player hoster;
    private TournamentFile file;
    private Player firstPlayer;
    private Player secondPlayer;
    private long protection;
    
    public Tournament(int size, TournamentType type, Player player) {
        this.plugin = HCFaction.getPlugin();
        this.players = new HashSet<UUID>();
        this.playerArmor = new HashMap<Player, ItemStack[]>();
        this.playerInventory = new HashMap<Player, ItemStack[]>();
        this.matches = new HashSet<UUID>();
        this.tournamentState = TournamentState.WAITING;
        this.currentRound = 1;
        this.file = TournamentFile.getConfig();
        this.size = size;
        this.type = type;
        this.hoster = player;
        this.countdown = 11;
        this.pregame = 6;
        this.announceCountdown = 60;
    }
    
    public boolean isActiveProtection() {
        return this.getProtection() / 1000.0 > 0.0;
    }
    
    public long getProtection() {
        return this.protection - System.currentTimeMillis();
    }
    
    public void setProtection(int time) {
        this.protection = System.currentTimeMillis() + (time + 1) * 1000L;
    }
    
    public void teleport(Player player, String location) {
        player.teleport(LocationUtils.getLocation(this.file.getString("Locations." + location)));
    }
    
    public void addPlayer(UUID uuid) {
        this.players.add(uuid);
    }
    
    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }
    
    public void saveInventory(Player player) {
        this.playerArmor.put(player, player.getInventory().getArmorContents());
        this.playerInventory.put(player, player.getInventory().getContents());
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
    }
    
    public void rollbackInventory(Player player) {
        player.getInventory().setArmorContents((ItemStack[])this.playerArmor.get(player));
        player.getInventory().setContents((ItemStack[])this.playerInventory.get(player));
    }
    
    public void giveItemWait(Player player) {
        player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).displayName(CC.translate("&cLeave event")).lore(CC.translate("&7Right click for exit!")).build());
    }
    
    public void broadcast(String message) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            player.sendMessage(message);
        }
    }
    
    public void broadcastWithSound(String message, Sound sound) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10.0f, 1.0f);
        }
    }
    
    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
    
    public int decrementCountdown() {
        return --this.countdown;
    }
    
    public int decrementAnnounce() {
        return --this.announceCountdown;
    }
    
    public int getDesecrentAnn() {
        return this.announceCountdown;
    }
    
    public int getCooldown() {
        return this.countdown;
    }
    
    public void setPregame(int pregame) {
        this.pregame = pregame;
    }
    
    public int decrementPregame() {
        return --this.pregame;
    }
    
    public int getPregame() {
        return this.pregame;
    }
    
    public void setTournamentState(TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }
    
    public TournamentState getTournamentState() {
        return this.tournamentState;
    }
    
    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
    
    public int getCurrentRound() {
        return this.currentRound;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Set<UUID> getPlayers() {
        return this.players;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public TournamentType getType() {
        return this.type;
    }
    
    public Player getHoster() {
        return this.hoster;
    }
    
    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }
    
    public Player getFirstPlayer() {
        return this.firstPlayer;
    }
    
    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }
    
    public Player getSecondPlayer() {
        return this.secondPlayer;
    }
}
