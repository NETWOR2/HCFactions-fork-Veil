package net.bfcode.bfhcf.utils;

import java.io.*;
import org.bukkit.entity.*;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.*;
import org.bukkit.configuration.file.*;

public class FileUtils
{
    public static File file;
    public static FileConfiguration cfg;
    
    public static void setMainLobby(Player p) {
        FileUtils.cfg.set("main-lobby.world", p.getLocation().getWorld().getName());
        FileUtils.cfg.set("main-lobby.x", p.getLocation().getX());
        FileUtils.cfg.set("main-lobby.y", p.getLocation().getY());
        FileUtils.cfg.set("main-lobby.z", p.getLocation().getZ());
        saveCfg();
    }
    
    public static void setLobby(Player p) {
        FileUtils.cfg.set("lobby.world", p.getLocation().getWorld().getName());
        FileUtils.cfg.set("lobby.x", p.getLocation().getX());
        FileUtils.cfg.set("lobby.y", p.getLocation().getY());
        FileUtils.cfg.set("lobby.z", p.getLocation().getZ());
        saveCfg();
    }
    
    public static void setPreGame(Player p) {
        FileUtils.cfg.set("pregame.world", p.getLocation().getWorld().getName());
        FileUtils.cfg.set("pregame.x", p.getLocation().getX());
        FileUtils.cfg.set("pregame.y", p.getLocation().getY());
        FileUtils.cfg.set("pregame.z", p.getLocation().getZ());
        saveCfg();
    }
    
    public static void setFirstSpawn(Player p) {
        FileUtils.cfg.set("game.spawn1.world", p.getLocation().getWorld().getName());
        FileUtils.cfg.set("game.spawn1.x", p.getLocation().getX());
        FileUtils.cfg.set("game.spawn1.y", p.getLocation().getY());
        FileUtils.cfg.set("game.spawn1.z", p.getLocation().getZ());
        FileUtils.cfg.set("game.spawn1.yaw", p.getLocation().getYaw());
        FileUtils.cfg.set("game.spawn1.pitch", p.getLocation().getPitch());
        saveCfg();
    }
    
    public static void setSecondSpawn(Player p) {
        FileUtils.cfg.set("game.spawn2.world", p.getLocation().getWorld().getName());
        FileUtils.cfg.set("game.spawn2.x", p.getLocation().getX());
        FileUtils.cfg.set("game.spawn2.y", p.getLocation().getY());
        FileUtils.cfg.set("game.spawn2.z", p.getLocation().getZ());
        FileUtils.cfg.set("game.spawn2.yaw", p.getLocation().getYaw());
        FileUtils.cfg.set("game.spawn2.pitch", p.getLocation().getPitch());
        saveCfg();
    }
    
    public static Location getMainLobby() {
        World w = Bukkit.getWorld(FileUtils.cfg.getString("main-lobby.world"));
        double x = FileUtils.cfg.getDouble("main-lobby.x");
        double y = FileUtils.cfg.getDouble("main-lobby.y");
        double z = FileUtils.cfg.getDouble("main-lobby.z");
        return new Location(w, x, y, z);
    }
    
    public static Location getLobby() {
        World w = Bukkit.getWorld(FileUtils.cfg.getString("lobby.world"));
        double x = FileUtils.cfg.getDouble("lobby.x");
        double y = FileUtils.cfg.getDouble("lobby.y");
        double z = FileUtils.cfg.getDouble("lobby.z");
        return new Location(w, x, y, z);
    }
    
    public static Location getPreGame() {
        World w = Bukkit.getWorld(FileUtils.cfg.getString("pregame.world"));
        double x = FileUtils.cfg.getDouble("pregame.x");
        double y = FileUtils.cfg.getDouble("pregame.y");
        double z = FileUtils.cfg.getDouble("pregame.z");
        return new Location(w, x, y, z);
    }
    
    public static void getFirstSpawn(Player p) {
        World w = Bukkit.getWorld(FileUtils.cfg.getString("game.spawn1.world"));
        double x = FileUtils.cfg.getDouble("game.spawn1.x");
        double y = FileUtils.cfg.getDouble("game.spawn1.y");
        double z = FileUtils.cfg.getDouble("game.spawn1.z");
        float yaw = (float)FileUtils.cfg.getDouble("game.spawn1.yaw");
        float pitch = (float)FileUtils.cfg.getDouble("game.spawn1.pitch");
        Location loc = new Location(w, x, y, z, yaw, pitch);
        p.teleport(loc);
    }
    
    public static void getSecondSpawn(Player p) {
        World w = Bukkit.getWorld(FileUtils.cfg.getString("game.spawn2.world"));
        double x = FileUtils.cfg.getDouble("game.spawn2.x");
        double y = FileUtils.cfg.getDouble("game.spawn2.y");
        double z = FileUtils.cfg.getDouble("game.spawn2.z");
        float yaw = (float)FileUtils.cfg.getDouble("game.spawn2.yaw");
        float pitch = (float)FileUtils.cfg.getDouble("game.spawn2.pitch");
        Location loc = new Location(w, x, y, z, yaw, pitch);
        p.teleport(loc);
    }
    
    public static void saveCfg() {
        try {
            FileUtils.cfg.save(FileUtils.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        FileUtils.file = new File(HCFaction.getPlugin().getDataFolder().getPath(), "location.yml");
        FileUtils.cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(FileUtils.file);
    }
}
