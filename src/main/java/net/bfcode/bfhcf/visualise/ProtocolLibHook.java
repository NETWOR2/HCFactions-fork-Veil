package net.bfcode.bfhcf.visualise;

import org.bukkit.Material;
import com.comphenix.protocol.ProtocolManager;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.bfcode.bfhcf.HCFaction;
import net.minecraft.server.v1_7_R4.EntityHuman;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.ProtocolLibrary;

public class ProtocolLibHook
{
    private static int STARTED_DIGGING = 0;
    private static int FINISHED_DIGGING = 2;
    
    public static void hook(HCFaction hcf) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener((PacketListener)new PacketAdapter(hcf, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier modifier = packet.getIntegers();
                Player player = event.getPlayer();
                try {
                    int face;
                    if (modifier.size() < 4 || (face = (int)modifier.read(3)) == 255) {
                        return;
                    }
                    Location location = new Location(player.getWorld(), (double)(int)modifier.read(0), (double)(int)modifier.read(1), (double)(int)modifier.read(2));
                    VisualBlock visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location);
                    if (visualBlock == null) {
                        return;
                    }
                    switch (face) {
                        case 0: {
                            location.add(0.0, -1.0, 0.0);
                            break;
                        }
                        case 1: {
                            location.add(0.0, 1.0, 0.0);
                            break;
                        }
                        case 2: {
                            location.add(0.0, 0.0, -1.0);
                            break;
                        }
                        case 3: {
                            location.add(0.0, 0.0, 1.0);
                            break;
                        }
                        case 4: {
                            location.add(-1.0, 0.0, 0.0);
                            break;
                        }
                        case 5: {
                            location.add(1.0, 0.0, 0.0);
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    event.setCancelled(true);
                    ItemStack stack = (ItemStack)packet.getItemModifier().read(0);
                    if (stack != null && (stack.getType().isBlock() || isLiquidSource(stack.getType()))) {
                        player.setItemInHand(player.getItemInHand());
                    }
                    if ((visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location)) != null) {
                        VisualBlockData visualBlockData = visualBlock.getBlockData();
                        player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
                    }
                    else {
                        new BukkitRunnable() {
                            public void run() {
                                Block block = location.getBlock();
                                player.sendBlockChange(location, block.getType(), block.getData());
                            }
                        }.runTask((Plugin)hcf);
                    }
                }
                catch (FieldAccessException ex) {}
            }
        });
        protocolManager.addPacketListener((PacketListener)new PacketAdapter(hcf, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.BLOCK_DIG }) {
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier modifier = packet.getIntegers();
                Player player = event.getPlayer();
                try {
                    int status = (int)modifier.read(4);
                    if (status == 0 || status == 2) {
                        int x = (int)modifier.read(0);
                        int y = (int)modifier.read(1);
                        int z = (int)modifier.read(2);
                        Location location = new Location(player.getWorld(), (double)x, (double)y, (double)z);
                        VisualBlock visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location);
                        if (visualBlock == null) {
                            return;
                        }
                        event.setCancelled(true);
                        VisualBlockData data = visualBlock.getBlockData();
                        if (status == 2) {
                            player.sendBlockChange(location, data.getBlockType(), data.getData());
                        }
                        else if (status == 0) {
                            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
                            if (player.getGameMode() == GameMode.CREATIVE || net.minecraft.server.v1_7_R4.Block.getById(data.getItemTypeId()).getDamage((EntityHuman)entityPlayer, entityPlayer.world, x, y, z) > 1.0f) {
                                player.sendBlockChange(location, data.getBlockType(), data.getData());
                            }
                        }
                    }
                }
                catch (FieldAccessException ex) {}
            }
        });
    }
    
    private static boolean isLiquidSource(Material material) {
        switch (material) {
            case LAVA_BUCKET:
            case WATER_BUCKET: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
