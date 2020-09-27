package net.bfcode.bfhcf.utils;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;
import java.util.Iterator;
import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;

public class StringUtils
{
    public static String formatMilisecondsToSeconds(Long time) {
        float seconds = (time + 0.0f) / 1000.0f;
        String string = String.format("%1$.1f", seconds);
        return string;
    }
    
    public static String formatMilisecondsToMinutes(Long time) {
        int seconds = (int)(time / 1000L % 60L);
        int minutes = (int)(time / 1000L / 60L);
        String string = String.format("%02d:%02d", minutes, seconds);
        return string;
    }
    
    public static String formatSecondsToMinutes(int time) {
        int seconds = time % 60;
        int minutes = time / 60;
        String string = String.format("%02d:%02d", minutes, seconds);
        return string;
    }
    
    public static String formatSecondsToHours(int time) {
        int hours = time / 3600;
        int minutes = time % 3600 / 60;
        int seconds = time % 60;
        String string = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return string;
    }
    
    public static String formatMinutes(int time) {
        int minutes = time / 60;
        String string = String.format("%02d", minutes);
        return string;
    }
    
    public static String formatInt(int i) {
        int r = i * 1000;
        int sec = r / 1000 % 60;
        int min = r / 60000 % 60;
        int h = r / 3600000 % 24;
        return String.valueOf((h > 0) ? new StringBuilder(String.valueOf(h)).append(":").toString() : "") + ((min < 10) ? ("0" + min) : min) + ":" + ((sec < 10) ? ("0" + sec) : sec);
    }
    
    public static String getEffectNamesList(ArrayList<PotionEffect> effects) {
        StringBuilder names = new StringBuilder();
        for (PotionEffect effect : effects) {
            names.append(getPotionEffectName(effect.getType())).append(", ");
        }
        if (names.length() != 0) {
            names.delete(names.length() - 2, names.length());
        }
        return names.toString();
    }
    
    public static String getPotionEffectName(PotionEffectType type) {
        String name;
        switch (name = type.getName()) {
            case "POISON": {
                return "Poison";
            }
            case "NIGHT_VISION": {
                return "Night Vision";
            }
            case "SLOW_DIGGING": {
                return "Slow Digging";
            }
            case "WITHER": {
                return "Wither";
            }
            case "INCREASE_DAMAGE": {
                return "Strength";
            }
            case "BLINDNESS": {
                return "Blindness";
            }
            case "WATER_BREATHING": {
                return "Water Breathing";
            }
            case "REGENERATION": {
                return "Regeneration";
            }
            case "ABSORPTION": {
                return "Absorption";
            }
            case "FAST_DIGGING": {
                return "Haste";
            }
            case "HEALTH_BOOST": {
                return "Health Boost";
            }
            case "HARM": {
                return "Instant Damage";
            }
            case "HEAL": {
                return "Instant Health";
            }
            case "JUMP": {
                return "Jump";
            }
            case "SLOW": {
                return "Slowness";
            }
            case "WEAKNESS": {
                return "Weakness";
            }
            case "SPEED": {
                return "Speed";
            }
            case "SATURATION": {
                return "Saturation";
            }
            case "DAMAGE_RESISTANCE": {
                return "Resistance";
            }
            case "INVISIBILITY": {
                return "Invisibility";
            }
            case "FIRE_RESISTANCE": {
                return "Fire Resistance";
            }
            case "CONFUSION": {
                return "Confusion";
            }
            case "HUNGER": {
                return "Hunger";
            }
            default:
                break;
        }
        return "";
    }
    
    public static String getEntityName(Entity entity) {
        String name;
        switch (name = entity.getType().name()) {
            case "VILLAGER": {
                return "Villager";
            }
            case "PLAYER": {
                return "Player";
            }
            case "SILVERFISH": {
                return "Silverfish";
            }
            case "SPIDER": {
                return "Spider";
            }
            case "ENDERMAN": {
                return "Enderman";
            }
            case "WITHER": {
                return "Wither";
            }
            case "ZOMBIE": {
                return "Zombie";
            }
            case "SKELETON": {
                return "Skeleton";
            }
            case "PIG_ZOMBIE": {
                return "Pig Zombie";
            }
            case "IRON_GOLEM": {
                return "Iron Golem";
            }
            case "WOLF": {
                return "Wolf";
            }
            case "CAVE_SPIDER": {
                return "Cave Spider";
            }
            case "BLAZE": {
                return "Blaze";
            }
            case "SLIME": {
                return "Slime";
            }
            case "WITCH": {
                return "Witch";
            }
            case "MAGMA_CUBE": {
                return "Magma Cube";
            }
            case "CREEPER": {
                return "Creeper";
            }
            default:
                break;
        }
        return "";
    }
    
    public static String getWorldName(Location location) {
        String worldName = "";
        World world = location.getWorld();
        if (world.getEnvironment().equals((Object)World.Environment.NORMAL)) {
            worldName = "World";
        }
        else if (world.getEnvironment().equals((Object)World.Environment.NETHER)) {
            worldName = "Nether";
        }
        else if (world.getEnvironment().equals((Object)World.Environment.THE_END)) {
            worldName = "End";
        }
        else {
            worldName = world.getName();
        }
        return worldName;
    }
    
    public static Object getTime(int seconds) {
        if (seconds < 60) {
            return String.valueOf(seconds) + "s";
        }
        int minutes = seconds / 60;
        int s = 60 * minutes;
        int secondsLeft = seconds - s;
        if (minutes < 60) {
            if (secondsLeft > 0) {
                return String.valueOf(String.valueOf(minutes) + "m " + secondsLeft + "s");
            }
            return String.valueOf(String.valueOf(minutes) + "m");
        }
        else {
            if (minutes < 1440) {
                String time = "";
                int hours = minutes / 60;
                time = String.valueOf(hours) + "h";
                int inMins = 60 * hours;
                int leftOver = minutes - inMins;
                if (leftOver >= 1) {
                    time = String.valueOf(time) + " " + leftOver + "m";
                }
                if (secondsLeft > 0) {
                    time = String.valueOf(time) + " " + secondsLeft + "s";
                }
                return time;
            }
            String time = "";
            int days = minutes / 1440;
            time = String.valueOf(days) + "d";
            int inMins = 1440 * days;
            int leftOver = minutes - inMins;
            if (leftOver >= 1) {
                if (leftOver < 60) {
                    time = String.valueOf(time) + " " + leftOver + "m";
                }
                else {
                    int hours2 = leftOver / 60;
                    time = String.valueOf(time) + " " + hours2 + "h";
                    int hoursInMins = 60 * hours2;
                    int minsLeft = leftOver - hoursInMins;
                    if (leftOver >= 1) {
                        time = String.valueOf(time) + " " + minsLeft + "m";
                    }
                }
            }
            if (secondsLeft > 0) {
                time = String.valueOf(time) + " " + secondsLeft + "s";
            }
            return time;
        }
    }
}
