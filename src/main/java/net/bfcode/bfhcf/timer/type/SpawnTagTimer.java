package net.bfcode.bfhcf.timer.type;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.command.CommandSender;

import com.google.common.base.Predicate;

import net.bfcode.bfbase.kit.event.KitApplyEvent;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.event.PlayerClaimEnterEvent;
import net.bfcode.bfhcf.faction.event.PlayerJoinFactionEvent;
import net.bfcode.bfhcf.faction.event.PlayerLeaveFactionEvent;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.timer.event.TimerClearEvent;
import net.bfcode.bfhcf.timer.event.TimerStartEvent;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.visualise.VisualBlock;
import net.bfcode.bfhcf.visualise.VisualType;

import org.bukkit.Bukkit;
import com.google.common.base.Optional;
import java.util.UUID;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnTagTimer extends PlayerTimer implements Listener
{
    private static long NON_WEAPON_TAG = 5000L;
    private HCFaction plugin;
    
    public SpawnTagTimer(HCFaction plugin) {
        super(ConfigurationService.SPAWNTAG_TIMER, TimeUnit.SECONDS.toMillis(HCFaction.getPlugin().getConfig().getInt("timers.spawntag-time")));
        this.plugin = plugin;
    }
    
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.SPAWNTAG_COLOUR;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitApply(KitApplyEvent event) {
        Player player = event.getPlayer();
        long remaining;
        if (!event.isForce() && (remaining = this.getRemaining(player)) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot apply kits whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (HCFaction.getPlugin().getConfig().getBoolean("Place-on-combat") && this.getRemaining(event.getPlayer()) > 0L && this.plugin.getFactionManager().getPlayerFaction(event.getPlayer()) != null && this.plugin.getFactionManager().getPlayerFaction(event.getPlayer()).equals(this.plugin.getFactionManager().getFactionAt(event.getBlock()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.translate("&cYou may not place blocks while your &4&lCombat &ctimer is active!"));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (HCFaction.getPlugin().getConfig().getBoolean("Break-on-combat") && this.getRemaining(event.getPlayer()) > 0L && this.plugin.getFactionManager().getPlayerFaction(event.getPlayer()) != null && this.plugin.getFactionManager().getPlayerFaction(event.getPlayer()).equals(this.plugin.getFactionManager().getFactionAt(event.getBlock()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.translate("&cYou may not break blocks while your &4&lCombat &ctimer is active!"));
        }
    }
    
    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	if(this.getRemaining(event.getPlayer()) > 0L) {
    		for(String commands : HCFaction.getPlugin().getConfig().getStringList("BLOCK_COMMANDS_IN_COMBAT")) {
    	        long remaining = this.getRemaining(player);
    			if(event.getMessage().equalsIgnoreCase(commands)) {
    				event.setCancelled(true);
    				player.sendMessage(CC.translate(HCFaction.getPlugin().getConfig().getString("BLOCK_COMMANDS_IN_COMBAT_MESSAGE")
    						.replace("%remaining%", HCFaction.getRemaining(remaining, true, false))));
    			}
    		}
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(TimerClearEvent event) {
        Optional<UUID> optionalUserUUID;
        if (event.getTimer().equals(this) && (optionalUserUUID = event.getUserUUID()).isPresent()) {
            this.onExpire((UUID)optionalUserUUID.get());
        }
    }
    
    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(PlayerJoinFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        Player player;
        long remaining;
        if (optional.isPresent() && (remaining = this.getRemaining(player = (Player)optional.get())) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        Player player;
        if (optional.isPresent() && this.getRemaining(player = (Player)optional.get()) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
            return;
        }
        Player player = event.getPlayer();
        if (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && this.getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot enter " + event.getToFaction().getDisplayName(player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCFaction.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
        Entity entity;

        if (attacker != null && (entity = event.getEntity()) instanceof Player) {
            Player attacked = (Player)entity;
            boolean weapon = event.getDamager() instanceof Arrow;
            if(HCFaction.getPlugin().getTournamentManager().isInTournament(attacker) || HCFaction.getPlugin().getTournamentManager().isInTournament(attacked)) {
            	return;
            }
            if (!weapon) {
                ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }
            long duration = weapon ? this.defaultCooldown : 30000L;
            this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
            this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(TimerStartEvent event) {
        Optional<Player> optional;
        if (event.getTimer().equals(this) && (optional = event.getPlayer()).isPresent()) {
            Player player = (Player)optional.get();
            player.sendMessage(ChatColor.YELLOW + "You are now spawn-tagged for " + ChatColor.RED + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.YELLOW + '.');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.clearCooldown(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()) {
            this.clearCooldown(event.getPlayer());
        }
    }
}
