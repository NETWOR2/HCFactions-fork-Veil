package net.bfcode.bfhcf.utils;

import net.bfcode.bfhcf.HCFaction;

public class Handler
{
    private HCFaction instance;
    
    public Handler(HCFaction instance) {
        this.instance = instance;
    }
    
    public void enable() {
    }
    
    public void disable() {
    }
    
    public HCFaction getInstance() {
        return this.instance;
    }
}
