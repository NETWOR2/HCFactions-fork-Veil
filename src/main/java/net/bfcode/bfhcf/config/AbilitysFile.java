package net.bfcode.bfhcf.config;

import java.io.File;

import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class AbilitysFile extends YamlConfiguration {
    private static AbilitysFile config;
    private Plugin plugin;
    private File configFile;
    
    public static AbilitysFile getConfig() {
        if (AbilitysFile.config == null) {
            AbilitysFile.config = new AbilitysFile();
        }
        return AbilitysFile.config;
    }
    
    private Plugin main() {
        return HCFaction.getPlugin();
    }
    
    public AbilitysFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "abilitys.yml");
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
        this.plugin.saveResource("abilitys.yml", false);
    }
    
    public static void saveConfig() {
    	AbilitysFile.config.save();
    	AbilitysFile.config.reload();
    }
}
