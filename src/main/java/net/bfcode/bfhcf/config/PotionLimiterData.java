package net.bfcode.bfhcf.config;

import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class PotionLimiterData
{
    static PotionLimiterData instance;
    Plugin p;
    FileConfiguration Data;
    File Datafile;
    
    public static PotionLimiterData getInstance() {
        return PotionLimiterData.instance;
    }
    
    public void setup(Plugin p) {
        this.p = p;
        this.Datafile = new File(p.getDataFolder(), "potion-limiter.yml");
        this.Data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.Datafile);
        if (!this.Datafile.exists()) {
            try {
                this.Datafile.createNewFile();
                PrintWriter writer = new PrintWriter(new FileWriter(this.Datafile));
                writer.println("#Remove '#/' for disabled potion."
                		+ "\ndisabled-potions:"
                		+ "\n  - 8201  #Strength 3:00."
                		+ "\n  - 8265  #Strength 8:00."
                		+ "\n  - 8233  #Strength II 1:30."
                		+ "\n  - 16393 #Strength Splash 2:15."
                		+ "\n  - 16457 #Strength Splash 6:00."
                		+ "\n  - 16425 #Strength Splash II 1:07."
                		+ "\n  - 8193  #Regeneration 0:45."
                		+ "\n  - 8257  #Regeneration 2:00."
                		+ "\n  - 8225  #Regeneration II 0:22."
                		+ "\n  - 16385 #Regeneration Splash 0:33."
                		+ "\n  - 16449 #Regeneration Splash 1:30."
                		+ "\n  - 16417 #Regeneration Splash II 0:16."
                		+ "\n  - 8232  #Weakness 1:30."
                		+ "\n  - 8264  #Weakness 4:00."
                		+ "\n  - 16424 #Weakness Splash 1:07."
                		+ "\n  - 16456 #Weakness Splash 3:00."
                		+ "\n  - 8268  #Instant Damage."
                		+ "\n  - 8236  #Instant Damage II."
                		+ "\n  - 16460 #Instant Damage Splash."
                		+ "\n  - 16428 #Instant Damage Splash II."
                		+ "\n  - 8237  #Water Breathing 3:00."
                		+ "\n  - 8269  #Water Breathing 8:00."
                		+ "\n  - 16429 #Water Breathing Splash 2:15."
                		+ "\n  - 16461 #Water Breathing Splash 6:00.");
                writer.flush();
                writer.close();
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
        PotionLimiterData.instance = new PotionLimiterData();
    }
}
