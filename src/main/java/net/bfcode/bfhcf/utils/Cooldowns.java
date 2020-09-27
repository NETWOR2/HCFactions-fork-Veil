package net.bfcode.bfhcf.utils;

import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;

public class Cooldowns
{
    private static HashMap<String, HashMap<UUID, Long>> cooldown;
    
    public static void createCooldown(String k) {
        if (Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }
        Cooldowns.cooldown.put(k, new HashMap<UUID, Long>());
    }
    
    public static HashMap<UUID, Long> getCooldownMap(String k) {
        if (Cooldowns.cooldown.containsKey(k)) {
            return Cooldowns.cooldown.get(k);
        }
        return null;
    }
    
    public static void addCooldown(String k, Player p, int seconds) {
        if (!Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        long next = System.currentTimeMillis() + seconds * 1000L;
        Cooldowns.cooldown.get(k).put(p.getUniqueId(), next);
    }
    
    public static boolean isOnCooldown(String k, Player p) {
        return Cooldowns.cooldown.containsKey(k) && Cooldowns.cooldown.get(k).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= Cooldowns.cooldown.get(k).get(p.getUniqueId());
    }
    
    public static int getCooldownForPlayerInt(String k, Player p) {
        return (int)(Cooldowns.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }
    
    public static long getCooldownForPlayerLong(String k, Player p) {
        return Cooldowns.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis();
    }
    
    public static void removeCooldown(String k, Player p) {
        if (!Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        Cooldowns.cooldown.get(k).remove(p.getUniqueId());
    }
    
    static {
        Cooldowns.cooldown = new HashMap<String, HashMap<UUID, Long>>();
    }
}
