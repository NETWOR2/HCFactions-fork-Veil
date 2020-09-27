package net.bfcode.bfhcf.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.bard.BardClass;
import net.bfcode.bfhcf.classes.effects.ChestplateEffect;
import net.bfcode.bfhcf.classes.event.PvpClassEquipEvent;
import net.bfcode.bfhcf.classes.event.PvpClassUnequipEvent;
import net.bfcode.bfhcf.classes.type.ArcherClass;
import net.bfcode.bfhcf.classes.type.AssassinClass;
import net.bfcode.bfhcf.classes.type.BombermanClass;
import net.bfcode.bfhcf.classes.type.HulkClass;
import net.bfcode.bfhcf.classes.type.MinerClass;
import net.bfcode.bfhcf.classes.type.RogueClass;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class PvpClassManager {
	
	private Map<UUID, PvpClass> equippedClass;
	private Map<String, PvpClass> pvpClasses;

	public PvpClassManager(HCFaction plugin) {
		this.equippedClass = new HashMap<UUID, PvpClass>();
		(this.pvpClasses = new HashMap<String, PvpClass>()).put("Archer", new ArcherClass(plugin));
		this.pvpClasses.put("Bard", new BardClass(plugin));
		this.pvpClasses.put("Miner", new MinerClass(plugin));
		this.pvpClasses.put("Rouge", new RogueClass(plugin));
		this.pvpClasses.put("Chestplate", new ChestplateEffect(plugin));
		if (ConfigurationService.KIT_MAP == true) {
			this.pvpClasses.put("Assassin", new AssassinClass(plugin));
			this.pvpClasses.put("Hulk", new HulkClass(plugin));
			this.pvpClasses.put("Bomberman", new BombermanClass(plugin));
		}
		for (PvpClass pvpClass : this.pvpClasses.values()) {
			if (!(pvpClass instanceof Listener)) {
				continue;
			}
			plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
		}
	}

	public void onDisable() {
		for (Map.Entry<UUID, PvpClass> entry : new HashMap<UUID, PvpClass>(this.equippedClass).entrySet()) {
			this.setEquippedClass(Bukkit.getPlayer((UUID) entry.getKey()), null);
		}
		this.pvpClasses.clear();
		this.equippedClass.clear();
	}

	public Collection<PvpClass> getPvpClasses() {
		return this.pvpClasses.values();
	}

	public PvpClass getPvpClass(String name) {
		return this.pvpClasses.get(name);
	}

	public PvpClass getEquippedClass(Player player) {
		Map<UUID, PvpClass> map = this.equippedClass;
		synchronized (map) {
			return this.equippedClass.get(player.getUniqueId());
		}
	}

	public boolean hasClassEquipped(Player player, PvpClass pvpClass) {
		PvpClass equipped = this.getEquippedClass(player);
		return equipped != null && equipped.equals(pvpClass);
	}

	public void setEquippedClass(Player player, @Nullable PvpClass pvpClass) {
		PvpClass equipped = this.getEquippedClass(player);
		if (equipped != null) {
			if (pvpClass == null) {
				this.equippedClass.remove(player.getUniqueId());
				equipped.onUnequip(player);
				Bukkit.getPluginManager().callEvent((Event) new PvpClassUnequipEvent(player, equipped));
				return;
			}
		} else if (pvpClass == null) {
			return;
		}
		if (pvpClass.onEquip(player)) {
			this.equippedClass.put(player.getUniqueId(), pvpClass);
			Bukkit.getPluginManager().callEvent((Event) new PvpClassEquipEvent(player, pvpClass));
		}
	}
}
