package net.bfcode.bfhcf.config;

import java.io.File;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerDataFile extends YamlConfiguration {
    private static PlayerDataFile config;
    private Plugin plugin;
    private File configFile;
    
    public static PlayerDataFile getConfig() {
        if (PlayerDataFile.config == null) {
            PlayerDataFile.config = new PlayerDataFile();
        }
        return PlayerDataFile.config;
    }
    
    private Plugin main() {
        return (Plugin)HCFaction.getPlugin();
    }
    
    public PlayerDataFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "player-data.yml");
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
        this.plugin.saveResource("player-data.yml", false);
    }
}
