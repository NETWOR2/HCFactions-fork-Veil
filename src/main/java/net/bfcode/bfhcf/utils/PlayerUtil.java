package net.bfcode.bfhcf.utils;

import net.bfcode.bfhcf.utils.playerversion.PlayerVersion;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import org.bukkit.scoreboard.*;

import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.org.apache.commons.io.output.ByteArrayOutputStream;

public class PlayerUtil
{
    private PlayerUtil() {
    }
    
    public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
            ItemStack itemStack2 = player.getInventory().getContents()[i];
            if (itemStack2 == null || itemStack2.getType() == type || itemStack2.getType() == Material.AIR) {
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }
    
    public static void sendToHub(Player player) {
	    ArrayList lobbys = new ArrayList();
	    lobbys.add("hub");
	    lobbys.add("lobby");

	    Random random = new Random();
	    String lobby = (String) lobbys.get(random.nextInt(lobbys.toArray().length));
	    teleport(player, lobby);
    }
    
    public static void teleport(Player pl, String input) {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    DataOutputStream out = new DataOutputStream(b);
    
	    try {
	        out.writeUTF("Connect");
	        out.writeUTF(input);
	    } catch (IOException localIOException) {
	        localIOException.printStackTrace();
	    }

	    pl.sendPluginMessage(HCFaction.getPlugin(), "BungeeCord", b.toByteArray());
    }
    
    public static void denyMovement(Player player) {
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
        player.setFoodLevel(1);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 200));
    }
    
    public static void allowMovement(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
    
    public static void startingSumo(Player player) {
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255));
    }
    
    public static void fightingSumo(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.SLOW);
    }
    
    public static void clearPlayer(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(12.8f);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setWalkSpeed(0.2f);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.updateInventory();
    }
    
    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
    
    public static void sendMessage(String message, Set<Player> players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sendFirework(FireworkEffect effect, Location location) {
		Firework f = (Firework)location.getWorld().spawn(location, (Class)Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);
        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle", (Class<?>[])new Class[0]);
            Object entityFirework = handle.invoke(firework, new Object[0]);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
    
    @SuppressWarnings("unused")
	private static Team getTeam(Scoreboard board, String prefix, String color) {
        Team team = board.getTeam(prefix);
        if (team == null) {
            team = board.registerNewTeam(prefix);
            team.setPrefix(CC.translate(color));
        }
        return team;
    }
    
    public static void unregister(Scoreboard board, String name) {
        Team team = board.getTeam(name);
        if (team != null) {
            team.unregister();
        }
    }

    public static PlayerVersion getPlayerVersion(Player player) {
        return net.bfcode.bfhcf.utils.playerversion.PlayerVersionHandler.version.getPlayerVersion(player);
    }
}
