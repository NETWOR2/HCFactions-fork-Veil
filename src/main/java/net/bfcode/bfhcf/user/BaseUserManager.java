package net.bfcode.bfhcf.user;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

import net.bfcode.bfhcf.HCFaction;

public class BaseUserManager{
	private ConsoleUser console;
	private JavaPlugin plugin;
	private Config userConfig;
	private Map<UUID, ServerParticipator> participators;

	public BaseUserManager(HCFaction plugin){
		this.plugin = plugin;
		this.reloadParticipatorData();
		ServerParticipator participator = this.participators.get(ConsoleUser.CONSOLE_UUID);
		if(participator != null){
			this.console = (ConsoleUser) participator;
		}else{
			this.participators.put(ConsoleUser.CONSOLE_UUID, this.console = new ConsoleUser());
		}
	}

	public ConsoleUser getConsole(){
		return this.console;
	}

	public Map<UUID, ServerParticipator> getParticipators(){
		return this.participators;
	}

	public ServerParticipator getParticipator(CommandSender sender){
		Preconditions.checkNotNull((Object) sender, "CommandSender cannot be null");
		if(sender instanceof ConsoleCommandSender){
			return this.console;
		}
		if(sender instanceof Player){
			return this.participators.get(((Player) sender).getUniqueId());
		}
		return null;
	}

	public ServerParticipator getParticipator(UUID uuid){
		Preconditions.checkNotNull((Object) uuid, "Unique ID cannot be null");
		return this.participators.get(uuid);
	}

	public BaseUser getUser(UUID uuid){
		ServerParticipator participator = this.getParticipator(uuid);
		BaseUser baseUser;
		if(participator != null && participator instanceof BaseUser){
			baseUser = (BaseUser) participator;
		}else{
			this.participators.put(uuid, baseUser = new BaseUser(uuid));
		}
		return baseUser;
	}

	public void reloadParticipatorData(){
		this.userConfig = new Config(this.plugin, "participators");
		Object object = this.userConfig.get("participators");
		if(object instanceof MemorySection){
			MemorySection section = (MemorySection) object;
			Set<String> keys = section.getKeys(false);
			this.participators = new HashMap<UUID, ServerParticipator>(keys.size());
			for(String id : keys){
				this.participators.put(UUID.fromString(id), (ServerParticipator) this.userConfig.get("participators." + id));
			}
		}else{
			this.participators = new HashMap<UUID, ServerParticipator>();
		}
	}

	public void saveParticipatorData(){
		Map<String, ServerParticipator> saveMap = new LinkedHashMap<String, ServerParticipator>(this.participators.size());
		for(Map.Entry<UUID, ServerParticipator> entry : this.participators.entrySet()){
			saveMap.put(entry.getKey().toString(), entry.getValue());
		}
		this.userConfig.set("participators", saveMap);
		this.userConfig.save();
	}
}
