package net.bfcode.bfhcf.destroythecore;

import java.io.File;
import org.bukkit.plugin.Plugin;

import net.bfcode.bfhcf.HCFaction;

import org.bukkit.configuration.file.YamlConfiguration;

public class DTCFile extends YamlConfiguration {
	private static DTCFile config;
	private Plugin plugin;
	private File configFile;

	public static DTCFile getConfig() {
		if (DTCFile.config == null) {
			DTCFile.config = new DTCFile();
		}
		return DTCFile.config;
	}

	private Plugin main() {
		return (Plugin) HCFaction.getPlugin();
	}

	public DTCFile() {
		this.plugin = this.main();
		this.configFile = new File(this.plugin.getDataFolder(), "destroy-the-core.yml");
		this.saveDefault();
		this.reload();
	}

	public void reload() {
		try {
			super.load(this.configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			super.save(this.configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveDefault() {
		this.plugin.saveResource("destroy-the-core.yml", false);
	}
}
