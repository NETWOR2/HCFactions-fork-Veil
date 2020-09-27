package net.bfcode.bfhcf.classes.effects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.PvpClass;
import net.bfcode.bfhcf.command.CustomEnchantCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;

public class ChestplateEffect extends PvpClass implements Listener {
	
    private HCFaction plugin;
    
    public ChestplateEffect(HCFaction plugin) {
        super("Chestplate", TimeUnit.MILLISECONDS.toMillis(1L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
    @Override
    public void onUnequip(Player player) {
    	if(!plugin.getTournamentManager().isInTournament(player)) {
	        super.onUnequip(player);
    	}
    }
    
    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack chestplate = playerInventory.getChestplate();
        List<String> lores = new ArrayList<String>();
        lores.add(CustomEnchantCommand.fire);
        lores.add(CustomEnchantCommand.speed);
        if(chestplate != null && chestplate.hasItemMeta() 
        		&& chestplate.getItemMeta().hasLore() && chestplate.getItemMeta().getLore().containsAll(lores)) {
        	return true;
        }
        return false;
    }
}
