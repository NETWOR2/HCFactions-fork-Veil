package net.bfcode.bfhcf.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;


public class ChatUtil {

private static char ALT_COLOUR_CODE = '&';
	
	// Static utility class -- cannot be created.
	public ChatUtil() { }

	public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes(ALT_COLOUR_CODE, string);
    }
    
    public static List<String> translate(Iterable<? extends String> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull).map(ChatUtil::translate).collect(Collectors.toList());
    }

    public static List<String> translate(List<String> list) {
        List<String> buffered = new ArrayList<String>();
        
        list.forEach(string -> buffered.add(translate(string)));
        return buffered;
    }

    public static String[] translate(String... strings) {
    	return translate(Arrays.asList(strings)).stream().toArray(String[]::new);
    }
    
    public static void sendMessage(Player receiver, String... strings) {
    	receiver.sendMessage(translate(strings));
    }
    
    public static void sendMessage(CommandSender commandSender, String... strings) {
    	commandSender.sendMessage(translate(strings));
    }
    
    public static void sendMessage(CommandSender commandSender, BaseComponent[] value) {
    	if(commandSender instanceof Player) {
    		((Player) commandSender).spigot().sendMessage(value);
    	} else {
    		commandSender.sendMessage(TextComponent.toLegacyText(value));
    	}
    }

    
    public static void broadcastConsole(String... strings) {
    	ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
    	consoleCommandSender.sendMessage(translate(strings));
    }

    public static void broadcast(String... strings) {
    	for(Player player : Bukkit.getOnlinePlayers()) {
    		sendMessage(player, strings);
    	}
    	
    	ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
    	consoleCommandSender.sendMessage(translate(strings));
    }
    
    public static void broadcastWithPermission(String permission, String... strings) {
    	for(Player player : Bukkit.getOnlinePlayers()) {
    		if(player.hasPermission(permission)) {
    			sendMessage(player, strings);
    		}
    	}
    	
    	ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
    	consoleCommandSender.sendMessage(translate(strings));
    }
    
    public static void broadcastWithPermission(String permission, BaseComponent[] strings) {
    	for(Player player : Bukkit.getOnlinePlayers()) {
    		if(player.hasPermission(permission)) {
    			sendMessage(player, strings);
    		}
    	}
    	
    	sendMessage(Bukkit.getConsoleSender(), strings);
    }
    
    public static void error(String... strings) {
    	Bukkit.getConsoleSender().sendMessage(translate("&4[ERROR]: &c" + strings));
    }
    
    public static void warn(String... strings) {
    	Bukkit.getConsoleSender().sendMessage(translate("&e[WARN]: &6" + strings));
    }
    
    public static void info(String... strings) {
    	Bukkit.getConsoleSender().sendMessage(translate("&2[INFO]: &a" + strings));
    }
	
    public static String formatMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static List<String> formatMessage(List<String> messages) {
        List<String> buffered = new ArrayList<String>();
        for (String message : messages){
            buffered.add(formatMessage("&r" + message));
        }
        return buffered;
    }
    
    public static String[] formatMessage(String... messages) {
    	return formatMessage(Arrays.asList(messages)).stream().toArray(String[]::new);
    }
    
    public static boolean endsWith(String word, List<String> suffix) {
		for(String message : suffix) {
			if(word.toLowerCase().endsWith(message)) {
				return true;
			}
		}
		return false;
	}
}
