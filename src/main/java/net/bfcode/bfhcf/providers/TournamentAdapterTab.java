package net.bfcode.bfhcf.providers;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.scoreboard.seventab.entry.TabEntry;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentState;
import net.bfcode.bfhcf.tournaments.TournamentType;
import net.bfcode.bfhcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TournamentAdapterTab {

    public static List<TabEntry> getLines(Player player, List<TabEntry> lines) {
        PlayerFaction faction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        Tournament tour = HCFaction.getPlugin().getTournamentManager().getTournament();

        lines.add(new TabEntry(1, 0, CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.SERVER-NAME")
                .replace("%customonline%", getOnlinePlayers() + "")
                .replace("%online%", Bukkit.getServer().getOnlinePlayers().size() + "")
                .replace("%maxplayers%", Bukkit.getServer().getMaxPlayers() + "")
                .replace("%normalarrow%", "»"))));
        lines.add(new TabEntry(1, 1, CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.ADDRESS")
                .replace("%normalarrow%", "»"))));

        lines.add(new TabEntry(0, 3, CC.translate("&7Your playing")));
        lines.add(new TabEntry(0, 4, CC.translate("&7actually in")));
        lines.add(new TabEntry(0, 5, CC.translate("&7Syfer Tournaments")));

        lines.add(new TabEntry(0, 12, CC.translate("&3&lInformation")));
        lines.add(new TabEntry(0, 13, CC.translate("&bType&7: &f" + tour.getType().getName())));
        lines.add(new TabEntry(0, 14, CC.translate("&bPlayers&7: &f" + tour.getPlayers().size() + "/" + tour.getSize())));
        lines.add(new TabEntry(0, 15, CC.translate("&bStatus&7: &f" + tour.getTournamentState().toString())));
        lines.add(new TabEntry(0, 16, CC.translate("&bHost&7: &7" + tour.getHoster().getName())));

        if(tour.getType().equals(TournamentType.SUMO) && tour.getTournamentState().equals(TournamentState.FIGHTING)) {
            lines.add(new TabEntry(1, 9, CC.translate("  &b" + tour.getFirstPlayer().getName())));
            lines.add(new TabEntry(1, 10, CC.translate("       &3VS")));
            lines.add(new TabEntry(1, 11, CC.translate("  &b" + tour.getSecondPlayer().getName())));
        }

        lines.add(new TabEntry(2, 1, CC.translate("&3&lUsers Remaining")));
        List<UUID> players = new ArrayList<>(tour.getPlayers());
        for(int i = 0; i < 17; i++) {
            if (i >= players.size()) {
                break;
            }
            Player next = Bukkit.getPlayer(players.get(i));
            lines.add(new TabEntry(2, 2 + i, CC.translate("&b" + (i + 1) + "&7) &r" + next.getName())));
        }

        return lines;
    }

    public static String getOnlinePlayers() {
        int online = Bukkit.getServer().getOnlinePlayers().size();
        String testing = "";
        if(online > 199) {
            return testing = CC.translate("&4"+ online + "&7/&4" + Bukkit.getMaxPlayers());
        } else if(online < 200 && online > 119) {
            return testing = CC.translate("&c"+ online + "&7/&c" + Bukkit.getMaxPlayers());
        } else if(online < 120 && online > 69) {
            return testing = CC.translate("&e"+ online + "&7/&e" + Bukkit.getMaxPlayers());
        } else if(online < 70 && online > 29) {
            return testing = CC.translate("&6"+ online + "&7/&6" + Bukkit.getMaxPlayers());
        } else if(online < 30 && online > 14) {
            return testing = CC.translate("&2"+ online + "&7/&2" + Bukkit.getMaxPlayers());
        } else if(online < 15 && online > 0) {
            return testing = CC.translate("&a" + online + "&7/&a" + Bukkit.getMaxPlayers());
        }
        return testing;
    }
}
