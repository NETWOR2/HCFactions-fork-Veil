package net.bfcode.bfhcf.tournaments.runnable;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import dev.hatsur.library.Library;
import net.bfcode.bfhcf.utils.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentState;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.PlayerUtil;
import org.bukkit.entity.Player;

public class TournamentRunnable {

    private HCFaction plugin;
    private Tournament tournament;

    public TournamentRunnable(Tournament tournament) {
        this.plugin = HCFaction.getPlugin();
        this.tournament = tournament;
    }

    public void startSumo() {
        new BukkitRunnable() {
            public void run() {
                if (!TournamentRunnable.this.plugin.getTournamentManager().isCreated()) {
                    this.cancel();
                    return;
                }
                if (TournamentRunnable.this.plugin.getTournamentManager().getPlayers().isEmpty()) {
                    this.cancel();
                    return;
                }
                if (TournamentRunnable.this.tournament.getTournamentState() == TournamentState.STARTING || TournamentRunnable.this.tournament.getTournamentState() == TournamentState.FIGHTING) {
                    int countdown = TournamentRunnable.this.tournament.decrementCountdown();
                    if(countdown == 7) {
                        TournamentRunnable.this.tournament.broadcast(CC.translate("&cSelecting players..."));
                    }
                    else if(countdown == 6) {
                        TournamentRunnable.this.searchPlayers();
                        Player first = TournamentRunnable.this.tournament.getFirstPlayer();
                        Player second = TournamentRunnable.this.tournament.getSecondPlayer();
                        PlayerUtil.startingSumo(first);
                        PlayerUtil.startingSumo(second);
                    }
                    else if(countdown == 5) {
                        TournamentRunnable.this.tournament.teleport(TournamentRunnable.this.tournament.getFirstPlayer(), "Sumo.First");
                        TournamentRunnable.this.tournament.teleport(TournamentRunnable.this.tournament.getSecondPlayer(), "Sumo.Second");
                        TournamentRunnable.this.tournament.broadcast(CC.translate("&7&oTeleporting players..."));
                        Player first = TournamentRunnable.this.tournament.getFirstPlayer();
                        Player second = TournamentRunnable.this.tournament.getSecondPlayer();
                        first.getInventory().clear();
                        second.getInventory().clear();
                        TournamentRunnable.this.tournament.setTournamentState(TournamentState.FIGHTING);
                    }
                    else if(countdown < 5 && countdown > 0) {
                    	TournamentRunnable.this.tournament.broadcast(CC.translate("&eMatch Starting in &c&l" + countdown + " &eseconds!"));
                    }
                    else if(countdown == 0) {
                        TournamentRunnable.this.tournament.broadcast(CC.translate("&7Starting fight: "
                    + Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(TournamentRunnable.this.tournament.getFirstPlayer().getUniqueId()).getColoredUsername()
                    + " &7vs "
                    + Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(TournamentRunnable.this.tournament.getSecondPlayer().getUniqueId()).getColoredUsername()));
                        PlayerUtil.fightingSumo(TournamentRunnable.this.tournament.getFirstPlayer());
                        PlayerUtil.fightingSumo(TournamentRunnable.this.tournament.getSecondPlayer());
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 20L, 20L);
    }

    public void runAnnounce() {
        new BukkitRunnable() {
			public void run() {
                if (!TournamentRunnable.this.plugin.getTournamentManager().isCreated()) {
                    this.cancel();
                    return;
                }
                if (TournamentRunnable.this.plugin.getTournamentManager().getPlayers().isEmpty()) {
                    this.cancel();
                    return;
                }
                if (TournamentRunnable.this.tournament != null && TournamentRunnable.this.tournament.getTournamentState() == TournamentState.WAITING) {
                    int countdown = TournamentRunnable.this.tournament.decrementAnnounce();
                    if (countdown == 0) {
                        Bukkit.broadcastMessage(CC.translate("&4&l" + TournamentRunnable.this.tournament.getType().getName() + " &fhas started with " + TournamentRunnable.this.tournament.getPlayers().size() + " players."));
                    }
                    else if ((countdown % 10 == 0 || countdown < 5) && countdown > 0) {
                    	Tournament tournament = HCFaction.getPlugin().getTournamentManager().getTournament();
                    	Player player = TournamentRunnable.this.tournament.getHoster();
                    	String name = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getColoredUsername();
                    	for(Player players : Bukkit.getServer().getOnlinePlayers()) {
                            FancyMessage message = new FancyMessage("");
                            players.sendMessage("");
                            message.then(CC.translate("&7(&4&lEvent&7) &4" + tournament.getType().getName() + "&c Hosted by " + name + " &cis starting in " + countdown + " second" + ((countdown == 1) ? "" : "s") + " &7(&c" + tournament.getPlayers().size() + " &7/&c " + tournament.getSize() + "&7)")).then(CC.translate(" &4¡Click here!")).tooltip(CC.translate("&a¡Right Click for join!")).command("/tournament join").send(players);
                            players.sendMessage("");
                        }
                    }
                    else if (countdown < 0) {
                        if (TournamentRunnable.this.tournament.getPlayers().size() < 2) {
                            TournamentRunnable.this.plugin.getTournamentManager().setCreated(false);
                            for (Player online2 : Bukkit.getServer().getOnlinePlayers()) {
                                if (TournamentRunnable.this.plugin.getTournamentManager().isInTournament(online2.getUniqueId())) {
                                    TournamentRunnable.this.tournament.rollbackInventory(online2);
                                    TournamentRunnable.this.plugin.getTournamentManager().kickPlayer(online2.getUniqueId());
                                    online2.sendMessage(CC.translate("&c&lYou were kicked from the tournament for&7: &fThe event need more players"));
                                    online2.teleport(Bukkit.getWorld("World").getSpawnLocation());
                                }
                            }
                        }
                        else {
                            TournamentRunnable.this.plugin.getTournamentManager().forceStart();
                        }
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 20L, 20L);
    }

    public void searchPlayers() {
        List<Player> players = new ArrayList<Player>();
        if (!players.isEmpty()) {
            players.clear();
        }
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (this.plugin.getTournamentManager().isInTournament(online.getUniqueId())) {
                players.add(online);
            }
        }
        Collections.shuffle(players);
        if (players.size() > 0) {
            this.tournament.setFirstPlayer(players.get(0));
            this.tournament.setSecondPlayer(players.get(1));
        }
    }
}
