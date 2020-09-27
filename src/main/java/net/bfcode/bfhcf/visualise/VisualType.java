package net.bfcode.bfhcf.visualise;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.type.Faction;

public enum VisualType
{
    SPAWN_BORDER {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(Player player, Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    }, 
    CLAIM_BORDER {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(Player player, Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    }, 
    SUBCLAIM_MAP {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(Player player, Location location) {
                    return new VisualBlockData(Material.LOG, (byte)1);
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    }, 
    CLAIM_MAP {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                private Material[] types = { Material.SNOW_BLOCK, Material.SANDSTONE, Material.FURNACE, Material.NETHERRACK, Material.GLOWSTONE, Material.LAPIS_BLOCK, Material.NETHER_BRICK, Material.DIAMOND_ORE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE };
                private int materialCounter = 0;
                
                @Override
                VisualBlockData generate(Player player, Location location) {
                    int y = location.getBlockY();
                    if (y == 0 || y % 3 == 0) {
                        return new VisualBlockData(this.types[this.materialCounter]);
                    }
                    Faction faction = HCFaction.getPlugin().getFactionManager().getFactionAt(location);
                    return new VisualBlockData(Material.STAINED_GLASS, ((faction != null) ? faction.getRelation((CommandSender)player) : Relation.ENEMY).toDyeColour().getData());
                }
                
                @Override
                ArrayList<VisualBlockData> bulkGenerate(Player player, Iterable<Location> locations) {
                    ArrayList<VisualBlockData> result = super.bulkGenerate(player, locations);
                    if (++this.materialCounter == this.types.length) {
                        this.materialCounter = 0;
                    }
                    return result;
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    }, 
    CREATE_CLAIM_SELECTION {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(Player player, Location location) {
                    return new VisualBlockData((location.getBlockY() % 3 != 0) ? Material.GLASS : Material.DIAMOND_BLOCK);
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    }, 
    WORLD_BORDER {
        private BlockFiller blockFiller;
        
        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(Player player, Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }
        
        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    };
    
    abstract BlockFiller blockFiller();
}
