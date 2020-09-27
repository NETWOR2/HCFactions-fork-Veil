package net.bfcode.bfhcf.utils;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class ClickableText {

	public static void sendClickableMessage(ComponentBuilder componentBuilder) {
		Bukkit.spigot().broadcast(componentBuilder.create());
	}
	
	public static HoverEvent createHover(String message) {
		ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', message));
		
		HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, componentBuilder.create());
		
		return hover;
	}
	
	public static ClickEvent createClick(String command) {
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
		
		return click;
	}
}
