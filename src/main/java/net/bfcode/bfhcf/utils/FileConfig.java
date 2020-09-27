package net.bfcode.bfhcf.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.bfcode.bfhcf.HCFaction;

public class FileConfig
{
    public String name;
    public File file;
    private FileConfiguration config;
    
    public FileConfig(String name) {
        this.name = name;
        this.file = new File(HCFaction.getPlugin().getDataFolder(), name);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (HCFaction.getPlugin().getResource(name) == null) {
                try {
                    this.file.createNewFile();
                }
                catch (IOException e) {
                	HCFaction.getPlugin().getLogger().severe("Failed to create new file " + name);
                }
            }
            else {
            	HCFaction.getPlugin().saveResource(name, false);
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
    }
    
    public FileConfig(File file, String fileName) {
        this.file = new File(file, fileName);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (HCFaction.getPlugin().getResource(fileName) == null) {
                try {
                    this.file.createNewFile();
                }
                catch (IOException e) {
                	HCFaction.getPlugin().getLogger().severe("Failed to create new file " + fileName);
                }
            }
            else {
            	HCFaction.getPlugin().saveResource(fileName, false);
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    @SuppressWarnings("null")
	public void save() {
        try {
            this.getConfig().save(this.file);
            this.getConfig().load(this.file);
        }
        catch (IOException | InvalidConfigurationException ex2) {
            Exception ex = null;
            Exception e = ex;
            Bukkit.getLogger().severe("Could not save config file " + this.file.toString());
            e.printStackTrace();
        }
    }
}
