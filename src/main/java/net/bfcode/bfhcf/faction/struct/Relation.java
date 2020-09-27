package net.bfcode.bfhcf.faction.struct;

import org.bukkit.DyeColor;

import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfhcf.utils.ConfigurationService;

import org.bukkit.ChatColor;

public enum Relation
{
    MEMBER(3), 
    ALLY(2), 
    ENEMY(1);
    
    private int value;
    
    private Relation(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }
    
    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }
    
    public boolean isMember() {
        return this == Relation.MEMBER;
    }
    
    public boolean isAlly() {
        return this == Relation.ALLY;
    }
    
    public boolean isEnemy() {
        return this == Relation.ENEMY;
    }
    
    public String getDisplayName() {
        switch (this) {
            case ALLY: {
                return this.toChatColour() + "alliance";
            }
            default: {
                return this.toChatColour() + this.name().toLowerCase();
            }
        }
    }
    
    public ChatColor toChatColour() {
        switch (this) {
            case MEMBER: {
                return ConfigurationService.TEAMMATE_COLOUR;
            }
            case ALLY: {
                return ConfigurationService.ALLY_COLOUR;
            }
            default: {
                return ConfigurationService.ENEMY_COLOUR;
            }
        }
    }
    
    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(this.toChatColour());
    }
}
