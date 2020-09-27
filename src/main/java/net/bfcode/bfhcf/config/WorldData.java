package net.bfcode.bfhcf.config;

import org.bukkit.plugin.PluginDescriptionFile;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class WorldData
{
    static WorldData instance;
    Plugin p;
    FileConfiguration Data;
    File Datafile;
    
    public static WorldData getInstance() {
        return WorldData.instance;
    }
    
    public void setup(Plugin p) {
        this.p = p;
        this.Datafile = new File(p.getDataFolder(), "world-config.yml");
        this.Data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.Datafile);
        if (!this.Datafile.exists()) {
            try {
                this.Datafile.createNewFile();
            }
            catch (IOException ex) {}
        }
    }
    
    public FileConfiguration getConfig() {
        return this.Data;
    }
    
    public void saveConfig() {
        try {
            this.Data.save(this.Datafile);
        }
        catch (IOException ex) {}
    }
    
    public PluginDescriptionFile getDescription() {
        return this.p.getDescription();
    }
    
    public void reloadConfig() {
        this.Data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.Datafile);
    }
    
    static {
        WorldData.instance = new WorldData();
    }
}
