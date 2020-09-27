package net.bfcode.bfhcf.providers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.command.module.essential.FreezeCommand;
import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfbase.listener.VanishListener;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.Cooldowns;
import net.bfcode.bfbase.util.handlers.ChatHandler;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.abilities.AntiTrapper;
import net.bfcode.bfhcf.abilities.BelchBomb;
import net.bfcode.bfhcf.abilities.Cocaine;
import net.bfcode.bfhcf.abilities.FreezeGun;
import net.bfcode.bfhcf.abilities.Grappling;
import net.bfcode.bfhcf.abilities.Murasame;
import net.bfcode.bfhcf.abilities.NinjaStar;
import net.bfcode.bfhcf.abilities.PocketBard;
import net.bfcode.bfhcf.abilities.RawPotion;
import net.bfcode.bfhcf.abilities.Refill;
import net.bfcode.bfhcf.abilities.Rocket;
import net.bfcode.bfhcf.abilities.RotateStick;
import net.bfcode.bfhcf.abilities.SnowBall;
import net.bfcode.bfhcf.abilities.SuperAxe;
import net.bfcode.bfhcf.abilities.Switcher;
import net.bfcode.bfhcf.abilities.ThunderAxe;
import net.bfcode.bfhcf.citadel.CitadelFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.classes.bard.BardClass;
import net.bfcode.bfhcf.classes.type.MinerClass;
import net.bfcode.bfhcf.destroythecore.DTCHandler;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.killstreaks.KillStreak;
import net.bfcode.bfhcf.killtheking.KillTheKingManager;
import net.bfcode.bfhcf.kothgame.EventTimer;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.kothgame.tracker.ConquestTracker;
import net.bfcode.bfhcf.scoreboard.SidebarEntry;
import net.bfcode.bfhcf.scoreboard.SidebarProvider;
import net.bfcode.bfhcf.timer.GlobalTimer;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.Timer;
import net.bfcode.bfhcf.timer.type.KeyAllTimer;
import net.bfcode.bfhcf.timer.type.OPKeyAllTimer;
import net.bfcode.bfhcf.timer.type.SaleTimer;
import net.bfcode.bfhcf.timer.type.SotwTimer;
import net.bfcode.bfhcf.timer.type.SpawnTagTimer;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentManager;
import net.bfcode.bfhcf.tournaments.TournamentState;
import net.bfcode.bfhcf.tournaments.TournamentType;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.DateTimeFormats;
import net.bfcode.bfhcf.utils.DurationFormatter;
import net.bfcode.bfhcf.utils.ParserUtil;
import net.bfcode.bfhcf.utils.Utils;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.DecimalFormat;

public class ScoreboardProvider implements SidebarProvider {
    private static ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
    private static String STRAIGHT_LINE;
    private HCFaction plugin;

    public ScoreboardProvider(HCFaction plugin) {
        this.plugin = plugin;
    }

    private static String handleBardFormat(long millis, boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }

    public static String getRemainingSpawn(long duration) {
        return DurationFormatUtils.formatDuration(duration, ((duration >= HCFaction.HOUR) ? "HH:" : "") + "mm:ss");
    }

    @Override
    public String getTitle() {
        return HCFaction.getPlugin().scoreboardTitle;
    }

    @Override
    public List<SidebarEntry> getLines(Player player) {
        List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        KeyAllTimer.KeyAllRunnable keyallRunnable = this.plugin.getKeyAllTimer().getKeyAllRunnable();
        OPKeyAllTimer.OPKeyAllRunnable opKeyallRunnable = this.plugin.getOpKeyAllTimer().getOpKeyAllRunnable();
        PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
        EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        EventFaction eventFaction = eventTimer.getEventFaction();
        SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
        SaleTimer.SaleRunnable saleTimer = this.plugin.getSaleTimer().getSaleRunnable();
        TournamentManager tournamentManager = HCFaction.getPlugin().getTournamentManager();
        Tournament tournament = HCFaction.getPlugin().getTournamentManager().getTournament();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        FactionUser factionUser = this.plugin.getUserManager().getUser(player.getUniqueId());


//      Player Stats (Kitmap)
        if ((ConfigurationService.KIT_MAP) && factionUser != null && !(tournamentManager.isInTournament(player)) && !FreezeCommand.isFrozen(player.getUniqueId()) && !factionAt.equals(playerFaction) && !StaffModeCommand.isMod(player) && !(eventFaction instanceof ConquestFaction) && !KillTheKingManager.isActive()) {
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.KILLS + "§7: ", ConfigurationService.KILLS_COLOUR.toString() + factionUser.getKills()));
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.DEATHS + "§7: ", ConfigurationService.DEATHS_COLOUR.toString() + factionUser.getDeaths()));
            if (factionAt instanceof SpawnFaction) {
                lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.BALANCE + "§7: ", ConfigurationService.BALANCE_COLOUR.toString() + "$" + this.plugin.getEconomyManager().getBalance(player.getUniqueId())));
            }
            if (KillStreak.getKillStreak(player) > 0) {
                lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.KILL_STREAK + "§7: ", ConfigurationService.KILL_STREAK_COLOR.toString() +  KillStreak.getKillStreak(player)));
            }
        }

//      StaffMode
        if (StaffModeCommand.isMod(player)) {
            double tps = Bukkit.spigot().getTPS()[1];
            lines.add(this.add(ConfigurationService.STAFFMODE.replace("%symbol%", "\u2735")));
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.VANISH + "§7: ", VanishListener.isVanished(player) ? (ChatColor.GREEN + "\u2714") : (ChatColor.RED + "\u2716")));
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.CHAT + "§7: ", ChatHandler.isMuted() ? (ChatColor.RED + "Muteado") : (ChatColor.GREEN + "Activado")));
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.PLAYERS + "§7: ", "§f" + Bukkit.getServer().getOnlinePlayers().size()));
            if(player.isOp()) {
                lines.add(this.add(" §f\u00BB §cFree Mem§7: §f" + Math.round((double)Runtime.getRuntime().freeMemory() / 1024 / 1024) + "MB"));
            }
            lines.add(new SidebarEntry(" §f\u00BB ", ConfigurationService.TPS + "§7: ", "§f" + formatTps(tps)));
        }

//      Player Freeze
        if (FreezeCommand.isFrozen(player.getUniqueId())) {
            lines.add(new SidebarEntry("§4§lYou'", "re ", "Frozen"));
            lines.add(new SidebarEntry("§4§lJoin", " to Disc", "ord"));
            lines.add(new SidebarEntry("§7§o(discord.c", "om/invite", "/xszxPE6)"));
        }

//      Kill The King Event
        if (KillTheKingManager.isActive() && !StaffModeCommand.isMod(player) && !FreezeCommand.isFrozen(player.getUniqueId())) {
            Player king = KillTheKingManager.getKing();
            String prize = KillTheKingManager.getPrize();
            lines.add(this.add(CC.translate("&4&lKillTheKing Event")));
            lines.add(this.add(CC.translate(" &cKing: &f" + king.getName())));
            lines.add(this.add(CC.translate(" &cPotions: &f" + KillTheKingManager.getPotions(king))));
            lines.add(this.add(CC.translate(" &cApples: &f" + KillTheKingManager.getHealGapple(king))));
            lines.add(this.add(CC.translate(" &cLocation: &f" +
                    Math.round(king.getLocation().getX()) + ", " +
                    Math.round(king.getLocation().getY()) + ", " +
                    Math.round(king.getLocation().getZ()))));
            lines.add(this.add(CC.translate(" &cPrize: &f" + prize)));
        }

//      DTC Event
        for (String dtc : DTCHandler.getDTCActiveList()) {
            if (DTCHandler.isStarted(dtc)) {
                if(ConfigurationService.KIT_MAP) {
                    lines.add(this.add(ConfigurationService.LINE_COLOUR + ScoreboardProvider.STRAIGHT_LINE + ScoreboardProvider.STRAIGHT_LINE));
                }
                lines.add(this.add(CC.translate("&4&lDTC Event")));
                lines.add(this.add(CC.translate(" &cPoints&f: &f" + DTCHandler.dtcFile.get("DTC." + dtc + ".PointsLeft"))));
                lines.add(this.add(CC.translate(" &cCoords&f: &f" +
                        DTCHandler.dtcFile.getInt("CurrentDTC." + dtc + ".X") + ", " +
                        DTCHandler.dtcFile.getInt("CurrentDTC." + dtc + ".Y") + ", " +
                        DTCHandler.dtcFile.getInt("CurrentDTC." + dtc + ".Z"))));
            }
        }

//      Miner Class
        if (pvpClass instanceof MinerClass && ConfigurationService.KIT_MAP == false) {
            lines.add(this.add(ConfigurationService.MINER_CLASS));
            lines.add(this.add("§3 \u2a20 §bDiamantes§7: §f" + String.valueOf(player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE))));
        }

//      Bard Class
        if (pvpClass instanceof BardClass) {
            BardClass bardClass = (BardClass)pvpClass;
            lines.add(this.add(ConfigurationService.BARD_CLASS));
            lines.add(new SidebarEntry(ConfigurationService.ENERGY, "§7: ", ConfigurationService.ENERGY_COLOUR + handleBardFormat(bardClass.getEnergyMillis(player), true)));
            long remaining = bardClass.getRemainingBuffDelay(player);
            if (remaining > 0L) {
                lines.add(new SidebarEntry(ConfigurationService.BARD_COOLDOWN, "§7: ", ConfigurationService.BARD_COOLDOWN_COLOUR + HCFaction.getRemaining(remaining, true)));
            }
        }

//      Faction Rally
        if(Cooldowns.isOnCooldown("TEMPORAL_COORDS", player) && !StaffModeCommand.isMod(player)) {
            lines.add(this.add("§4§lFaction Rally"));
            lines.add(this.add("§c» §f§o" + playerFaction.getTemporalCoords() + ""));
        }

//      Faction Information
        if (playerFaction != null && factionAt.equals(playerFaction)) {
            if(!StaffModeCommand.isMod(player) && !(eventFaction instanceof ConquestFaction)) {
                lines.add(this.add(ConfigurationService.FACTION_TITLE));
                lines.add(new SidebarEntry(" ", ConfigurationService.FAC_DTR + "§7: ", ConfigurationService.FAC_DTR_COLOUR.toString() + playerFaction.getDtrColour() + ParserUtil.format(playerFaction.getDeathsUntilRaidable(false)) + "/" + ParserUtil.format(playerFaction.getMaximumDeathsUntilRaidable())));
                lines.add(new SidebarEntry(" ", ConfigurationService.FAC_BALANCE + "§7: ", ConfigurationService.FAC_BALANCE_COLOUR.toString() + "$" + this.plugin.getFactionManager().getPlayerFaction(player).getBalance()));
                lines.add(new SidebarEntry(" ", ConfigurationService.FAC_ONLINE + "§7: ", ConfigurationService.FAC_ONLINE_COLOUR.toString() + playerFaction.getOnlineMembers().size()));
                lines.add(new SidebarEntry(" ", ConfigurationService.FAC_POINTS + "§7: ", ConfigurationService.FAC_POINTS_COLOUR.toString() +  playerFaction.getPoints()));
                if(HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).getRemainingRegenerationTime() > 0L) {
                    lines.add(new SidebarEntry(" ", ConfigurationService.FAC_REGEN + "§7: ", ConfigurationService.FAC_REGEN_COLOUR.toString() + new StringBuilder(String.valueOf(Utils.getRemainingg(HCFaction.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).getRemainingRegenerationTime(), false))).toString()));
                }
            }
        }

//      Spawn Balance (HCF)
        if(factionAt instanceof SpawnFaction && !ConfigurationService.KIT_MAP) {
            lines.add(this.add("§a§lBalance§7: §f$" + this.plugin.getEconomyManager().getBalance(player.getUniqueId())));
        }

//      EOTW Timer
        if (eotwRunnable != null) {
            long remaining = eotwRunnable.getTimeUntilStarting();
            if (remaining > 0L) {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Starts", " In " + HCFaction.getRemaining(remaining, true)));
            }
            else if ((remaining = eotwRunnable.getTimeUntilCappable()) > 0L) {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Cappable ", "In " + HCFaction.getRemaining(remaining, true)));
            }
            else {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " is ", "currently active"));
            }
        }

//      Citadel Event
        if (eventFaction instanceof CitadelFaction && !FreezeCommand.isFrozen(player.getUniqueId())) {
            CitadelFaction citadelFaction = (CitadelFaction) eventFaction;
            long remaining = citadelFaction.getCaptureZone().getRemainingCaptureMillis();
            if (remaining > 0L) {
                lines.add(this.add("§5§lCitadel§7: §f" + HCFaction.getRemaining(remaining, true)));
            }
        }

//      Conquest Event
        if (eventFaction instanceof ConquestFaction &&!FreezeCommand.isFrozen(player.getUniqueId())) {
            ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            lines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatColor.BOLD + "Conquest Event", ""));
            lines.add(new SidebarEntry(" " + ChatColor.RED.toString() + ScoreboardProvider.CONQUEST_FORMATTER.get().format(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0) + "s", ChatColor.DARK_GRAY + " | ", ChatColor.YELLOW.toString() + ScoreboardProvider.CONQUEST_FORMATTER.get().format(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0) + "s"));
            lines.add(new SidebarEntry(" " + ChatColor.GREEN.toString() + ScoreboardProvider.CONQUEST_FORMATTER.get().format(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0) + "s", ChatColor.DARK_GRAY + " | " + ChatColor.RESET, ChatColor.AQUA.toString() + ScoreboardProvider.CONQUEST_FORMATTER.get().format(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0) + "s"));
            ConquestTracker conquestTracker1 = (ConquestTracker)conquestFaction.getEventType().getEventTracker();
            List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<Map.Entry<PlayerFaction, Integer>>(conquestTracker1.getFactionPointsMap().entrySet());
            int max = StaffModeCommand.isMod(player) ? 1 : 4;
            if (entries.size() > max) {
                entries = entries.subList(0, max);
            }
            int i = 0;
            for (Map.Entry<PlayerFaction, Integer> entry : entries) {
                if (i < 4) {
                    lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName((CommandSender)player), ChatColor.DARK_GRAY + ": " + ChatColor.RED + entry.getValue()));
                    ++i;
                }
            }
        }

//      Restart Timer
        long autore = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingMilliseconds();
        if (autore <= 300000L && autore >= 0L) {
            long remainingTicks = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingTicks();
            long remainingMillis = remainingTicks * 50L;
            lines.add(new SidebarEntry("§4§l", "Reinicio§7: §c", DurationFormatUtils.formatDuration(remainingMillis, ((remainingMillis >= HCFaction.HOUR) ? "HH:" : "") + "mm:ss")));
        }

//      SOTW Timer
        if (sotwRunnable != null) {
            lines.add(this.add(ConfigurationService.SOTW_TIMER));
            if(sotwRunnable.isSotwEnabled(player)) {
                lines.add(this.add(" §7» §r" + ConfigurationService.SOTW_COLOUR + "§m" + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
            } else {
                lines.add(this.add(" §7» §r" + ConfigurationService.SOTW_COLOUR + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
            }
        }

//      Home Range
        if(!ConfigurationService.KIT_MAP) {
            if(playerFaction != null && playerFaction.getHome() != null && factionUser.getHomeRangeMode() == true && player.getWorld().getName().equals("world")) {
                if(!(factionAt.equals(playerFaction) || factionAt instanceof ConquestFaction || factionAt instanceof KothFaction || factionAt instanceof SpawnFaction)) {
                    lines.add(this.add(" §7» §cHome Range§7: §f" + Math.round(player.getLocation().distance(playerFaction.getHome()))));
                }
            }
        }

//      Sale Timer
        if (saleTimer != null) {
            lines.add(new SidebarEntry(ConfigurationService.SALE_COLOUR.toString() + ChatColor.BOLD + ConfigurationService.SALE_TIMER, "§7:§f ", DurationFormatter.getRemaining(saleTimer.getRemaining(), true)));
        }

//      Key-All Timer
        if (keyallRunnable != null) {
            lines.add(new SidebarEntry(ConfigurationService.KEYALL_COLOUR.toString() + ChatColor.BOLD + ConfigurationService.KEYALL_TIMER, "§7: §f", DurationFormatter.getRemaining(keyallRunnable.getRemaining(), true)));
        }

//      OPKey-All Timer
        if (opKeyallRunnable != null) {
            lines.add(new SidebarEntry(ConfigurationService.OPKEYALL_COLOUR.toString() + ChatColor.BOLD + ConfigurationService.OPKEYALL_TIMER, "§7: §f", DurationFormatter.getRemaining(opKeyallRunnable.getRemaining(), true)));
        }

//      All Timers
        Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
        for (Timer timer : timers) {
            if (timer instanceof EventTimer) {
                EventTimer event = (EventTimer)timer;
                if (event.getEventFaction() instanceof ConquestFaction) {
                    continue;
                }
                if (event.getEventFaction() instanceof KothFaction) {
                    lines.add(this.add(CC.translate(" &f\u00BB &9&l" + event.getName() + "&7: &f" + HCFaction.getRemaining(event.getRemaining(), true))));
                    continue;
                }
                if(event.getEventFaction() instanceof CitadelFaction) {
                    lines.add(this.add(CC.translate(" &f\u00BB &5&l" + event.getName() + "&7: &f" + HCFaction.getRemaining(event.getRemaining(), true))));
                    continue;
                }
            }
            if(tournamentManager.isInTournament(player)) {
                continue;
            }
            if (timer instanceof PlayerTimer) {
                PlayerTimer playerTimer = (PlayerTimer)timer;
                long remaining2 = playerTimer.getRemaining(player);
                if (remaining2 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if (playerTimer instanceof SpawnTagTimer) {
                    lines.add(new SidebarEntry(" §f\u00BB " + playerTimer.getScoreboardPrefix(), ChatColor.BOLD + timerName, ChatColor.GRAY + ": " + ChatColor.WHITE + HCFaction.getRemaining(remaining2, true)));
                }
                else {
                    lines.add(new SidebarEntry(" §f\u00BB " + playerTimer.getScoreboardPrefix(), ChatColor.BOLD + timerName, ChatColor.GRAY + ": " + ChatColor.WHITE + HCFaction.getRemaining(remaining2, true)));
                }
            }
            else {
                if (!(timer instanceof GlobalTimer)) {
                    continue;
                }
                GlobalTimer playerTimer2 = (GlobalTimer)timer;
                long remaining2 = playerTimer2.getRemaining();
                if (remaining2 <= 0L) {
                    continue;
                }
                String timerName2 = playerTimer2.getName();
                lines.add(new SidebarEntry(playerTimer2.getScoreboardPrefix(), ChatColor.BOLD + timerName2, ChatColor.GRAY + ": " + ChatColor.WHITE + HCFaction.getRemaining(remaining2, true)));
            }
        }

//      Abilities
        if (AntiTrapper.isOnCooldownDam(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(AntiTrapper.name) + ": §f" + AntiTrapper.getCooldownDam(player)));
        }

        if (BelchBomb.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(BelchBomb.name) + ": §f" + BelchBomb.getCooldown(player)));
        }

        if (Cocaine.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Cocaine.name) + ": §f" + Cocaine.getCooldown(player)));
        }

        if (FreezeGun.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(FreezeGun.name) + ": §f" + FreezeGun.getCooldown(player)));
        }

        if (Grappling.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Grappling.name) + ": §f" + Grappling.getCooldown(player)));
        }

        if (Murasame.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Murasame.name) + ": §f" + Murasame.getCooldown(player)));
        }

        if (NinjaStar.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(NinjaStar.name) + ": §f" + NinjaStar.getCooldown(player)));
        }

        if (PocketBard.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(PocketBard.name) + ": §f" + PocketBard.getCooldown(player)));
        }

        if (RawPotion.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(RawPotion.name) + ": §f" + RawPotion.getCooldown(player)));
        }

        if (Refill.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Refill.name) + ": §f" + Refill.getCooldown(player)));
        }

        if (Rocket.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Rocket.name) + ": §f" + Rocket.getCooldown(player)));
        }

        if (RotateStick.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(RotateStick.name) + ": §f" + RotateStick.getCooldown(player)));
        }

        if (SnowBall.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(SnowBall.name) + ": §f" + SnowBall.getCooldown(player)));
        }

        if (SuperAxe.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(SuperAxe.name) + ": §f" + SuperAxe.getCooldown(player)));
        }

        if (Switcher.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(Switcher.name) + ": §f" + Switcher.getCooldown(player)));
        }

        if (ThunderAxe.isOnCooldown(player)) {
            lines.add(this.add(" §f\u00BB §r" + String.valueOf(ThunderAxe.name) + ": §f" + ThunderAxe.getCooldown(player)));
        }

//      All Tournaments
        if (tournamentManager.isInTournament(player) && !FreezeCommand.isFrozen(player.getUniqueId())) {
            int announceCountdown = tournament.getDesecrentAnn();
            lines.add(this.add("§c§l" + tournament.getType().getName() + " Event"));

            if(tournament.getType() == TournamentType.SUMO) {
                lines.add(this.add(" §f\u00BB §cPlayers§7: §f" + tournament.getPlayers().size() + "/" + tournament.getSize()));
                if (announceCountdown > 0) {
                    lines.add(this.add(" §f\u00BB §cStarting in§7: §f" + announceCountdown + "s"));
                }
                if (tournament.getTournamentState() == TournamentState.WAITING) {
                    lines.add(this.add(" §f\u00BB §cState§7: §fWaiting"));
                } else if (tournament.getTournamentState() == TournamentState.FIGHTING) {
                    lines.add(this.add(" §f\u00BB §cState§7: §fFighting"));
                } else {
                    lines.add(this.add(" §f\u00BB §cState§7: §fSelecting"));
                }
            } else if(tournament.getType() == TournamentType.DIAMOND || tournament.getType() == TournamentType.BARD || tournament.getType() == TournamentType.ASSASSIN || tournament.getType() == TournamentType.BOMBER || tournament.getType() == TournamentType.AXE || tournament.getType() == TournamentType.ARCHER) {
                lines.add(this.add(" §f\u00BB §cPlayers§7: §f" + tournament.getPlayers().size() + "/" + tournament.getSize()));
                if (announceCountdown > 0) {
                    lines.add(this.add(" §f\u00BB §cStarting in§7: §f" + announceCountdown + "s"));
                }
                if (tournament.getTournamentState() == TournamentState.WAITING) {
                    lines.add(this.add(" §f\u00BB §cState§7: §fWaiting"));
                }
                else if (tournament.isActiveProtection()) {
                    lines.add(this.add(" §f\u00BB §4§lGrace Period§7: §f" + (tournament.getProtection() / 1000) + "s"));
                }
                else {
                    lines.add(this.add(" §f\u00BB §cState§7: §fFighting"));
                }
            } else if(tournament.getType() == TournamentType.SPLEEF) {
                lines.add(this.add(" §f\u00BB §cPlayers§7: §f" + tournament.getPlayers().size() + "/" + tournament.getSize()));
                if (announceCountdown > 0) {
                    lines.add(this.add(" §f\u00BB §cStarting in§7: §f" + announceCountdown + "s"));
                }
                if (tournament.getTournamentState() == TournamentState.WAITING) {
                    lines.add(this.add(" §f\u00BB §cState§7: §fWaiting"));
                }
                else if (tournament.isActiveProtection()) {
                    lines.add(this.add(" §f\u00BB §4§lGrace Period§7: §f" + (tournament.getProtection() / 1000) + "s"));
                }
                else {
                    lines.add(this.add(" §f\u00BB §cState§7: §fFighting"));
                }
            }
        }

        if (!lines.isEmpty()) {
            lines.add(0, this.add("§r§7§m---§7§m---------------"));
            lines.add(new SidebarEntry(""));
            lines.add(this.add(ConfigurationService.FOOTER_SCOREBOARD));
            lines.add(lines.size(), this.add("§7§m------------------"));
        }

        return lines;
    }

    public SidebarEntry add(String s) {

        if (s.length() < 10) {
            return new SidebarEntry(s);
        }

        if (s.length() > 10 && s.length() < 20) {
            return new SidebarEntry(s.substring(0, 10), s.substring(10, s.length()), "");
        }

        if (s.length() > 20) {
            return new SidebarEntry(s.substring(0, 10), s.substring(10, 20), s.substring(20, s.length()));
        }

        return null;
    }

    private String formatTps(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }

    static {
        ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
        new SidebarEntry(" ", " ", " ");
        STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
        CONQUEST_FORMATTER = ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
    }
}
