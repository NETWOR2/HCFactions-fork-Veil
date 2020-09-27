package net.bfcode.bfhcf.classes.bard;

import org.bukkit.potion.PotionEffect;

public class BardEffect
{
    public int energyCost;
    public PotionEffect clickable;
    public PotionEffect heldable;
    
    public BardEffect(int energyCost, PotionEffect clickable, PotionEffect heldable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
        this.heldable = heldable;
    }
}
