package net.bfcode.bfhcf.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

public class UserDataFile extends YamlConfiguration {
    private static UserDataFile config;
    private Plugin plugin;
    private File configFile;
    
    public static UserDataFile getConfig() {
        if (UserDataFile.config == null) {
        	UserDataFile.config = new UserDataFile();
        }
        return UserDataFile.config;
    }
    
    private Plugin main() {
        return HCFaction.getPlugin();
    }
    
    public UserDataFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "user-data.yml");
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
        this.plugin.saveResource("user-data.yml", false);
    }
}
