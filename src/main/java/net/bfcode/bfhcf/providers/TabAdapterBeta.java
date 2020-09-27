package net.bfcode.bfhcf.providers;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.scoreboard.seventab.adapter.TabAdapter;
import net.bfcode.bfhcf.scoreboard.seventab.entry.TabEntry;
import net.bfcode.bfhcf.utils.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabAdapterBeta implements TabAdapter {

    @Override
    public String getHeader(Player player) {
        return CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.HEADER"));
    }

    @Override
    public String getFooter(Player player) {
        return CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.FOOTER"));
    }

    @Override
    public List<TabEntry> getLines(Player player) {
        List<TabEntry> lines = new ArrayList<TabEntry>();

        if (HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
            TournamentAdapterTab.getLines(player, lines);
        }
        else if (!HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
            NormalAdapterTab.getLines(player, lines);
        }

        return lines;
    }
}
