package net.bfcode.bfhcf.faction.type;

import com.google.common.collect.Lists;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.GenericUtils;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.event.FactionClaimChangeEvent;
import net.bfcode.bfhcf.faction.event.FactionClaimChangedEvent;
import net.bfcode.bfhcf.faction.event.cause.ClaimChangeCause;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import java.util.Collections;
import org.bukkit.Location;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;
import com.google.common.collect.ImmutableMap;

public class ClaimableFaction extends Faction
{
    protected static ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS;
    protected Set<Claim> claims;
    
    public ClaimableFaction(String name) {
        super(name);
        this.claims = new HashSet<Claim>();
    }
    
    public ClaimableFaction(Map<String, Object> map) {
        super(map);
        (this.claims = new HashSet<Claim>()).addAll(GenericUtils.createList(map.get("claims"), Claim.class));
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList<>(this.claims));
        return map;
    }
    
    @Override
    public void printDetails(CommandSender sender) {
        List<String> toSend = new ArrayList<String>();
        for (String string : HCFaction.getPlugin().getConfig().getStringList("faction-settings.show.system-faction")) {
            string = string.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "");
            string = string.replace("%FACTION%", this.getDisplayName(sender));
            if (string.contains("%WORLD%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%WORLD%", ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + "");
                }
            }
            if (string.contains("%X%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%X%", location.getBlockX() + "");
                }
            }
            if (string.contains("%Z%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%Z%", location.getBlockZ() + "");
                }
            }
            toSend.add(string);
        }
        for (String message : toSend) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
    
    public Set<Claim> getClaims() {
        return this.claims;
    }
    
    public boolean addClaim(Claim claim, CommandSender sender) {
        return this.addClaims(Collections.singleton(claim), sender);
    }
    
    public boolean addClaims(Collection<Claim> adding, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }
    
    public boolean removeClaim(Claim claim, CommandSender sender) {
        return this.removeClaims(Collections.singleton(claim), sender);
    }
    
    public boolean removeClaims(Collection<Claim> removing, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        int previousClaims = this.claims.size();
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return false;
        }
        for (Claim claim : Lists.newArrayList(removing)) {
            while (this.claims.contains(claim)) {
                this.claims.remove(claim);
            }
        }
        if (this instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)this;
            Location home = playerFaction.getHome();
            HCFaction plugin = HCFaction.getPlugin();
            int refund = 0;
            for (Claim claim2 : removing) {
                refund += plugin.getClaimHandler().calculatePrice((Cuboid)claim2, previousClaims, true);
                if (previousClaims > 0) {
                    --previousClaims;
                }
                if (home != null) {
                    if (!claim2.contains(home)) {
                        continue;
                    }
                    playerFaction.setHome(null);
                    playerFaction.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
                    break;
                }
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
            playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
    
    static {
        ENVIRONMENT_MAPPINGS = ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End");
    }
}
