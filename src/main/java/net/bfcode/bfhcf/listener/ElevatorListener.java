package net.bfcode.bfhcf.listener;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.faction.type.WarzoneFaction;
import net.bfcode.bfhcf.faction.type.WildernessFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.timer.PlayerTimer;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class ElevatorListener implements Listener
{
    private HCFaction plugin;
    private String prefix;
    private String signTitle;
    
    public ElevatorListener(HCFaction plugin) {
        this.plugin = plugin;
        this.signTitle = ChatColor.GOLD + ChatColor.BOLD.toString() + "[Elevator]";
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignUpdate(SignChangeEvent e) {
        if (StringUtils.containsIgnoreCase(e.getLine(0), "Elevator")) {
            boolean up;
            if (StringUtils.containsIgnoreCase(e.getLine(1), "Up")) {
                up = true;
            }
            else {
                if (!StringUtils.containsIgnoreCase(e.getLine(1), "Down")) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Incorrect usage: Up or Down");
                    return;
                }
                up = false;
            }
            e.setLine(0, this.signTitle);
            e.setLine(1, up ? "Up" : "Down");
            e.setLine(2, "");
            e.setLine(3, "");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            Player player = e.getPlayer();
            Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            if ((factionAt.equals(playerFaction) || factionAt instanceof SpawnFaction || factionAt instanceof WildernessFaction || factionAt instanceof WarzoneFaction || factionAt instanceof EventFaction || factionAt instanceof RoadFaction) && block.getState() instanceof Sign) {
                Sign sign = (Sign)block.getState();
                String[] lines = sign.getLines();
                if (lines[0].equals(this.signTitle)) {
                    boolean up;
                    if (lines[1].equalsIgnoreCase("Up")) {
                        up = true;
                    }
                    else {
                        if (!lines[1].equalsIgnoreCase("Down")) {
                            return;
                        }
                        up = false;
                    }
                    this.signClick(e.getPlayer(), sign.getLocation(), up);
                }
            } else if(!factionAt.equals(playerFaction) && block.getState() instanceof Sign) {
            	player.sendMessage(CC.translate("&cYou can't use enemies elevators."));
            }
        }
    }
    
    public boolean signClick(Player player, Location signLocation, boolean up) {
        Block block = signLocation.getBlock();
        do {
            block = block.getRelative(up ? BlockFace.UP : BlockFace.DOWN);
            if (block.getY() > block.getWorld().getMaxHeight() || block.getY() <= 1) {
                player.sendMessage(ChatColor.RED + "Could not find a sign " + (up ? "above" : "below") + " to teleport you to.");
                return false;
            }
        } while (!this.isSign(block));
        boolean underSafe = this.isSafe(block.getRelative(BlockFace.DOWN));
        boolean overSafe = this.isSafe(block.getRelative(BlockFace.UP));
        if (player.getGameMode().equals((Object)GameMode.CREATIVE)) {
            Location location = player.getLocation().clone();
            location.setX(block.getX() + 0.5);
            location.setY((double)(block.getY() + (underSafe ? -1 : 0)));
            location.setZ(block.getZ() + 0.5);
            location.setPitch(0.0f);
            player.teleport(location);
            return true;
        }
/*        Faction at = this.plugin.getFactionManager().getFactionAt(block);
        if (at instanceof PlayerFaction && ((PlayerFaction)at).getMember(player) == null && !ConfigurationService.KIT_MAP) {
            player.sendMessage(ChatColor.RED + "You cannot use this in the territory of " + ChatColor.stripColor(at.getDisplayName((CommandSender)player)));
            return false;
        }
        PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
        long remaining = timer.getRemaining(player);
        if ((timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player) > 0L && !ConfigurationService.KIT_MAP) {
            player.sendMessage(ChatColor.RED + "You can not use this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
            return false;
        } */
        if (!underSafe && !overSafe) {
            player.sendMessage(ChatColor.RED + "There is a block blocking the sign " + (up ? "above" : "below") + "!");
            return false;
        }
        Location location2 = player.getLocation().clone();
        location2.setX(block.getX() + 0.5);
        location2.setY((double)(block.getY() + (underSafe ? -1 : 0)));
        location2.setZ(block.getZ() + 0.5);
        location2.setPitch(0.0f);
        player.teleport(location2);
        return true;
    }
    
    private boolean isSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            String[] lines = sign.getLines();
            return lines[0].equals(this.signTitle) && (lines[1].equalsIgnoreCase("Up") || lines[1].equalsIgnoreCase("Down"));
        }
        return false;
    }
    
    private boolean isSafe(Block block) {
        return block != null && !block.getType().isSolid() && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS;
    }
}
