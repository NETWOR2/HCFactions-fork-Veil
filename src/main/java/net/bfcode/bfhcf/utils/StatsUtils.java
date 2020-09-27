package net.bfcode.bfhcf.utils;

import org.bukkit.entity.*;

import net.bfcode.bfhcf.HCFaction;

import java.io.*;
import org.bukkit.configuration.file.*;

public class StatsUtils
{
    public static void createPlayer(Player p) {
        File file = new File(HCFaction.getPlugin().getDataFolder().getPath(), "/PlayerStats/" + p.getName() + ".yml");
        FileConfiguration cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        if (file != null) {
            return;
        }
        try {
            cfg.set("wins", 0);
            cfg.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void addWins(Player p) {
        File file = new File(HCFaction.getPlugin().getDataFolder().getPath(), "/PlayerStats/" + p.getName() + ".yml");
        FileConfiguration cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        int wins = cfg.getInt("wins");
        ++wins;
        cfg.set("wins", wins);
    }
}
