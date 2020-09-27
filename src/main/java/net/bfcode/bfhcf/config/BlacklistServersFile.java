package net.bfcode.bfhcf.config;

import java.io.File;

import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class BlacklistServersFile extends YamlConfiguration {
    private static BlacklistServersFile config;
    private Plugin plugin;
    private File configFile;
    
    public static BlacklistServersFile getConfig() {
        if (BlacklistServersFile.config == null) {
            BlacklistServersFile.config = new BlacklistServersFile();
        }
        return BlacklistServersFile.config;
    }
    
    private Plugin main() {
        return HCFaction.getPlugin();
    }
    
    public BlacklistServersFile() {
        this.plugin = this.main();
        this.configFile = new File(this.plugin.getDataFolder(), "blacklist-servers.yml");
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
        this.plugin.saveResource("blacklist-servers.yml", false);
    }
    
    public static void saveConfig() {
    	BlacklistServersFile.config.save();
    	BlacklistServersFile.config.reload();
    }
}
