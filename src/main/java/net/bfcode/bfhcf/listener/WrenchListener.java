package net.bfcode.bfhcf.listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Optional;

import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfbase.util.ParticleEffect;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionMember;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.GuavaCompat;
import net.bfcode.bfhcf.wrench.Wrench;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class WrenchListener implements Listener
{
    private HCFaction plugin;
    
    public WrenchListener(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Optional<Wrench> crowbarOptional;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem() && (crowbarOptional = Wrench.fromStack(event.getItem())).isPresent()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            World world = player.getWorld();
            if (world.getEnvironment() != World.Environment.NORMAL) {
                player.sendMessage(ChatColor.RED + "Wrench may only be used in the overworld.");
                return;
            }
            Block block = event.getClickedBlock();
            Location blockLocation = block.getLocation();
            if (!FactionsCoreListener.attemptBuild((Entity)player, blockLocation, ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.')) {
                return;
            }
            Wrench wrench = (Wrench)crowbarOptional.get();
            BlockState blockState = block.getState();
            if (blockState instanceof CreatureSpawner) {
                int remainingUses = wrench.getSpawnerUses();
                if (remainingUses <= 0) {
                    player.sendMessage(ChatColor.RED + "This wrench has no more Spawner uses.");
                    return;
                }
                wrench.setSpawnerUses(remainingUses - 1);
                player.setItemInHand(wrench.getItemIfPresent());
                CreatureSpawner spawner = (CreatureSpawner)blockState;
                block.setType(Material.AIR);
                blockState.update();
                world.dropItemNaturally(blockLocation, new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").data((short)spawner.getData().getData()).loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner.getSpawnedType().name())).build());
            }
            else if (block.getType() == Material.ENDER_PORTAL_FRAME) {
                if (block.getType() != Material.ENDER_PORTAL_FRAME) {
                    return;
                }
                int remainingUses = wrench.getEndFrameUses();
                if (remainingUses <= 0) {
                    player.sendMessage(ChatColor.RED + "This wrench has no more End Portal Frame uses.");
                    return;
                }
                boolean destroyed = false;
                int blockX = blockLocation.getBlockX();
                int blockY = blockLocation.getBlockY();
                int blockZ = blockLocation.getBlockZ();
                for (int searchRadius = 4, x = blockX - searchRadius; x <= blockX + searchRadius; ++x) {
                    for (int z = blockZ - searchRadius; z <= blockZ + searchRadius; ++z) {
                        Block next = world.getBlockAt(x, blockY, z);
                        if (next.getType() == Material.ENDER_PORTAL) {
                            next.setType(Material.AIR);
                            next.getState().update();
                            destroyed = true;
                        }
                    }
                }
                if (destroyed) {
                    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Ender Portal is no longer active");
                    if (playerFaction != null) {
                        boolean informFaction = false;
                        for (Claim claim : playerFaction.getClaims()) {
                            if (!claim.contains(blockLocation)) {
                                continue;
                            }
                            informFaction = true;
                            break;
                        }
                        if (informFaction) {
                            FactionMember factionMember = playerFaction.getMember(player);
                            String astrix = factionMember.getRole().getAstrix();
                            playerFaction.broadcast(astrix + ConfigurationService.TEAMMATE_COLOUR + " has used a Wrench de-activating one of the factions' end portals.", player.getUniqueId());
                        }
                    }
                }
                wrench.setEndFrameUses(remainingUses - 1);
                player.setItemInHand(wrench.getItemIfPresent());
                block.setType(Material.AIR);
                blockState.update();
                world.dropItemNaturally(blockLocation, new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
            }
            else {
                if (block.getType() != Material.DRAGON_EGG) {
                    return;
                }
                int remainingUses = wrench.getEndDragonUses();
                if (remainingUses != 1) {
                    player.sendMessage(ChatColor.RED + "This wrench has no more Dragon egg uses.");
                    return;
                }
                wrench.setEndDragonUses(0);
                player.setItemInHand(wrench.getItemIfPresent());
                block.setType(Material.AIR);
                blockState.update();
                String[] arrstring = { ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " took from " + ((this.plugin.getFactionManager().getFactionAt(blockLocation) != null) ? this.plugin.getFactionManager().getFactionAt(blockLocation).getName() : "wilderness") };
                world.dropItemNaturally(blockLocation, new ItemBuilder(Material.DRAGON_EGG).displayName(ChatColor.WHITE + "Dragon Egg").lore(arrstring).build());
            }
            if (event.getItem().getType() == Material.AIR) {
                player.playSound(blockLocation, Sound.ITEM_BREAK, 1.0f, 1.0f);
            }
            else {
                ParticleEffect.ENCHANTMENT_TABLE.display(player, blockLocation, 0.125f, 50);
                player.playSound(blockLocation, Sound.LEVEL_UP, 1.0f, 1.0f);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack stack = event.getItemInHand();
        Player player = event.getPlayer();
        if (block.getState() instanceof CreatureSpawner && stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasLore() && meta.hasDisplayName()) {
                CreatureSpawner spawner = (CreatureSpawner)block.getState();
                List<String> lore = (List<String>)meta.getLore();
                if (!lore.isEmpty()) {
                    String spawnerName = ChatColor.stripColor(lore.get(0).toUpperCase());
                    Optional<EntityType> entityTypeOptional = GuavaCompat.getIfPresent(EntityType.class, spawnerName);
                    if (entityTypeOptional.isPresent()) {
                        spawner.setSpawnedType((EntityType)entityTypeOptional.get());
                        spawner.update(true, true);
                        player.sendMessage(ChatColor.YELLOW + "Placed a " + ChatColor.GREEN + spawnerName + " Spawner.");
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareCrowbarCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        if (event.isRepair() == true && event.getRecipe().getResult().getType() == Wrench.WRENCH_TYPE) {
            int endFrameUses = 0;
            int spawnerUses = 0;
            int dragonUses = 0;
            boolean changed = false;
            @SuppressWarnings("unused")
			ItemStack[] matrix3;
            ItemStack[] array;
            @SuppressWarnings("unused")
			ItemStack[] matrix2 = array = (matrix3 = inventory.getMatrix());
            for (ItemStack ingredient : array) {
                Optional<Wrench> crowbarOptional = Wrench.fromStack(ingredient);
                if (crowbarOptional.isPresent()) {
                    Wrench wrench = (Wrench)crowbarOptional.get();
                    spawnerUses += wrench.getSpawnerUses();
                    dragonUses += wrench.getEndDragonUses();
                    endFrameUses += wrench.getEndFrameUses();
                    changed = true;
                }
            }
            if (changed) {
                inventory.setResult(new Wrench(spawnerUses, endFrameUses, dragonUses).getItemIfPresent());
            }
        }
    }
}
