package net.bfcode.bfhcf;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.bfcode.bfhcf.providers.TabAdapterBeta;
import net.bfcode.bfhcf.scoreboard.seventab.SevenTab;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import lombok.Setter;
import net.bfcode.bfbase.BasePlugin;
import net.bfcode.bfbase.ServerHandler;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.balance.EconomyCommand;
import net.bfcode.bfhcf.balance.EconomyManager;
import net.bfcode.bfhcf.balance.FlatFileEconomyManager;
import net.bfcode.bfhcf.balance.PayCommand;
import net.bfcode.bfhcf.balance.ShopSignListener;
import net.bfcode.bfhcf.balance.SpawnerShopListener;
import net.bfcode.bfhcf.classes.PvpClassManager;
import net.bfcode.bfhcf.classes.bard.BardClass;
import net.bfcode.bfhcf.classes.type.ArcherClass;
import net.bfcode.bfhcf.classes.type.AssassinClass;
import net.bfcode.bfhcf.classes.type.BombermanClass;
import net.bfcode.bfhcf.combatlog.CombatLogListener;
import net.bfcode.bfhcf.combatlog.CustomEntityRegistration;
import net.bfcode.bfhcf.command.AbilityCommand;
import net.bfcode.bfhcf.command.AutoFeedCommand;
import net.bfcode.bfhcf.command.CobbleCommand;
import net.bfcode.bfhcf.command.CoordsCommand;
import net.bfcode.bfhcf.command.CraftCommand;
import net.bfcode.bfhcf.command.CustomEnchantCommand;
import net.bfcode.bfhcf.command.DailyCommand;
import net.bfcode.bfhcf.command.DiscordCommand;
import net.bfcode.bfhcf.command.EndDragonCommand;
import net.bfcode.bfhcf.command.EndPortalCommand;
import net.bfcode.bfhcf.command.FFACommand;
import net.bfcode.bfhcf.command.FightCommand;
import net.bfcode.bfhcf.command.FocusCommand;
import net.bfcode.bfhcf.command.GlowstoneMountainCommand;
import net.bfcode.bfhcf.command.GoppleCommand;
import net.bfcode.bfhcf.command.HelpCommand;
import net.bfcode.bfhcf.command.HostCommand;
import net.bfcode.bfhcf.command.KeyAllCommand;
import net.bfcode.bfhcf.command.KeyShopCommand;
import net.bfcode.bfhcf.command.LFFCommand;
import net.bfcode.bfhcf.command.LocationCommand;
import net.bfcode.bfhcf.command.LogoutCommand;
import net.bfcode.bfhcf.command.MapKitCommand;
import net.bfcode.bfhcf.command.NetherCommand;
import net.bfcode.bfhcf.command.OPKeyAllCommand;
import net.bfcode.bfhcf.command.OreMountainCommand;
import net.bfcode.bfhcf.command.OresCommand;
import net.bfcode.bfhcf.command.PlayersCommand;
import net.bfcode.bfhcf.command.PvpTimerCommand;
import net.bfcode.bfhcf.command.ReviveCommand;
import net.bfcode.bfhcf.command.SafestopCommand;
import net.bfcode.bfhcf.command.SaleCommand;
import net.bfcode.bfhcf.command.SaveDataCommand;
import net.bfcode.bfhcf.command.SendCoordsCommand;
import net.bfcode.bfhcf.command.ServerTimeCommand;
import net.bfcode.bfhcf.command.SetBorderCommand;
import net.bfcode.bfhcf.command.SetCommand;
import net.bfcode.bfhcf.command.SotwCommand;
import net.bfcode.bfhcf.command.SpawnCommand;
import net.bfcode.bfhcf.command.SpawnerCommand;
import net.bfcode.bfhcf.command.StaffListCommand;
import net.bfcode.bfhcf.command.StatResetCommand;
import net.bfcode.bfhcf.command.StatsCommand;
import net.bfcode.bfhcf.command.StatsManagerCommand;
import net.bfcode.bfhcf.command.TeamspeakCommand;
import net.bfcode.bfhcf.command.ToggleEndCommand;
import net.bfcode.bfhcf.command.ToggleFDCommand;
import net.bfcode.bfhcf.command.ToggleHomeRangeCommand;
import net.bfcode.bfhcf.command.TwitterCommand;
import net.bfcode.bfhcf.command.EnderChestCommand;
import net.bfcode.bfhcf.command.WebsiteCommand;
import net.bfcode.bfhcf.command.death.DeathExecutor;
import net.bfcode.bfhcf.command.lives.LivesExecutor;
import net.bfcode.bfhcf.command.reclaim.Reclaim;
import net.bfcode.bfhcf.command.reclaim.ReclaimManager;
import net.bfcode.bfhcf.command.reclaim.argument.ReclaimCommand;
import net.bfcode.bfhcf.command.reclaim.argument.SetClaimCommand;
import net.bfcode.bfhcf.command.tournament.TournamentExecutor;
import net.bfcode.bfhcf.config.PotionLimiterData;
import net.bfcode.bfhcf.deathban.Deathban;
import net.bfcode.bfhcf.deathban.DeathbanListener;
import net.bfcode.bfhcf.deathban.DeathbanManager;
import net.bfcode.bfhcf.deathban.FlatFileDeathbanManager;
import net.bfcode.bfhcf.deathhistory.DeathHistoryListener;
import net.bfcode.bfhcf.destroythecore.DTCCommand;
import net.bfcode.bfhcf.destroythecore.DTCListener;
import net.bfcode.bfhcf.faction.FactionExecutor;
import net.bfcode.bfhcf.faction.FactionManager;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.FlatFileFactionManager;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.claim.ClaimHandler;
import net.bfcode.bfhcf.faction.claim.ClaimWandListener;
import net.bfcode.bfhcf.faction.claim.Subclaim;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.EndPortalFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.killstreaks.KillStreakListener;
import net.bfcode.bfhcf.killtheking.KillTheKingCommand;
import net.bfcode.bfhcf.killtheking.KillTheKingListener;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.EventExecutor;
import net.bfcode.bfhcf.kothgame.EventScheduler;
import net.bfcode.bfhcf.kothgame.conquest.ConquestExecutor;
import net.bfcode.bfhcf.kothgame.eotw.EOTWHandler;
import net.bfcode.bfhcf.kothgame.eotw.EotwCommand;
import net.bfcode.bfhcf.kothgame.eotw.EotwListener;
import net.bfcode.bfhcf.kothgame.faction.CapturableFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.KothFaction;
import net.bfcode.bfhcf.kothgame.koth.KothExecutor;
import net.bfcode.bfhcf.listener.AutoSmeltOreListener;
import net.bfcode.bfhcf.listener.BeaconListener;
import net.bfcode.bfhcf.listener.BookDeenchantListener;
import net.bfcode.bfhcf.listener.BorderListener;
import net.bfcode.bfhcf.listener.BottledExpListener;
import net.bfcode.bfhcf.listener.BrewingSpeedListener;
import net.bfcode.bfhcf.listener.ChatListener;
import net.bfcode.bfhcf.listener.ClearInventoryListener;
import net.bfcode.bfhcf.listener.CoreListener;
import net.bfcode.bfhcf.listener.CreeperFriendlyListener;
import net.bfcode.bfhcf.listener.DeathListener;
import net.bfcode.bfhcf.listener.DeathMessageListener;
import net.bfcode.bfhcf.listener.DeathSignListener;
import net.bfcode.bfhcf.listener.DonorOnlyListener;
import net.bfcode.bfhcf.listener.ElevatorListener;
import net.bfcode.bfhcf.listener.EndListener;
import net.bfcode.bfhcf.listener.EntityLimitListener;
import net.bfcode.bfhcf.listener.EventSignListener;
import net.bfcode.bfhcf.listener.ExpMultiplierListener;
import net.bfcode.bfhcf.listener.FactionListener;
import net.bfcode.bfhcf.listener.FactionsCoreListener;
import net.bfcode.bfhcf.listener.FoundDiamondsListener;
import net.bfcode.bfhcf.listener.FurnaceSmeltSpeederListener;
import net.bfcode.bfhcf.listener.HitDetectionListener;
import net.bfcode.bfhcf.listener.InteractListener;
import net.bfcode.bfhcf.listener.ItemShopListener;
import net.bfcode.bfhcf.listener.ItemStatTrackingListener;
import net.bfcode.bfhcf.listener.KitListener;
import net.bfcode.bfhcf.listener.MinecartElevatorListener;
import net.bfcode.bfhcf.listener.PotionListener;
import net.bfcode.bfhcf.listener.SignsListener;
import net.bfcode.bfhcf.listener.SignSubclaimListener;
import net.bfcode.bfhcf.listener.SkullListener;
import net.bfcode.bfhcf.listener.SotwListener;
import net.bfcode.bfhcf.listener.WorldListener;
import net.bfcode.bfhcf.listener.WrenchListener;
import net.bfcode.bfhcf.listener.fixes.ArmorFixListener;
import net.bfcode.bfhcf.listener.fixes.BlockHitFixListener;
import net.bfcode.bfhcf.listener.fixes.BlockJumpGlitchFixListener;
import net.bfcode.bfhcf.listener.fixes.BlockTNTCartListener;
import net.bfcode.bfhcf.listener.fixes.BoatGlitchFixListener;
import net.bfcode.bfhcf.listener.fixes.BookQuillFixListener;
import net.bfcode.bfhcf.listener.fixes.ColonFix;
import net.bfcode.bfhcf.listener.fixes.DupeGlitchFix;
import net.bfcode.bfhcf.listener.fixes.EnchantLimitListener;
import net.bfcode.bfhcf.listener.fixes.EndermanFixListener;
import net.bfcode.bfhcf.listener.fixes.HungerFixListener;
import net.bfcode.bfhcf.listener.fixes.InfinityArrowFixListener;
import net.bfcode.bfhcf.listener.fixes.PearlGlitchListener;
import net.bfcode.bfhcf.listener.fixes.PexCrashFix;
import net.bfcode.bfhcf.listener.fixes.PortalFixListener;
import net.bfcode.bfhcf.listener.fixes.PotionLimitListener;
import net.bfcode.bfhcf.listener.fixes.VoidGlitchFixListener;
import net.bfcode.bfhcf.listener.fixes.WeatherFixListener;
import net.bfcode.bfhcf.mountain.DestroyTheCoreFaction;
import net.bfcode.bfhcf.mountain.GlowstoneFaction;
import net.bfcode.bfhcf.mountain.GlowstoneMountainManager;
import net.bfcode.bfhcf.mountain.MountainFaction;
import net.bfcode.bfhcf.mountain.OreFaction;
import net.bfcode.bfhcf.mountain.OreMountainManager;
import net.bfcode.bfhcf.scoreboard.ScoreboardHandler;
import net.bfcode.bfhcf.timer.TimerExecutor;
import net.bfcode.bfhcf.timer.TimerManager;
import net.bfcode.bfhcf.timer.type.SaleTimer;
import net.bfcode.bfhcf.timer.type.SotwTimer;
import net.bfcode.bfhcf.timer.type.KeyAllTimer;
import net.bfcode.bfhcf.timer.type.OPKeyAllTimer;
import net.bfcode.bfhcf.tournaments.TournamentListener;
import net.bfcode.bfhcf.tournaments.TournamentManager;
import net.bfcode.bfhcf.tournaments.file.TournamentFile;
import net.bfcode.bfhcf.tournaments.runnable.TournamentRunnable;
import net.bfcode.bfhcf.user.BaseUserManager;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.user.UserManager;
import net.bfcode.bfhcf.utils.ArmorUtils;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Cooldowns;
import net.bfcode.bfhcf.utils.DateTimeFormats;
import net.bfcode.bfhcf.utils.Message;
import net.bfcode.bfhcf.utils.file.MainFile;
import net.bfcode.bfhcf.utils.file.MessagesFile;
import net.bfcode.bfhcf.utils.file.ReclaimFile;
import net.bfcode.bfhcf.utils.file.SettingsFile;
import net.bfcode.bfhcf.visualise.ProtocolLibHook;
import net.bfcode.bfhcf.visualise.VisualiseHandler;
import net.bfcode.bfhcf.visualise.WallBorderListener;
import net.bfcode.bfhcf.wrench.WrenchCommand;
import net.bfcode.bfhcf.wrench.WrenchGiveCommand;
import net.bfcode.bfhcf.abilities.AntiTrapper;
import net.bfcode.bfhcf.abilities.BelchBomb;
import net.bfcode.bfhcf.abilities.Cocaine;
import net.bfcode.bfhcf.abilities.FreezeGun;
import net.bfcode.bfhcf.abilities.Grappling;
import net.bfcode.bfhcf.abilities.Murasame;
import net.bfcode.bfhcf.abilities.NinjaStar;
import net.bfcode.bfhcf.abilities.NoFall;
import net.bfcode.bfhcf.abilities.PocketBard;
import net.bfcode.bfhcf.abilities.RawPotion;
import net.bfcode.bfhcf.abilities.Refill;
import net.bfcode.bfhcf.abilities.Rocket;
import net.bfcode.bfhcf.abilities.RotateStick;
import net.bfcode.bfhcf.abilities.SnowBall;
import net.bfcode.bfhcf.abilities.SuperAxe;
import net.bfcode.bfhcf.abilities.Switcher;
import net.bfcode.bfhcf.abilities.ThunderAxe;

@Getter
@Setter
public class HCFaction extends JavaPlugin implements Listener {
	
	@Getter
	private static HCFaction plugin;
	public static File SchedulesData;
    public static Joiner SPACE_JOINER;
    public static Joiner COMMA_JOINER;
    public static long HOUR;
    private static long MINUTE;
    public static int POTION_TIME;
    public static Comparator<PlayerFaction> POINTS_COMPARATOR = Comparator.comparingInt(PlayerFaction::getPoints);
	public static Comparator<FactionUser> KILLS_COMPARATOR = Comparator.comparingInt(FactionUser::getKills);
    public EventScheduler eventScheduler;
    public String scoreboardTitle;
    public ArrayList<String> players;
    private Message message;
    private Random random;
    private WorldEditPlugin worldEdit;
    public FoundDiamondsListener foundDiamondsListener;
    private ClaimHandler claimHandler;
    private SotwTimer sotwTimer;
    private DeathbanManager deathbanManager;
    private EconomyManager economyManager;
    private EOTWHandler eotwHandler;
    private FactionManager factionManager;
    private PvpClassManager pvpClassManager;
    private BardClass bardClass;
    private ScoreboardHandler scoreboardHandler;
    private TimerManager timerManager;
    private UserManager userManager;
    private VisualiseHandler visualiseHandler;
    private ReclaimManager reclaimManager;
    private TournamentManager tournamentManager;
    private BaseUserManager baseUserManager;
    private SaleTimer saleTimer;
    private KeyAllTimer keyAllTimer;
    private OPKeyAllTimer opKeyAllTimer;
    public String armor;
    public String Configfile;
    private MainFile mainFile;
    private MessagesFile messagesFile;
    private ReclaimFile reclaimFile;
    private TournamentFile tournamentFile;
    private TournamentRunnable tournamentRunnable;
    private SettingsFile settingsFile;
    private GlowstoneMountainManager glowstoneMountainManager;
    private OreMountainManager oreMountainManager;
    private ArmorUtils armorUtils;
    
    public HCFaction() {
        random = new Random();
    }
    
    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    
    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < HCFaction.MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= HCFaction.HOUR) ? "HH:" : "") + "mm:ss");
    }
    
	public void onEnable() {
        registerConfiguration();
        registerConfig();
        HCFaction.plugin = this;
        (HCFaction.plugin = this).saveDefaultConfig();
        SchedulesData = new File(getDataFolder(), "event-schedules.txt");
        CustomEntityRegistration.registerCustomEntities();
        ProtocolLibHook.hook(this);
        saveDefaultConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        ConfigurationService.init(getConfig());
        PotionLimiterData.getInstance().setup(this);
        PotionLimitListener.reload();
        Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        worldEdit = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin)wep) : null);
        registerCommandsPermission();
        registerManagers();
        registerListeners();
        registerCooldowns();
        timerManager.enable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "                    &cpHCFactions"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis plugin has been loaded successfully."));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThanks for buy this plugin, contact me on"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDiscord: TulioTrivi\u00F1o#6969 or Risas#9999 "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        scoreboardTitle = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Scoreboard.TITLE").replace("|", "\u2503"));
        if (ConfigurationService.DEV) {
            for (int i = 0; i < 10; ++i) {
                getLogger().warning("SERVER HAS BEEN LOADED AS DEV VERSION! PLUGIN MAY NOT BE STABLE!");
            }
        }
        
//        if(getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
//        	PlaceholderAPI.registerPlaceholder(this, "ftop1", new PlaceholderReplacer() {
//				
//				@Override
//				public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
//			        List<PlayerFaction> data = new ArrayList<>(HCFaction.getPlugin().getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getPoints() > 0).collect(Collectors.toSet()));
//			        Collections.sort(data, POINTS_COMPARATOR);
//			        Collections.reverse(data);
////			        if(!data.isEmpty()) {
////				        PlayerFaction next = data.get(0);
////				        return next.getName();
////			        } else {
////			        	return "no existe rey";
////			        }
//			        return data.get(0).getName();
//				}
//			});
//        }
        
        if(ConfigurationService.KIT_MAP && Bukkit.getWorld("events") == null) {
        	WorldCreator creator = new WorldCreator("events");
        	creator.generator(new ChunkGenerator() {

        	    public byte[] generate(World world, Random random, int x, int z) {
        	        return new byte[32768]; //Empty byte array
        	    }
        	});
        	Bukkit.createWorld(creator);
        }
        rainCheck();
        
//        new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				List<String> players = new ArrayList<String>();
//				for(Player player : Bukkit.getOnlinePlayers()) {
//					if(player.hasPermission("top.rank") && !player.isOp() && !player.hasPermission("*")) {
//						players.add(player.getName());
//					}
//				}
//				
//				if(!players.isEmpty()) {
//					for (String onlinedonator : HCFaction.getPlugin().getConfig().getStringList("Online-Donator-Broadcast")) {
//						getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', onlinedonator)
//								.replace("%player%", players.toString().replace("[", "").replace("]", ""))
//								.replace("%store%", ConfigurationService.STORE)); 	
//					}
//				}
//			}
//		}.runTaskTimer(this, 200L, 18000L);
        
//        new TablistManager(this, new TablistProvider(), TimeUnit.MILLISECONDS.toMillis(500L));
        new SevenTab(this, new TabAdapterBeta());

        new BukkitRunnable() {
			
			@Override
			public void run() {
		        for(String commands : HCFaction.getPlugin().getConfig().getStringList("ON_STARTS_COMMANDS")) {
		        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
		        }
			}
		}.runTaskLater(this, 80L);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
	            for(World worlds : Bukkit.getWorlds()) {
	            	worlds.setTime(6000L);
	            }
			}
		}.runTaskTimer(this, 200L, 600L);
		
//		new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				UserDataFile config = UserDataFile.getConfig();
//				if(config.get("users.") != null) {
//					DailyTask();
//				}
//			}
//		}.runTaskLater(this, 60L);
//	}
//	
//	private void DailyTask() {
//		UserDataFile config = UserDataFile.getConfig();
//		for(String player : config.getConfigurationSection("users").getKeys(false)) {
//			new BukkitRunnable() {
//		        public void run() {
//		                config.set("users." + player + ".time", config.getInt("users." + player + ".time") - 1);
//		                if (config.getInt("users." + player + ".time") == 0) {
//		                	config.set("users." + player, null);
//		                	DailyCommand.COOLDOWN_TASK.remove(player);
//		                        this.cancel();
//		                }
//		        }
//			}.runTaskTimer(this, 20L, 20L);	
//		}
	}

	public void saveData() {
        BasePlugin.getPlugin().getServerHandler().saveServerData();
        if (!ConfigurationService.KIT_MAP) {
            executeMonititoredTask("Mountain Data Save", () -> {
                glowstoneMountainManager.save();
                oreMountainManager.save();
            });
            deathbanManager.saveDeathbanData();
        }
        economyManager.saveEconomyData();
        factionManager.saveFactionData();
        userManager.saveUserData();
    }
    
    public void onDisable() {
        Bukkit.getServer().savePlayers();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        CustomEntityRegistration.unregisterCustomEntities();
        CombatLogListener.removeCombatLoggers();
        pvpClassManager.onDisable();
        scoreboardHandler.clearBoards();
        saveData();
        timerManager.disable();
    }
    
    private void registerConfiguration() {
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction1.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction2.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction3.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction4.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
        ConfigurationSerialization.registerClass(MountainFaction.class);
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(GlowstoneFaction.class);
        ConfigurationSerialization.registerClass(OreFaction.class);
        ConfigurationSerialization.registerClass(DestroyTheCoreFaction.class);
    }
    
    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        if(ConfigurationService.KIT_MAP == true) {
    		manager.registerEvents(new TournamentListener(this), this);
    		manager.registerEvents(new KillStreakListener(), this);
    		manager.registerEvents(new BeaconListener(), this);
    		manager.registerEvents(new HostCommand(), this);
    		manager.registerEvents(new ClearInventoryListener(), this);
    		manager.registerEvents(new AssassinClass(this), this);
            manager.registerEvents(new BombermanClass(this), this);
        }
		manager.registerEvents(new BeaconListener(), this);
        manager.registerEvents(new BlockTNTCartListener(), this);
        manager.registerEvents(new ItemShopListener(), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new PortalFixListener(), this);
        manager.registerEvents(new ElevatorListener(this), this);
        manager.registerEvents(new EndPortalCommand(this), this);
        manager.registerEvents(new ColonFix(), this);
        manager.registerEvents(new PotionListener(), this);
        manager.registerEvents(new PexCrashFix(), this);
        manager.registerEvents(new DupeGlitchFix(), this);
        manager.registerEvents(new DonorOnlyListener(), this);
        manager.registerEvents(new ArcherClass(this), this);
        manager.registerEvents(new EndermanFixListener(), this);
        manager.registerEvents(new MinecartElevatorListener(), this);
        manager.registerEvents(new AutoSmeltOreListener(), this);
        manager.registerEvents(new BlockHitFixListener(), this);
        manager.registerEvents(new BlockJumpGlitchFixListener(), this);
        manager.registerEvents(new HungerFixListener(), this);
        manager.registerEvents(new BoatGlitchFixListener(), this);
        manager.registerEvents(new BookDeenchantListener(), this);
        manager.registerEvents(new BorderListener(), this);
        manager.registerEvents(new BottledExpListener(), this);
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(new CombatLogListener(this), this);
        manager.registerEvents(new CoreListener(this), this);
        manager.registerEvents(new CreeperFriendlyListener(), this);
        manager.registerEvents(new WrenchListener(this), this);
        manager.registerEvents(new DeathListener(this), this);
        manager.registerEvents(new DeathMessageListener(this), this);
        manager.registerEvents(new DeathSignListener(this), this);
        manager.registerEvents(new DeathbanListener(this), this);
        manager.registerEvents(new EnchantLimitListener(), this);
        manager.registerEvents(new EntityLimitListener(), this);
        manager.registerEvents(new FlatFileFactionManager(this), this);
        manager.registerEvents(new EndListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new EventSignListener(), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(new HitDetectionListener(), this);
        manager.registerEvents(new FoundDiamondsListener(), this);
        manager.registerEvents(new FurnaceSmeltSpeederListener(this), this);
        manager.registerEvents(new InfinityArrowFixListener(), this);
        manager.registerEvents(new KitListener(this), this);
        manager.registerEvents(new ItemStatTrackingListener(), this);
        manager.registerEvents(new PearlGlitchListener(this), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new FactionsCoreListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new ShopSignListener(this), this);
        manager.registerEvents(new SpawnerShopListener(this), this);
        manager.registerEvents(new SkullListener(), this);
        manager.registerEvents(new BookQuillFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(), this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(new WorldListener(this), this);
        manager.registerEvents(new SotwListener(this), this);
        manager.registerEvents(new CobbleCommand(), this);
        manager.registerEvents(new BrewingSpeedListener(this), this);
		manager.registerEvents(new ArmorFixListener(this), this);
		manager.registerEvents(new PlayersCommand(), this);
		manager.registerEvents(new AutoFeedCommand(), this);
        manager.registerEvents(new SignsListener(), this);
        manager.registerEvents(new KeyShopCommand(), this);
        manager.registerEvents(new DTCListener(this), this);
        manager.registerEvents(new KillTheKingListener(), this);
        manager.registerEvents(new WeatherFixListener(), this);
        manager.registerEvents(new DeathHistoryListener(), this);
        manager.registerEvents(new Grappling(), this);
		manager.registerEvents(new AntiTrapper(), this);
		manager.registerEvents(new Switcher(), this);
		manager.registerEvents(new SnowBall(), this);
		manager.registerEvents(new Cocaine(), this);
		manager.registerEvents(new PocketBard(), this);
		manager.registerEvents(new RawPotion(), this);
		manager.registerEvents(new Rocket(), this);
		manager.registerEvents(new NoFall(), this);
		manager.registerEvents(new SuperAxe(), this);
		manager.registerEvents(new NinjaStar(), this);
		manager.registerEvents(new Refill(), this);
		manager.registerEvents(new Murasame(), this);
		manager.registerEvents(new RotateStick(), this);
		manager.registerEvents(new BelchBomb(), this);
		manager.registerEvents(new FreezeGun(), this);
		manager.registerEvents(new ThunderAxe(), this);
		manager.registerEvents(new InteractListener(), this);
    }
    
    private void registerCooldowns() {
    	Cooldowns.createCooldown("FIGHT_COOLDOWN_DELAY");
    	Cooldowns.createCooldown("FIGHT_COOLDOWN");
        Cooldowns.createCooldown("REVIVE_COOLDOWN");
        Cooldowns.createCooldown("ASSASSIN_ITEM_COOLDOWN");
        Cooldowns.createCooldown("ARCHER_ITEM_COOLDOWN");
        Cooldowns.createCooldown("ARCHER_JUMP_COOLDOWN");
        Cooldowns.createCooldown("TOURNAMENT_COOLDOWN");
        Cooldowns.createCooldown("SLOWCHAT_DELAY");
        Cooldowns.createCooldown("FALL_DAMAGE_HULK");
        Cooldowns.createCooldown("JUMP_HULK_COOLDOWN");
        Cooldowns.createCooldown("HULK_FEATHER_COOLDOWN");
        Cooldowns.createCooldown("HULK_INGOT_COOLDOWN");
    }
    
    private void registerCommandsPermission() {
        getCommand("lives").setExecutor(new LivesExecutor(this));
    	getCommand("tournament").setExecutor(new TournamentExecutor());
    	getCommand("host").setExecutor(new HostCommand());
    	getCommand("killtheking").setExecutor(new KillTheKingCommand());
    	getCommand("stafflist").setExecutor(new StaffListCommand());
    	getCommand("customenchant").setExecutor(new CustomEnchantCommand());
    	getCommand("revive").setExecutor(new ReviveCommand(this));
    	getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        getCommand("teamspeak").setExecutor(new TeamspeakCommand());
        getCommand("twitter").setExecutor(new TwitterCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("website").setExecutor(new WebsiteCommand());
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        getCommand("statreset").setExecutor(new StatResetCommand(this));
        getCommand("togglefd").setExecutor(new ToggleFDCommand());
        getCommand("ffa").setExecutor(new FFACommand());
        getCommand("endportal").setExecutor(new EndPortalCommand(this));
        getCommand("toggleend").setExecutor(new ToggleEndCommand(this));
        getCommand("focus").setExecutor(new FocusCommand(this));
        getCommand("sendcoords").setExecutor(new SendCoordsCommand(this));
        getCommand("spawner").setExecutor(new SpawnerCommand(this));
        getCommand("sotw").setExecutor(new SotwCommand(this));
        getCommand("conquest").setExecutor(new ConquestExecutor(this));
        getCommand("wrench").setExecutor(new WrenchCommand());
        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("eotw").setExecutor(new EotwCommand(this));
        getCommand("game").setExecutor(new EventExecutor(this));
        getCommand("help").setExecutor(new HelpCommand());
        getCommand("faction").setExecutor(new FactionExecutor(this));
        getCommand("gopple").setExecutor(new GoppleCommand(this));
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("koth").setExecutor(new KothExecutor(this));
        getCommand("death").setExecutor(new DeathExecutor(this));
        getCommand("location").setExecutor(new LocationCommand(this));
        getCommand("logout").setExecutor(new LogoutCommand(this));
        getCommand("mapkit").setExecutor(new MapKitCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("coords").setExecutor(new CoordsCommand(this));
        getCommand("servertime").setExecutor(new ServerTimeCommand());
        getCommand("timer").setExecutor(new TimerExecutor(this));
        getCommand("savedata").setExecutor(new SaveDataCommand());
        getCommand("setborder").setExecutor(new SetBorderCommand());
        getCommand("safestop").setExecutor(new SafestopCommand());
        getCommand("nether").setExecutor(new NetherCommand(this));
        getCommand("cobble").setExecutor(new CobbleCommand());
        getCommand("ores").setExecutor(new OresCommand());
        getCommand("wrenchgive").setExecutor(new WrenchGiveCommand());
        getCommand("reclaim").setExecutor(new ReclaimCommand(this));
        getCommand("setclaim").setExecutor(new SetClaimCommand(this));
    	getCommand("craft").setExecutor(new CraftCommand());
    	getCommand("sale").setExecutor(new SaleCommand(this));
    	getCommand("keyall").setExecutor(new KeyAllCommand(this));
    	getCommand("opkeyall").setExecutor(new OPKeyAllCommand(this));
    	getCommand("spawn").setExecutor(new SpawnCommand(this));
    	getCommand("set").setExecutor(new SetCommand());
    	getCommand("players").setExecutor(new PlayersCommand());
        LFFCommand lffCommand = new LFFCommand(this);
        getCommand("lff").setExecutor(new LFFCommand(this));
        getCommand("lffalerts").setExecutor(lffCommand);
        getCommand("fight").setExecutor(new FightCommand());
        getCommand("enddragon").setExecutor(new EndDragonCommand(this));
        getCommand("autofeed").setExecutor(new AutoFeedCommand());
        getCommand("statsmanager").setExecutor(new StatsManagerCommand());
        getCommand("keyshop").setExecutor(new KeyShopCommand());
        getCommand("destroythecore").setExecutor(new DTCCommand());
        getCommand("glowstone").setExecutor(new GlowstoneMountainCommand(this));
        getCommand("oremountain").setExecutor(new OreMountainCommand(this));
        getCommand("ability").setExecutor(new AbilityCommand());
        getCommand("daily").setExecutor(new DailyCommand());
        getCommand("togglehomerange").setExecutor(new ToggleHomeRangeCommand());
        
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>)getDescription().getCommands();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            PluginCommand command = getCommand((String)entry.getKey());
            command.setPermission("hcf.command." + entry.getKey());
        }
    }
    
    private void registerManagers() {
        claimHandler = new ClaimHandler(this);
        deathbanManager = new FlatFileDeathbanManager(this);
        economyManager = new FlatFileEconomyManager(this);
        eotwHandler = new EOTWHandler(this);
        eventScheduler = new EventScheduler(this);
        factionManager = new FlatFileFactionManager(this);
        pvpClassManager = new PvpClassManager(this);
        timerManager = new TimerManager(this);
        scoreboardHandler = new ScoreboardHandler(this);
        userManager = new UserManager(this);
        visualiseHandler = new VisualiseHandler();
        sotwTimer = new SotwTimer();
        saleTimer = new SaleTimer();
        keyAllTimer = new KeyAllTimer();
        opKeyAllTimer = new OPKeyAllTimer();
        message = new Message(this);
        reclaimManager = new Reclaim(this);
		tournamentManager = new TournamentManager();
		if (!ConfigurationService.KIT_MAP && !ConfigurationService.ORIGINS) {
            executeMonititoredTask("Mountain Managers Loading", () -> {
                glowstoneMountainManager = new GlowstoneMountainManager(this);
                oreMountainManager = new OreMountainManager(this);
            });
        }
    }
    
    private void executeMonititoredTask(String name, Runnable runnable){
        long start = System.nanoTime();
        runnable.run();
        long end = System.nanoTime();
        long diff = end - start;
        if(diff > 100000000){
            double millis = diff / 1000000D;
            String output = DateTimeFormats.REMAINING_SECONDS.get().format(millis);
            getLogger().info("Running [" + name + "] took " + output + "ms");
        }
    }
    
    public void registerConfig() {
        File config = new File(getDataFolder(), "config.yml");
        Configfile = config.getPath();
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }
    
    private void rainCheck() {
        for (World w : this.getServer().getWorlds()) {
            if (!this.getConfig().getBoolean("rain." + w.getName()) && w.hasStorm()) {
                w.setStorm(false);
            }
        }
    }
    
    public ServerHandler getServerHandler() {
    	return BasePlugin.getPlugin().getServerHandler();
    }
    
    static {
        SPACE_JOINER = Joiner.on(' ');
        COMMA_JOINER = Joiner.on(", ");
        POTION_TIME = 21;
        HOUR = TimeUnit.HOURS.toMillis(1L);
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
    }
}
