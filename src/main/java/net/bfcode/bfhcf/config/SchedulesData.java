package net.bfcode.bfhcf.config;

import org.bukkit.plugin.PluginDescriptionFile;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class SchedulesData
{
    static SchedulesData instance;
    Plugin p;
    FileConfiguration Data;
    File Datafile;
    
    public static SchedulesData getInstance() {
        return SchedulesData.instance;
    }
    
    public void setup(Plugin p) {
        this.p = p;
        this.Datafile = new File(p.getDataFolder(), "event-schedules.txt");
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
    	SchedulesData.instance = new SchedulesData();
    }
}
