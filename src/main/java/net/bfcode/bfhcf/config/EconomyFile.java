package net.bfcode.bfhcf.config;

import java.io.File;

import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class EconomyFile extends YamlConfiguration {
    private static EconomyFile config;
    private Plugin plugin;
    private File configFile;
    
    public static EconomyFile getConfig() {
        if (EconomyFile.config == null) {
            EconomyFile.config = new EconomyFile();
        }
        return EconomyFile.config;
    }
    
    private Plugin main() {
        return HCFaction.getPlugin();
    }
    
    public EconomyFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "balances.yml");
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
        this.plugin.saveResource("balances.yml", false);
    }
    
    public static void saveConfig() {
    	EconomyFile.config.save();
    	EconomyFile.config.reload();
    }
}
