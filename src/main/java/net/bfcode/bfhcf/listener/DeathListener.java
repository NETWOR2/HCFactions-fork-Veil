package net.bfcode.bfhcf.listener;

import java.util.concurrent.TimeUnit;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import net.bfcode.bfbase.util.JavaUtils;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.user.FactionUser;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.EntityLightning;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class DeathListener implements Listener
{
    private static long REGEN_DELAY;
    public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
    public static HashMap<UUID, ItemStack[]> PlayerArmorContents;
    private HCFaction plugin;
    
    public DeathListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        
        if (killer != null && !HCFaction.getPlugin().getTournamentManager().isInTournament(killer)) {
            FactionUser user = this.plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
        }
        
        if(event.getEntity() instanceof Player) {
            if(event.getEntity() != null && !HCFaction.getPlugin().getTournamentManager().isInTournament(event.getEntity())) {
            	Player player = event.getEntity();
                FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
                user.setDeaths(user.getDeaths() + 1);
            }	
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDeathKitMap(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Player k = event.getEntity().getKiller();
        
        if(k instanceof Player && !HCFaction.getPlugin().getTournamentManager().isInTournament(k)) {
        	PlayerFaction killerFaction = this.plugin.getFactionManager().getPlayerFaction(k.getUniqueId());
        
        	if (ConfigurationService.KIT_MAP && k != null) {
            	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
            	SkullMeta meta = (SkullMeta)skull.getItemMeta();
            	meta.setOwner(p.getName());
            	skull.setItemMeta((ItemMeta)meta);
            	k.getInventory().addItem(new ItemStack[] { skull });
        	}
        
        	if (killerFaction != null) {
            	killerFaction.setPoints(killerFaction.getPoints() + this.plugin.getConfig().getInt("Points.win-per-kill"));
            	killerFaction.broadcast(CC.translate("&a" + k.getName() + " &eHas gotten &6" + this.plugin.getConfig().getInt("Points.win-per-kill") + " &epoint &efor your faction"));

            	if(p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL 
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FIRE 
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LIGHTNING 
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.WITHER 
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.DROWNING
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.MAGIC
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA
            			|| p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            		return;
            	}
        	}
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction != null && !HCFaction.getPlugin().getTournamentManager().isInTournament(player)) {
            Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            Role role = playerFaction.getMember(player.getUniqueId()).getRole();
            if (playerFaction.getDeathsUntilRaidable() >= -5.0) {
                playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossMultiplier());
                }
                else {
                    playerFaction.setRemainingRegenerationTime(REGEN_DELAY);
                }
                playerFaction.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE + role.getAstrix() + player.getName() + ChatColor.YELLOW + " DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format((Number)playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
    			playerFaction.setPoints(playerFaction.getPoints() - this.plugin.getConfig().getInt("Points.loss-per-death"));
                playerFaction.broadcast(CC.translate("&eYour faction has lost &6" + this.plugin.getConfig().getInt("Points.loss-per-death") + " point&e because &a" + player.getName() + " &edied!"));

                if(ConfigurationService.KIT_MAP == true) {
                	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "faction setdtrregen " + player.getName() + " " + this.plugin.getConfig().getString("DEATH_TIME.KIT_MAP"));
            	} else if(ConfigurationService.KIT_MAP == false && !this.plugin.getTournamentManager().isInTournament(player)) {
                	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "faction setdtrregen " + player.getName() + " " + this.plugin.getConfig().getString("DEATH_TIME.HCF"));
            	}
        }
		
        if (Bukkit.spigot().getTPS()[0] > 15.0 && !ConfigurationService.KIT_MAP) {
            DeathListener.PlayerInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
            DeathListener.PlayerArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
            Location location = player.getLocation();
            WorldServer worldServer = ((CraftWorld)location.getWorld()).getHandle();
            EntityLightning entityLightning = new EntityLightning((World)worldServer, location.getX(), location.getY(), location.getZ(), false);
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather((Entity)entityLightning);
            for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                if (this.plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket((Packet)packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void addTotalKills(PlayerDeathEvent event) {
    	Player player = event.getEntity();

    	PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
    	if(playerFaction != null) {
    		if(playerFaction.getTotalKillsFaction() == 0) {
    			return;
    		}
    		if(playerFaction.getTotalKillsFaction() > 0) {
    			playerFaction.setTotalKillsFaction(playerFaction.getTotalKillsFaction() - 1);
    		}
    	}
    	if(event.getEntity().getKiller() instanceof Player) {
    		Player killer = event.getEntity().getKiller();
    		PlayerFaction killerFaction = this.plugin.getFactionManager().getPlayerFaction(killer.getUniqueId());
    		if(killerFaction != null) {
    			killerFaction.setTotalKillsFaction(killerFaction.getTotalKillsFaction() + 1);
    		}
    	}
    }
    
    static {
        REGEN_DELAY = TimeUnit.MINUTES.toMillis(60);
        DeathListener.PlayerInventoryContents = new HashMap<UUID, ItemStack[]>();
        DeathListener.PlayerArmorContents = new HashMap<UUID, ItemStack[]>();
    }
}
