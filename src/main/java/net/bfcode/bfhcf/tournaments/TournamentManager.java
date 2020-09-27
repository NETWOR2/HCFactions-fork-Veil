package net.bfcode.bfhcf.tournaments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.hatsur.library.Library;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.runnable.TournamentRunnable;
import net.bfcode.bfhcf.utils.CC;

public class TournamentManager {

	private HCFaction plugin;
    public Map<UUID, Integer> matches;
    private Tournament tournament;
    private List<UUID> players;
    private boolean created;
    public static Map<Location, Material> spleefblocks;
    
    static {
    	spleefblocks = new HashMap<Location, Material>();
    }
    
    public TournamentManager() {
        this.plugin = HCFaction.getPlugin();
        this.matches = new HashMap<UUID, Integer>();
        this.players = new ArrayList<UUID>();
        this.created = false;
    }
    
    public boolean isInTournament(UUID uuid) {
        return this.players.contains(uuid);
    }
    
    public boolean isInTournament(Player player) {
        return this.players.contains(player.getUniqueId());
    }
    
    public void kickPlayer(UUID uuid) {
        this.players.remove(uuid);
    }
    
    public void createTournament(CommandSender commandSender, int size, TournamentType type, Player player) {
        Tournament tournament = new Tournament(size, type, player);
        this.tournament = tournament;
        TournamentRunnable runnable = new TournamentRunnable(tournament);
        if (type == TournamentType.SUMO) {
            runnable.startSumo();
        }
        commandSender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        commandSender.sendMessage(CC.translate("&4&lThe event was successfully created!"));
        commandSender.sendMessage(CC.translate("&7Max Players: &f" + size));
        commandSender.sendMessage(CC.translate("&7Event Type: &f" + type));
        commandSender.sendMessage(CC.translate("&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        this.created = true;
    }
    
	public void playerLeft(Tournament tournament, Player player, boolean message) {
        if (message) {
            player.sendMessage(CC.translate("&cYou are leave the event!"));
            tournament.rollbackInventory(player);
        }
        this.players.remove(player.getUniqueId());
        for (PotionEffect effects : player.getActivePotionEffects()) {
            player.removePotionEffect(effects.getType());
        }
        if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
            this.plugin.getTimerManager().spawnTagTimer.clearCooldown(player);
        }
        tournament.removePlayer(player.getUniqueId());
        if (message) {
            tournament.broadcast(CC.translate("&a" + player.getDisplayName() + " &chave left the event. &7(" + tournament.getPlayers().size() + '/' + tournament.getSize() + ')'));
        }
        if (player.isOnline()) {
            tournament.rollbackInventory(player);
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        if (this.players.size() == 1) {
        	if (tournament.getTournamentState() != TournamentState.FIGHTING) {
        		return;
        	}
            Player winner = Bukkit.getPlayer((UUID)this.players.get(0));
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            	online.sendMessage(new String[2]);
                online.sendMessage(CC.translate("&7[&4&l" + tournament.getType().toString() + "&7] &r" + Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(winner.getUniqueId()).getColoredUsername() + " &chas won the event!"));
                online.sendMessage(new String[2]);
                online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 2.0f, 2.0f);
                tournament.rollbackInventory(winner);
                for (PotionEffect effects2 : winner.getActivePotionEffects()) {
                    winner.removePotionEffect(effects2.getType());
                }
            }
            this.plugin.getTournamentManager().setCreated(false);
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            	tournament.rollbackInventory(player);
                if (this.plugin.getTournamentManager().isInTournament(online.getUniqueId())) {
                    this.players.remove(online.getUniqueId());
                    online.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    tournament.rollbackInventory(player);
                    for (PotionEffect effects2 : online.getActivePotionEffects()) {
                        online.removePotionEffect(effects2.getType());
                    }
                }
            }
            if (this.getTournament().getType() == TournamentType.SUMO) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_SUMO").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.DIAMOND) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_FFA").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.BARD) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_FFA").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.ASSASSIN) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_FFA").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.BOMBER) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_FFA").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.AXE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_AXE").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.ARCHER) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_ARCHER").replace("%player%", winner.getName()));
            }
            else if(this.getTournament().getType() == TournamentType.SPLEEF) {
            	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_SPLEEF").replace("%player%", winner.getName()));
				for(Location loc : spleefblocks.keySet()) {
					loc.getBlock().setType(Material.SNOW_BLOCK);
				}
            }
            else if(this.getTournament().getType() == TournamentType.TNTTAG) {
            	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), HCFaction.getPlugin().getConfig().getString("COMMANDS_EVENT.WIN_TNTTAG").replace("%player%", winner.getName()));
            }
        }
        else if (this.players.size() == 0) {
            this.plugin.getTournamentManager().setCreated(false);
        }
        else if (this.players.size() > 1) {
            TournamentRunnable runnable = new TournamentRunnable(tournament);
            if (this.plugin.getTournamentManager().getTournament().getType() == TournamentType.SUMO) {
                tournament.setCountdown(10);
                tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                runnable.startSumo();
            }
        }
    }
    
    public void leaveTournament(Player player) {
        if (!this.isInTournament(player.getUniqueId())) {
            return;
        }
        this.playerLeft(this.tournament, player, true);
    }
    
    private void playerJoined(Tournament tournament, Player player) {
        tournament.addPlayer(player.getUniqueId());
        this.players.add(player.getUniqueId());
        player.setFoodLevel(20);
        player.setHealth(20.0);
        for (PotionEffect effects : player.getActivePotionEffects()) {
            player.removePotionEffect(effects.getType());
        }
        tournament.broadcast(CC.translate("&a" + player.getDisplayName() + " &ahave join the event. &7(" + tournament.getPlayers().size() + '/' + tournament.getSize() + ')'));
    }
    
    public void joinTournament(Player player) {
        Tournament tournament = this.tournament;
        if (this.players.size() >= tournament.getSize()) {
            player.sendMessage(CC.translate("&cThis event is full!"));
        }
        else {
            this.playerJoined(tournament, player);
        }
    }
    
    public void forceStart() {
        if (this.tournament.getType() == TournamentType.DIAMOND || this.tournament.getType() == TournamentType.BARD || this.tournament.getType() == TournamentType.ASSASSIN || this.tournament.getType() == TournamentType.BOMBER) {
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                this.tournament.teleport(online, "FFA.Spawn");
            }
            this.tournament.setTournamentState(TournamentState.FIGHTING);
            this.tournament.setProtection(10);
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                PlayerInventory inventory = online.getInventory();
                inventory.clear();
                inventory.setItem(0, new ItemBuilder(Material.ENCHANTED_BOOK).displayName(CC.translate("&6Default Kit")).lore(CC.translate("&7Right click for equip kit!")).build());
                online.updateInventory();
                online.setGameMode(GameMode.SURVIVAL);
                online.setAllowFlight(false);
                online.setFlying(false);
                online.setFoodLevel(20);
            }
        }
        else if (this.tournament.getType() == TournamentType.AXE) {
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                this.tournament.teleport(online, "Axe.Spawn");
            }
            this.tournament.setTournamentState(TournamentState.FIGHTING);
            this.tournament.setProtection(10);
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                PlayerInventory inventory = online.getInventory();
                inventory.clear();
                inventory.setItem(0, new ItemBuilder(Material.ENCHANTED_BOOK).displayName(CC.translate("&6Default Kit")).lore(CC.translate("&7Right click for equip kit!")).build());
                online.updateInventory();
                online.setGameMode(GameMode.SURVIVAL);
                online.setAllowFlight(false);
                online.setFlying(false);
                online.setFoodLevel(20);
            }
        }
        else if (this.tournament.getType() == TournamentType.ARCHER) {
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                this.tournament.teleport(online, "Archer.Spawn");
            }
            this.tournament.setTournamentState(TournamentState.FIGHTING);
            this.tournament.setProtection(10);
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                PlayerInventory inventory = online.getInventory();
                inventory.clear();
                inventory.setItem(0, new ItemBuilder(Material.ENCHANTED_BOOK).displayName(CC.translate("&6Default Kit")).lore(CC.translate("&7Right click for equip kit!")).build());
                online.updateInventory();
                online.setGameMode(GameMode.SURVIVAL);
                online.setAllowFlight(false);
                online.setFlying(false);
                online.setFoodLevel(20);
            }
        }
        else if(this.tournament.getType() == TournamentType.SPLEEF) {
        	for(UUID players : this.players) {
        		Player online = Bukkit.getPlayer(players);
        		PlayerInventory inventory = online.getInventory();
        		this.tournament.teleport(online, "Spleef.Spawn");
        		inventory.clear();
        		inventory.setItem(0, new ItemBuilder(Material.DIAMOND_SPADE).build());
        		online.updateInventory();
        		online.setGameMode(GameMode.SURVIVAL);
        		online.setAllowFlight(false);
        		online.setFlying(false);
                online.setFoodLevel(20);
        	}
        	this.tournament.setTournamentState(TournamentState.FIGHTING);
        	this.tournament.setProtection(10);
        }
        else if(this.tournament.getType() == TournamentType.TNTTAG) {
        	for(UUID players : this.players) {
        		Player online = Bukkit.getPlayer(players);
        		PlayerInventory inventory = online.getInventory();
        		this.tournament.teleport(online, "TNTTag.Spawn");
        		inventory.clear();
        		inventory.setItem(0, new ItemBuilder(Material.DIAMOND_SPADE).build());
        		online.updateInventory();
        		online.setGameMode(GameMode.SURVIVAL);
        		online.setAllowFlight(false);
        		online.setFlying(false);
                online.setFoodLevel(20);
        	}
        	this.tournament.setTournamentState(TournamentState.FIGHTING);
        	this.tournament.setProtection(5);
        }
        else if (this.tournament.getType() == TournamentType.SUMO) {
            for (UUID players : this.players) {
                Player online = Bukkit.getPlayer(players);
                this.tournament.teleport(online, "Sumo.Spawn");
                online.getInventory().clear();
                online.setGameMode(GameMode.SURVIVAL);
                online.setAllowFlight(false);
                online.setFlying(false);
                online.setFoodLevel(20);
            }
            this.tournament.setTournamentState(TournamentState.STARTING);
        }
    }
    
    public List<UUID> getPlayers() {
        return this.players;
    }
    
    public void setCreated(boolean s) {
        this.created = s;
    }
    
    public boolean isCreated() {
        return this.created;
    }
    
    public Tournament getTournament() {
        return this.tournament;
    }
}