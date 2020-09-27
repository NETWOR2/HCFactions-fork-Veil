package net.bfcode.bfhcf.scoreboard;

import javax.annotation.Nonnull;

@Nonnull
public class SidebarEntry
{
    public String name;
    public String prefix;
    public String suffix;
    
    public SidebarEntry(String name) {
        this.name = name;
    }
    
    public SidebarEntry(Object name) {
        this.name = String.valueOf(name);
    }
    
    public SidebarEntry(String prefix, String name, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    public SidebarEntry(Object prefix, Object name, Object suffix) {
        this(name);
        this.prefix = String.valueOf(prefix);
        this.suffix = String.valueOf(suffix);
    }
}
