package net.bfcode.bfhcf.faction.struct;

import org.bukkit.ChatColor;

public enum RegenStatus
{
    FULL(ChatColor.GREEN.toString() + ' '), 
    REGENERATING(ChatColor.GOLD.toString() + '^'), 
    PAUSED(ChatColor.RED.toString() + '<');
    
    private String symbol;
    
    private RegenStatus(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
}
