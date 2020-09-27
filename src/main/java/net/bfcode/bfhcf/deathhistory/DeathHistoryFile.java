package net.bfcode.bfhcf.deathhistory;

import java.io.File;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class DeathHistoryFile extends YamlConfiguration {
	
    private static DeathHistoryFile config;
    private Plugin plugin;
    private File configFile;
    
    public static DeathHistoryFile getConfig() {
        if (DeathHistoryFile.config == null) {
            DeathHistoryFile.config = new DeathHistoryFile();
        }
        return DeathHistoryFile.config;
    }
    
    private Plugin main() {
        return (Plugin)HCFaction.getPlugin();
    }
    
    public DeathHistoryFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "death-history.yml");
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
        this.plugin.saveResource("death-history.yml", false);
    }
}
