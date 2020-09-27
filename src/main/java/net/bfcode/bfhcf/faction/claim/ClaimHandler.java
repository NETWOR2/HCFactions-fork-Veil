package net.bfcode.bfhcf.faction.claim;

import org.bukkit.Material;
import java.util.Set;
import org.bukkit.command.CommandSender;
import com.google.common.base.Preconditions;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionManager;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.ClaimableFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.faction.type.RoadFaction;
import net.bfcode.bfhcf.faction.type.WildernessFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.visualise.VisualType;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

import java.util.concurrent.ConcurrentMap;
import org.bukkit.inventory.ItemStack;

public class ClaimHandler
{
    public static int MIN_CLAIM_HEIGHT = 0;
    public static int MAX_CLAIM_HEIGHT = 256;
    public static ItemStack CLAIM_WAND;
    public static int MIN_CLAIM_RADIUS = 5;
    public static int MAX_CHUNKS_PER_LIMIT = 16;
    private static int NEXT_PRICE_MULTIPLIER_AREA = 250;
    private static int NEXT_PRICE_MULTIPLIER_CLAIM = 500;
    private static double CLAIM_SELL_MULTIPLIER = 0.8;
    public static double CLAIM_PRICE_PER_BLOCK = 0.25;
	public static Object NEARBY_CLAIM_RADIUS;
    public ConcurrentMap<Object, Object> claimSelectionMap;
    private HCFaction plugin;
    
    public ClaimHandler(HCFaction plugin) {
        this.plugin = plugin;
        this.claimSelectionMap = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build().asMap();
    }
    
    public int calculatePrice(Cuboid claim, int currentClaims, boolean selling) {
        if (currentClaims == -1 || !claim.hasBothPositionsSet()) {
            return 0;
        }
        int multiplier = 1;
        int remaining = claim.getArea();
        double price = 0.0;
        while (remaining > 0) {
            if (--remaining % NEXT_PRICE_MULTIPLIER_AREA == 0) {
                ++multiplier;
            }
            price += CLAIM_PRICE_PER_BLOCK * multiplier;
        }
        if (currentClaims != 0) {
            currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
            price += currentClaims * NEXT_PRICE_MULTIPLIER_CLAIM;
        }
        if (selling) {
            price *= CLAIM_SELL_MULTIPLIER;
        }
        return (int)price;
    }
    
    public boolean clearClaimSelection(Player player) {
        ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.remove(player.getUniqueId());
        if (claimSelection != null) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, null);
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("deprecation")
    public boolean canSubclaimHere(Player player, Location location) {
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to subclaim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (!this.plugin.getFactionManager().getFactionAt(location).equals(playerFaction)) {
            player.sendMessage(ChatColor.RED + "This location is not part of your factions' territory.");
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    public boolean canClaimHere(Player player, Location location) {
        World world = location.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        if (!(this.plugin.getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the " + ConfigurationService.WILDERNESS_COLOUR + "Wilderness" + ChatColor.RED + ". Make sure you are past " + ConfigurationService.WARZONE_RADIUS + " blocks from spawn..");
            return false;
        }
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
            player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        int locX = location.getBlockX();
        int locZ = location.getBlockZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = locX - 5; x < locX + 5; ++x) {
            for (int z = locZ - 5; z < locZ + 5; ++z) {
                Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (!(factionAtNew instanceof RoadFaction)) {}
                if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "This position contains enemy claims within a " + MIN_CLAIM_RADIUS + " block buffer radius.");
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean tryPurchasing(Player player, Claim claim) {
        Preconditions.checkNotNull((Object)claim, (Object)"Claim is null");
        World world = claim.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        @SuppressWarnings("deprecation")
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
            player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        int factionBalance = playerFaction.getBalance();
        int claimPrice = this.calculatePrice((Cuboid)claim, playerFaction.getClaims().size(), false);
        if (claimPrice > factionBalance) {
            player.sendMessage(ChatColor.RED + "Your faction bank only has " + '$' + factionBalance + ", the price of this claim is " + '$' + claimPrice + '.');
            return false;
        }
        if (claim.getChunks().size() > 16) {
            player.sendMessage(ChatColor.RED + "Claims cannot exceed " + 16 + " chunks.");
            return false;
        }
        if (claim.getWidth() < 5 || claim.getLength() < 5) {
            player.sendMessage(ChatColor.RED + "Claims must be at least " + 5 + 'x' + 5 + " blocks.");
            return false;
        }
        int minimumX = claim.getMinimumX();
        int maximumX = claim.getMaximumX();
        int minimumZ = claim.getMinimumZ();
        int maximumZ = claim.getMaximumZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = minimumX; x < maximumX; ++x) {
            for (int z = minimumZ; z < maximumZ; ++z) {
                Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (factionAt != null && !(factionAt instanceof WildernessFaction)) {
                    player.sendMessage(ChatColor.RED + "This claim contains a location not within the " + ChatColor.GRAY + "Wilderness" + ChatColor.RED + '.');
                    return false;
                }
            }
        }
        for (int x = minimumX - 1; x < maximumX + 1; ++x) {
            for (int z = minimumZ - 1; z < maximumZ + 1; ++z) {
                Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (!(factionAtNew instanceof RoadFaction)) {}
                if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "This claim contains enemy claims within a " + 1 + " block buffer radius.");
                    return false;
                }
            }
        }
        @SuppressWarnings("unused")
		Location minimum = claim.getMinimumPoint();
        @SuppressWarnings("unused")
		Location maximum = claim.getMaximumPoint();
        Set<Claim> otherClaims = playerFaction.getClaims();
        boolean conjoined = otherClaims.isEmpty();
        if (!conjoined) {
            player.sendMessage(ChatColor.RED + "Use /f unclaim to resize your faction claims.");
            return false;
        }
        claim.setY1(0);
        claim.setY2(256);
        if (!playerFaction.addClaim(claim, (CommandSender)player)) {
            return false;
        }
        Location center = claim.getCenter();
        player.sendMessage(ChatColor.YELLOW + "Claim has been purchased for " + ChatColor.GREEN + '$' + claimPrice + ChatColor.YELLOW + '.');
        playerFaction.setBalance(factionBalance - claimPrice);
        playerFaction.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " claimed land for your faction at " + ChatColor.GOLD + '(' + center.getBlockX() + ", " + center.getBlockZ() + ')' + ChatColor.GREEN + '.', player.getUniqueId());
        return true;
    }
    
    static {
        CLAIM_WAND = new ItemBuilder(Material.DIAMOND_HOE).displayName(ChatColor.LIGHT_PURPLE.toString() + "Claim Wand").lore(new String[] { ChatColor.YELLOW + "Left or Right Click " + ChatColor.RED + "a Block" + ChatColor.YELLOW + " to:", ChatColor.GRAY + "Set the first and second position of ", ChatColor.GRAY + "your Claim selection.", "", ChatColor.YELLOW + "Right Click " + ChatColor.RED + "the Air" + ChatColor.YELLOW + " to:", ChatColor.GRAY + "Clear your current Claim selection.", "", ChatColor.YELLOW + "Shift " + ChatColor.YELLOW + "Left Click " + ChatColor.RED + "the Air or a Block" + ChatColor.YELLOW + " to:", ChatColor.GRAY + "Purchase your current Claim selection." }).build();
    }
}
