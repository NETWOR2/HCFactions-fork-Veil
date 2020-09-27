package net.bfcode.bfhcf.timer;

import org.bukkit.ChatColor;

import net.bfcode.bfhcf.user.Config;

public abstract class Timer
{
    protected String name;
    protected long defaultCooldown;
    
    public Timer(String name, long defaultCooldown) {
        if (name == null) {
            throw new IllegalStateException("Can not create timer with a null name");
        }
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }
    
    public abstract ChatColor getScoreboardPrefix();
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return this.getScoreboardPrefix() + this.name;
    }
    
    public void load(Config config) {
    }
    
    public void onDisable(Config config) {
    }
}
