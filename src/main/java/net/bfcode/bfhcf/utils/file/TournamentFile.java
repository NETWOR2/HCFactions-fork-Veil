package net.bfcode.bfhcf.utils.file;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

import net.bfcode.bfhcf.HCFaction;

import java.io.File;

public class TournamentFile
{
    private File file;
    private YamlConfiguration configuration;
    
    public TournamentFile() {
        this.file = new File(HCFaction.getPlugin().getDataFolder(), "tournament.yml");
        if (!this.file.exists()) {
            HCFaction.getPlugin().saveResource("tournament.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public void load() {
        this.file = new File(HCFaction.getPlugin().getDataFolder(), "tournament.yml");
        if (!this.file.exists()) {
            HCFaction.getPlugin().saveResource("tournament.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public void save() {
        try {
            this.configuration.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public double getDouble(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0;
    }
    
    public int getInt(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }
    
    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }
    
    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return "String at path: " + path + " not found!";
    }
    
    public long getLong(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getLong(path);
        }
        return 0L;
    }
    
    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<String>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Arrays.asList("String List at path: " + path + " not found!");
    }
}
