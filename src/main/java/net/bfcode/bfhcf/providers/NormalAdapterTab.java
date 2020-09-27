package net.bfcode.bfhcf.providers;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.killstreaks.KillStreak;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.kothgame.koth.argument.KothScheduleArgument;
import net.bfcode.bfhcf.scoreboard.seventab.entry.TabEntry;
import net.bfcode.bfhcf.scoreboard.seventab.skin.Skin;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.DateTimeFormats;
import net.bfcode.bfhcf.utils.Utils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NormalAdapterTab {

    public static Comparator<PlayerFaction> FACTION_COMPARATOR;
    public static Comparator<FactionMember> ROLE_COMPARATOR;

    static {
        FACTION_COMPARATOR = ((playerFaction, targetFaction) -> Integer.compare(playerFaction.getOnlinePlayers().size(), targetFaction.getOnlinePlayers().size()));
        ROLE_COMPARATOR = ((playerMember, targetMember) -> Integer.compare(playerMember.getRole().ordinal(), targetMember.getRole().ordinal()));
    }

    public static List<TabEntry> getLines(Player player, List<TabEntry> lines) {
        FactionUser factionUser = HCFaction.getPlugin().getUserManager().getUser(player.getUniqueId());
        PlayerFaction faction = HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        int economy = HCFaction.getPlugin().getEconomyManager().getBalance(player.getUniqueId());
        String home = (faction == null || faction.getHome() == null) ? "None" : faction.getHome().getBlockX() + ", " + faction.getHome().getBlockZ();

        lines.add(new TabEntry(1, 0, CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.SERVER-NAME")
                .replace("%customonline%", getOnlinePlayers() + "")
                .replace("%online%", Bukkit.getServer().getOnlinePlayers().size() + "")
                .replace("%maxplayers%", Bukkit.getServer().getMaxPlayers() + "")
                .replace("%normalarrow%", "»"))));
        lines.add(new TabEntry(1, 1, CC.translate(HCFaction.getPlugin().getConfig().getString("TABLIST.ADDRESS")
                .replace("%normalarrow%", "»"))));

        lines.add(new TabEntry(0, 3, CC.translate(ConfigurationService.TABLIST_COLOR + "&lInformation")));
        if(faction != null) {
            lines.add(new TabEntry(0, 4, CC.translate("&bHome&7: &f" + home)));
            lines.add(new TabEntry(0, 5, CC.translate("&bOnline&7: &f" + faction.getOnlineMembers().size())));
            lines.add(new TabEntry(0, 6, CC.translate("&bDTR&7: &f" + new DecimalFormat("#.#").format(faction.getDeathsUntilRaidable()))));
            lines.add(new TabEntry(0, 7, CC.translate("&bBalance&7: &2$" + faction.getBalance())));

            lines.add(new TabEntry(0, 10, (faction == null ? "" : CC.translate("&3&l" + faction.getName() + " &7(&f"+ faction.getOnlineMembers().size() + "&7/&f" + faction.getMembers().size() + "&7)"))));

            List<FactionMember> members = new ArrayList<FactionMember>(faction.getMembers().values().stream().filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null).collect(Collectors.toList()));
            Collections.sort(members, NormalAdapterTab.ROLE_COMPARATOR);
            for (int i = 11; i < 18; i++) {
                int exact = i - 11;
                if(members.size() > exact) {
                    if(i == 18 && members.size() > 18) {
                        lines.add(new TabEntry(0, i, CC.translate("&7 and " + (members.size() - 19) + " more...")));
                    } else {
                        FactionMember targetMember = members.get(exact);
                        lines.add(new TabEntry(0, i, CC.translate("&7" + targetMember.getRole().getAstrix() + "&a" + targetMember.getName()))
                                .setSkin(Skin.getPlayer(targetMember.toOnlinePlayer().getPlayer()))
                                .setPing(Utils.getPing(targetMember.toOnlinePlayer().getPlayer())));
                    }
                }
            }
        } else {
            lines.add(new TabEntry(0, 4, CC.translate("&7Create your Faction using")));
            lines.add(new TabEntry(0, 5, CC.translate("&7/f create <name>")));
        }

        lines.add(new TabEntry(1, 3, CC.translate(ConfigurationService.TABLIST_COLOR + "&lStatistics")));
        lines.add(new TabEntry(1, 4, CC.translate("&bKills&7: &f" + factionUser.getKills())));
        lines.add(new TabEntry(1, 5, CC.translate("&bDeaths&7: &f" + factionUser.getDeaths())));
        lines.add(new TabEntry(1, 6, CC.translate("&bKillstreak&7: &f" + KillStreak.getKillStreak(player))));
        lines.add(new TabEntry(1, 7, CC.translate("&bBalance&7: &2$" + economy)));

        lines.add(new TabEntry(1, 10, CC.translate(ConfigurationService.TABLIST_COLOR + "&lLocalization")));
        lines.add(new TabEntry(1, 11, CC.translate("&7" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + " [" + getCardinalDirection(player) + "]")));
        lines.add(new TabEntry(1, 12, HCFaction.getPlugin().getFactionManager().getFactionAt(player.getLocation()).getDisplayName(player)));

        lines.add(new TabEntry(2, 3, CC.translate("&3&lEnd Portals")));
        lines.add(new TabEntry(2, 4, CC.translate("&b1000, 1000")));
        lines.add(new TabEntry(2, 5, CC.translate("&bIn all quadrants")));

        lines.add(new TabEntry(2, 7, CC.translate("&3&lMap Kit")));
        lines.add(new TabEntry(2, 8, CC.translate("&bProt 1, Sharp 1")));

        lines.add(new TabEntry(2, 10, CC.translate("&3&lMap Border")));
        lines.add(new TabEntry(2, 11, CC.translate("&b" + ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL) + " x " + ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL))));

        EventTimer eventTimer = HCFaction.getPlugin().getTimerManager().eventTimer;
        EventFaction eventFaction = eventTimer.getEventFaction();
        Map<LocalDateTime, String> scheduleMap = HCFaction.getPlugin().eventScheduler.getScheduleMap();
        if(eventFaction instanceof KothFaction) {
            if (eventTimer.getEventFaction() instanceof KothFaction) {
                lines.add(new TabEntry(2, 13, CC.translate("&3&l" + eventTimer.getName() + " KoTH")));
                lines.add(new TabEntry(2, 14, CC.translate("&b" + HCFaction.getRemaining(eventTimer.getRemaining(), true))));
                for(Claim claim : eventTimer.getEventFaction().getClaims()) {
                    Location location = claim.getCenter();
                    lines.add(new TabEntry(2, 15, CC.translate("&b" + location.getBlockX() + ", " + location.getBlockZ())));
                }
            }
        }
        else if(scheduleMap.isEmpty()) {
            lines.add(new TabEntry(2, 13, CC.translate("&3&lKoTH info")));
            lines.add(new TabEntry(2, 14, CC.translate("&7No seteados.")));
        } else {
            LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
            for (Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
                LocalDateTime scheduleDateTime = entry.getKey();
                if (now.isAfter(scheduleDateTime)) {
                    continue;
                }
                String eventName = entry.getValue();
                lines.add(new TabEntry(2, 13, CC.translate("&3&lKoTH info")));
                lines.add(new TabEntry(2, 14, CC.translate("&b&l" + WordUtils.capitalizeFully(eventName) + " &c" + KothScheduleArgument.HHMMA.format(scheduleDateTime))));
            }
        }

        lines.add(new TabEntry(3, 0, CC.translate(ConfigurationService.TABLIST_COLOR + "&lFactions List")));

        List<PlayerFaction> PlayerTeams = new ArrayList<>(HCFaction.getPlugin().getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getOnlineMembers().size() > 0).collect(Collectors.toSet()));
        Collections.sort(PlayerTeams, FACTION_COMPARATOR);
        Collections.reverse(PlayerTeams);
        for (int i = 0; i < 18; i ++) {
            if (i >= PlayerTeams.size()) {
                break;
            }
            PlayerFaction next = PlayerTeams.get(i);
            lines.add(new TabEntry(3, i + 1, CC.AQUA.toString() + (i + 1) + CC.GRAY + ") " + next.getDisplayName(player) + CC.GRAY + " (" + CC.WHITE + next.getOnlinePlayers().size() + CC.GRAY + ")"));
        }

        return lines;
    }

    public static String getOnlinePlayers() {
        int online = Bukkit.getServer().getOnlinePlayers().size();
        String testing = "";
        if(online > 199) {
            return testing = CC.translate("&4"+ online + "&7/&4" + Bukkit.getMaxPlayers());
        } else if(online > 119) {
            return testing = CC.translate("&c"+ online + "&7/&c" + Bukkit.getMaxPlayers());
        } else if(online > 69) {
            return testing = CC.translate("&e"+ online + "&7/&e" + Bukkit.getMaxPlayers());
        } else if(online > 29) {
            return testing = CC.translate("&6"+ online + "&7/&6" + Bukkit.getMaxPlayers());
        } else if(online > 14) {
            return testing = CC.translate("&2"+ online + "&7/&2" + Bukkit.getMaxPlayers());
        } else if(online > 0) {
            return testing = CC.translate("&a" + online + "&7/&a" + Bukkit.getMaxPlayers());
        }
        return testing;
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() + 180.0f) % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 22.5) {
            return "N";
        }
        if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        }
        if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        }
        if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        }
        if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        }
        if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        }
        if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        }
        return null;
    }
}
