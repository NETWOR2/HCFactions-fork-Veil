package net.bfcode.bfhcf.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

public class DailyFile extends YamlConfiguration {
    private static DailyFile config;
    private Plugin plugin;
    private File configFile;
    
    public static DailyFile getConfig() {
        if (DailyFile.config == null) {
        	DailyFile.config = new DailyFile();
        }
        return DailyFile.config;
    }
    
    private Plugin main() {
        return HCFaction.getPlugin();
    }
    
    public DailyFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "daily.yml");
        this.saveDefault();
        this.reload();
    }
    
    public void reload() {
        try {
            super.load(this.configFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            super.save(this.configFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveDefault() {
        this.plugin.saveResource("daily.yml", false);
    }
}
