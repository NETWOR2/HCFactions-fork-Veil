package net.bfcode.bfhcf.utils;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class CC {
	
    public static String BLUE;
    public static String AQUA;
    public static String YELLOW;
    public static String RED;
    public static String GRAY;
    public static String GOLD;
    public static String GREEN;
    public static String WHITE;
    public static String BLACK;
    public static String BOLD;
    public static String ITALIC;
    public static String UNDER_LINE;
    public static String STRIKE_THROUGH;
    public static String RESET;
    public static String MAGIC;
    public static String DARK_BLUE;
    public static String DARK_AQUA;
    public static String DARK_GRAY;
    public static String DARK_GREEN;
    public static String DARK_PURPLE;
    public static String DARK_RED;
    public static String PINK;
    public static String MENU_BAR;
    public static String CHAT_BAR;
    public static String SB_BAR;
    
    public static ChatColor fromBukkit(ChatColor chatColor){
        return ChatColor.values()[chatColor.ordinal()];
    }
    
    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }
    
    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList<String>();
        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return toReturn;
    }
    
    public static List<String> translate(String[] lines) {
        List<String> toReturn = new ArrayList<String>();
        for (String line : lines) {
            if (line != null) {
                toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        return toReturn;
    }
    
    static {
        BLUE = ChatColor.BLUE.toString();
        AQUA = ChatColor.AQUA.toString();
        YELLOW = ChatColor.YELLOW.toString();
        RED = ChatColor.RED.toString();
        GRAY = ChatColor.GRAY.toString();
        GOLD = ChatColor.GOLD.toString();
        GREEN = ChatColor.GREEN.toString();
        WHITE = ChatColor.WHITE.toString();
        BLACK = ChatColor.BLACK.toString();
        BOLD = ChatColor.BOLD.toString();
        ITALIC = ChatColor.ITALIC.toString();
        UNDER_LINE = ChatColor.UNDERLINE.toString();
        STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
        RESET = ChatColor.RESET.toString();
        MAGIC = ChatColor.MAGIC.toString();
        DARK_BLUE = ChatColor.DARK_BLUE.toString();
        DARK_AQUA = ChatColor.DARK_AQUA.toString();
        DARK_GRAY = ChatColor.DARK_GRAY.toString();
        DARK_GREEN = ChatColor.DARK_GREEN.toString();
        DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
        DARK_RED = ChatColor.DARK_RED.toString();
        PINK = ChatColor.LIGHT_PURPLE.toString();
        MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
        CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
        SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";
    }

	public static String translate(String string, String string2, String string3) {
		return ChatColor.translateAlternateColorCodes('&', string + string2 + string3);
	}

	public static String translate(String string, String string2, int balance) {
		return ChatColor.translateAlternateColorCodes('&', string + string2 + balance);
	}
}
